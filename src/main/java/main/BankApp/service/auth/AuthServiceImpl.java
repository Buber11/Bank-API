package main.BankApp.service.auth;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import main.BankApp.common.Loggable;
import main.BankApp.dto.UserModel;
import main.BankApp.expection.DuplicateException;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.Account;
import main.BankApp.model.session.ActivityLogAction;
import main.BankApp.model.session.Session;
import main.BankApp.model.user.Role;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.model.user.UserPersonalData;
import main.BankApp.repository.UserPersonalDataRepository;
import main.BankApp.repository.UserRepository;
import main.BankApp.request.auth.LoginRequest;
import main.BankApp.request.auth.SignupRequest;
import main.BankApp.security.JwtService;
import main.BankApp.service.account.AccountService;
import main.BankApp.service.activityLog.ActivityLogService;
import main.BankApp.service.googleAuthenticator.GoogleAuthService;
import main.BankApp.service.hashing.HashingService;
import main.BankApp.service.rsa.RSAService;
import main.BankApp.service.session.SessionService;
import main.BankApp.service.user.UserModelAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final UserPersonalDataRepository userPersonalDataRepository;
    private final RSAService rsaService;
    private final HashingService hashingService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final ActivityLogService activityLogService;
    private final AccountService accountService;
    private final UserModelAssembly userModelAssembly;
    private final GoogleAuthService googleAuthService;

    @Override
    @Loggable
    public String signup(SignupRequest request) {
        logger.info("Signing up user: {}", request.username());

        if (isDuplicatePesel(request.pesel())) {
            throw new DuplicateException("Pesel already exists.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateException("Username already exists.");
        }

        try {
            UserAccount newUserAccount = createUserAccount(request);
            Account account = accountService.createAccount(newUserAccount);
            newUserAccount.setAccounts(List.of(account));
            userRepository.save(newUserAccount);

            logger.info("User {} signed up successfully", request.username());
            return googleAuthService.getGoogleAuthenticatorBarCode(newUserAccount.getGoogleSecret(),
                    newUserAccount.getUsername(), "Liberty Bank");
        } catch (Exception e) {
            logger.error("Error signing up user: {}", request.username(), e);
            throw new RSAException("Unexpected encryption error.", e);
        }
    }

    private boolean isDuplicatePesel(String pesel) {
        return userPersonalDataRepository.findAllPeselHash().stream()
                .anyMatch(hashedPesel -> hashingService.matches(pesel, hashedPesel));
    }

    private UserAccount createUserAccount(SignupRequest request) throws Exception {
        logger.debug("Creating user account for username: {}", request.username());

        UserPersonalData userPersonalData = createUserPersonalData(request);
        UserAccount userAccount = UserAccount.builder()
                .username(request.username())
                .email(rsaService.encrypt(request.email()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(StatusAccount.ACTIVE)
                .role(request.isBusiness() ? Role.COMPANY : Role.CLIENT)
                .googleSecret(googleAuthService.generateSecretKey())
                .hmac(hashingService.hash(generateUserHmacContent(request)))
                .build();

        userAccount.setUserPersonalData(userPersonalData);
        userPersonalData.setUserAccount(userAccount);

        return userAccount;
    }

    private UserPersonalData createUserPersonalData(SignupRequest request) throws Exception {
        return UserPersonalData.builder()
                .firstName(rsaService.encrypt(request.firstName()))
                .lastName(rsaService.encrypt(request.lastName()))
                .pesel(rsaService.encrypt(request.pesel()))
                .peselHash(hashingService.hash(request.pesel()))
                .idCardNumber(rsaService.encrypt("0"))
                .phoneNumber(rsaService.encrypt(request.phoneNumber()))
                .countryOfOrigin(rsaService.encrypt(request.countryOfOrigin()))
                .hmac(hashingService.hash(generatePersonalDataHmacContent(request)))
                .build();
    }

    private String generateUserHmacContent(SignupRequest request) {
        return request.username() + request.email() + StatusAccount.PENDING + "0" + "False" + "False";
    }

    private String generatePersonalDataHmacContent(SignupRequest request) {
        return request.firstName() + request.lastName() + request.pesel() + request.phoneNumber() + request.countryOfOrigin();
    }

    @Override
    @Loggable
    public UserModel authenticate(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.info("Authenticating user: {}", request.username());

        UserAccount user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        authenticateUser(request, user);
        validateTotpCode(request, user);

        sessionService.invalidateSession(user.getUserId());
        addJwtCookieToResponse(user, httpServletResponse);

        Session session = createSession(user, httpServletRequest);
        activityLogService.createLog(session, ActivityLogAction.LOGIN);

        logger.info("User {} authenticated successfully", request.username());
        return userModelAssembly.toModelAuthenticate(user);
    }

    private void authenticateUser(LoginRequest request, UserAccount user) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException e) {
            handleFailedLoginAttempt(user);
            throw new AuthenticationException("Invalid username or password") {};
        }
    }

    private void validateTotpCode(LoginRequest request, UserAccount user) {
        String expectedCode = googleAuthService.getTOTPCode(user.getGoogleSecret());
        if (!request.code().equals(expectedCode)) {
            throw new AuthenticationException("Invalid TOTP code") {};
        }
    }

    private void handleFailedLoginAttempt(UserAccount user) {
        logger.warn("Invalid login attempt for user: {}", user.getUsername());
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 3) {
            user.setStatus(StatusAccount.LOCKED);
        }
    }

    private void addJwtCookieToResponse(UserAccount user, HttpServletResponse response) {
        logger.debug("Adding JWT cookie for user: {}", user.getUserId());
        Cookie cookie = jwtService.createJwtCookie(Map.of("id", user.getUserId()), user, "jwt_token");
        response.addCookie(cookie);
    }

    private Session createSession(UserAccount userAccount, HttpServletRequest request) {
        String ipAddress = sessionService.getClientIp(request);
        String userAgent = sessionService.getUserAgent(request);
        return sessionService.createSession(userAccount, ipAddress, userAgent);
    }

    @Override
    @Loggable
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        long userId = (long) request.getAttribute("id");
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User account with ID " + userId + " does not exist"));

        addJwtCookieToResponse(user, response);
        logger.info("Token refreshed for user ID: {}", userId);
    }

    @Override
    @Loggable
    public void logout(HttpServletRequest request) {
        String sessionId = (String) request.getAttribute("session_id");
        sessionService.invalidateSession(sessionId);
        logger.info("Session ID: {} invalidated", sessionId);
    }

    @Override
    public void deactivate(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        userRepository.findById(userId).ifPresentOrElse(user -> {
            user.setStatus(StatusAccount.INACTIVE);
            userRepository.save(user);
        }, () -> {
            throw new EntityNotFoundException("User account with ID " + userId + " does not exist");
        });
    }
}

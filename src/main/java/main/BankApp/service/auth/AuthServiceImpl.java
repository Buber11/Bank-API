package main.BankApp.service.auth;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.BankApp.dto.UserModel;
import main.BankApp.model.account.Account;
import main.BankApp.model.session.ActivityLogAction;
import main.BankApp.model.session.Session;
import main.BankApp.model.user.Role;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import main.BankApp.model.user.UserPersonalData;
import main.BankApp.request.auth.LoginRequest;
import main.BankApp.expection.DuplicateException;

import main.BankApp.expection.RSAException;
import main.BankApp.repository.UserPersonalDataRepository;
import main.BankApp.repository.UserRepository;
import main.BankApp.request.auth.SignupRequest;


import main.BankApp.common.Loggable;
import main.BankApp.service.googleAuthenticator.GoogleAuthService;
import main.BankApp.service.hashing.HashingService;
import main.BankApp.security.JwtService;
import main.BankApp.service.rsa.RSAService;
import main.BankApp.service.session.SessionService;
import main.BankApp.service.account.AccountService;
import main.BankApp.service.activityLog.ActivityLogService;
import main.BankApp.service.user.UserModelAssembly;
import org.apache.catalina.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("Attempting to sign up user: {}", request.username());
        String barCodeUrl;
        if (isPeselAlreadyExists(request.pesel())) {
            logger.warn("Pesel already exists");
            throw new DuplicateException("This Pesel has already existed");
        }
        if (userRepository.existsByUsername(request.username())) {
            logger.warn("Username already exists: {}", request.username());
            throw new DuplicateException("This Username has already existed");
        }

        try {
            UserAccount newUserAccount = buildUserAccount(request);
            UserPersonalData newUserPersonalData = buildUserPersonalData(request);

            newUserAccount.setUserPersonalData(newUserPersonalData);
            newUserPersonalData.setUserAccount(newUserAccount);

            Account account = accountService.createAccount(newUserAccount);
            newUserAccount.setAccounts(List.of(account));

            barCodeUrl = googleAuthService.getGoogleAuthenticatorBarCode(newUserAccount.getGoogleSecret(),
                    newUserAccount.getUsername(),
                    "Liberty Bank");

            userRepository.save(newUserAccount);

            logger.info("User {} successfully signed up", request.username());



        } catch (Exception e) {
            logger.error("An error occurred during signup for user: {}", request.username(), e);
            throw new RSAException("An unexpected error occurred during encryption.", e);
        }
        return barCodeUrl;
    }

    private boolean isPeselAlreadyExists(String pesel) {
        List<String> hashedPeselList = userPersonalDataRepository.findAllPeselHash();
        for (String hashedPesel : hashedPeselList) {
            if (hashingService.matches(pesel, hashedPesel)) {
                return true;
            }
        }
        return false;
    }

    private UserAccount buildUserAccount(SignupRequest request) throws Exception {
        logger.debug("Building user account for username: {}", request.username());
        return UserAccount.builder()
                .username(request.username())
                .email(rsaService.encrypt(request.email()))
                .passwordHash(passwordEncoder.encode(request.password()))
                .status(StatusAccount.ACTIVE)
                .role( request.isBusiness() ? Role.COMPANY : Role.CLIENT )
                .googleSecret( googleAuthService.generateSecretKey() )
                .hmac(hashingService.hash(request.username() + request.email() + StatusAccount.PENDING + "0" + "False" + "False"))
                .build();
    }

    private UserPersonalData buildUserPersonalData(SignupRequest request) throws Exception {
        logger.debug("Building personal data for user: {}", request.username());
        return UserPersonalData.builder()
                .firstName(rsaService.encrypt(request.firstName()))
                .lastName(rsaService.encrypt(request.lastName()))
                .pesel(rsaService.encrypt(request.pesel()))
                .peselHash(hashingService.hash(request.pesel()))
                .idCardNumber(rsaService.encrypt("0"))
                .phoneNumber(rsaService.encrypt(request.phoneNumber()))
                .countryOfOrigin(rsaService.encrypt(request.countryOfOrigin()))
                .hmac(hashingService.hash(request.firstName() + request.lastName() + request.pesel() + request.phoneNumber() + request.countryOfOrigin()))
                .build();
    }

    @Override
    @Loggable
    public UserModel authenticate(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.info("Attempting to authenticate user: {}", request.username());
        UserAccount user;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            String secretKey = user.getGoogleSecret(); // Pobierz klucz TOTP z konta użytkownika
            String expectedCode = googleAuthService.getTOTPCode(secretKey); // Generuj kod na podstawie klucza

            if (!request.code().equals(expectedCode)) {
                logger.warn("Invalid TOTP code for user: {}", request.username());
                throw new AuthenticationException("Invalid TOTP code") {};
            }

            sessionService.invalidateSession(user.getUserId());

            addJwtCookieToResponse(user, httpServletResponse);

            String ipAddress = sessionService.getClientIp(httpServletRequest);
            String userAgent = sessionService.getUserAgent(httpServletRequest);

            Session session = createSession(user, ipAddress, userAgent);
            createLog(session, ActivityLogAction.LOGIN);

            logger.info("User {} successfully authenticated", request.username());

        } catch (BadCredentialsException e) {
            logger.warn("Invalid login attempt for user: {}", request.username());
            throw new AuthenticationException("Invalid username or password") {};
        } catch (AuthenticationException e) {
            logger.error("Authentication error for user: {}", request.username(), e);
            throw e;
        }

        return userModelAssembly.toModelAuthenticate(user);
    }

    private void addJwtCookieToResponse(UserAccount user, HttpServletResponse response) {
        logger.debug("Adding JWT cookie for user: {}", user.getUserId());
        Cookie cookie = jwtService.createJwtCookie(Map.of("id", user.getUserId()), user, "jwt_token");
        response.addCookie(cookie);
    }

    private Session createSession(UserAccount userAccount, String ipAddress, String userAgent) {
        logger.debug("Creating session for user ID: {}", userAccount.getUserId());
        return sessionService.createSession(userAccount, ipAddress, userAgent);
    }

    private void createLog(Session session, ActivityLogAction action) {
        logger.debug("Creating activity log for session ID: {} with action: {}", session.getSessionId(), action);
        activityLogService.createLog(session, action);
    }

    @Override
    @Loggable
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        long userId = (long) request.getAttribute("id");
        logger.info("Refreshing token for user with ID: {}", userId);

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User account with ID " + userId + " does not exist"));

        addJwtCookieToResponse(user, response);

        logger.info("Token successfully refreshed for user ID: {}", userId);
    }

    @Override
    @Loggable
    public void logout(HttpServletRequest request) {
        String sessionId = (String) request.getAttribute("session_id");
        logger.info("User with session ID: {} is logging out", sessionId);
        sessionService.invalidateSession(sessionId);
        logger.info("Session ID: {} invalidated", sessionId);
    }

    @Override
    public void deactivate(HttpServletRequest request) {
        long userId = (long) request.getAttribute("id");
        Optional<UserAccount> userAccount = userRepository.findById(userId);
        userAccount.ifPresentOrElse(user -> {
            user.setStatus(StatusAccount.INACTIVE);
            userRepository.save(user);
        }, EntityNotFoundException::new);
    }
}

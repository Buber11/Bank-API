package main.BankApp.Auth;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.BankApp.Auth.Request.LoginRequest;
import main.BankApp.BankAccount.Service.AccountService;
import main.BankApp.BankAccount.entity.Account;
import main.BankApp.Expection.DuplicateException;

import main.BankApp.Expection.EncryptionException;
import main.BankApp.Security.JwtService;
import main.BankApp.SecurityAlgorithms.Hash.HashingService;
import main.BankApp.SecurityAlgorithms.RSA.RSAService;
import main.BankApp.Session.Session.ActivityLogService;
import main.BankApp.Session.Session.SessionService;
import main.BankApp.Session.enitity.ActivityLogAction;
import main.BankApp.Session.enitity.Session;
import main.BankApp.User.ENTITY.*;
import main.BankApp.User.Repository.UserPersonalDataRepository;
import main.BankApp.User.Repository.UserRepository;
import main.BankApp.Auth.Request.SignupRequest;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Override
    public void signup(SignupRequest request) {
        logger.info("Attempting to sign up user: {}", request.username());

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
            userRepository.save(newUserAccount);

            logger.info("User {} successfully signed up", request.username());

        } catch (Exception e) {
            logger.error("An error occurred during signup for user: {}", request.username(), e);
            throw new EncryptionException("An unexpected error occurred during encryption.", e);
        }
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
    public void authenticate(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        logger.info("Attempting to authenticate user: {}", request.username());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserAccount user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

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
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        long userId = (long) request.getAttribute("id");
        logger.info("Refreshing token for user with ID: {}", userId);

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User account with ID " + userId + " does not exist"));

        addJwtCookieToResponse(user, response);

        logger.info("Token successfully refreshed for user ID: {}", userId);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String sessionId = (String) request.getAttribute("session_id");
        logger.info("User with session ID: {} is logging out", sessionId);
        sessionService.invalidateSession(sessionId);
        logger.info("Session ID: {} invalidated", sessionId);
    }
}

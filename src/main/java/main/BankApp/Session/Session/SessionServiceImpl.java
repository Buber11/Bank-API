package main.BankApp.Session.Session;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.SecurityAlgorithms.Hash.HashingService;
import main.BankApp.Session.enitity.Session;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.Session.repository.SessionRepository;


import main.BankApp.User.Service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    private final SessionRepository sessionRepository;
    private final HashingService hashingService;
    private final UserService userService;

    public Session createSession(UserAccount userAccount, String ipAddress, String userAgent) {
        String sessionId = generateSessionId();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(1);

        String dataToHash = sessionId + userAccount.getUserId() + ipAddress;
        String hmac = hashingService.hash(dataToHash);

        Session session = Session.builder()
                .sessionId(sessionId)
                .userAccount(userAccount)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isActive(true)
                .hmac(hmac)
                .build();

        logger.info("Creating session for user ID: {} with session ID: {}", userAccount.getUserId(), sessionId);
        return sessionRepository.save(session);
    }

    public void invalidateSession(String sessionId) {
        logger.info("Invalidating session with ID: {}", sessionId);
        Optional<Session> sessionOptional = sessionRepository.findBySessionId(sessionId);
        sessionOptional.ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
            logger.info("Session with ID: {} has been invalidated", sessionId);
        });
    }

    public boolean isSessionActive(String sessionId) {
        logger.info("Checking if session with ID: {} is active", sessionId);
        Optional<Session> sessionOptional = sessionRepository.findBySessionId(sessionId);
        return sessionOptional.map(Session::isActive).orElse(false);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void invalidateExpiredSessions() {
        logger.info("Scheduled task started to invalidate expired sessions.");
        LocalDateTime now = LocalDateTime.now();
        List<Session> sessions = sessionRepository.findByIsActive(true);

        for (Session session : sessions) {
            if (!checkHmac(session)) {
                logger.warn("Session with ID: {} has invalid HMAC, invalidating session", session.getSessionId());
                session.setActive(false);
                sessionRepository.save(session);
                UserAccount userAccount = session.getUserAccount();
                userService.lockAccount(userAccount);
                logger.warn("Session with ID: {} has been invalidated due to invalid HMAC", session.getSessionId());
            } else if (session.getExpiresAt().isBefore(now)) {
                logger.warn("Session with ID: {} has expired, invalidating session", session.getSessionId());
                session.setActive(false);
                sessionRepository.save(session);
                logger.warn("Session with ID: {} has been invalidated due to expiration", session.getSessionId());
            }
        }
    }

    @Override
    public boolean checkSession(long userId, String ip, String userAgent) {
        logger.info("Checking session for user ID: {} with IP: {} and User-Agent: {}", userId, ip, userAgent);
        Optional<Session> sessionOpt = sessionRepository.findByUserAccount_UserIdAndIsActive(userId, true);
        if (sessionOpt.isEmpty()) {
            logger.info("No active session found for user ID: {}", userId);
            return false;
        } else {
            Session session = sessionOpt.get();
            if (session.getUserAgent().equals(userAgent) && session.getIpAddress().equals(ip)) {
                logger.info("Session for user ID: {} is valid", userId);
                return true;
            } else {
                logger.info("Session for user ID: {} is invalid due to mismatch in IP or User-Agent", userId);
                return false;
            }
        }
    }

    @Override
    public String getSessionId(long userId) {
        logger.info("Fetching session ID for user ID: {}", userId);
        Optional<Session> sessionOpt = sessionRepository.findByUserAccount_UserIdAndIsActive(userId, true);
        if (sessionOpt.isEmpty()) {
            logger.error("No active session found for user ID: {}", userId);
            throw new RuntimeException("The Session is empty");
        } else {
            String sessionId = sessionOpt.get().getSessionId();
            logger.info("Session ID for user ID: {} is {}", userId, sessionId);
            return sessionId;
        }
    }

    @Override
    public void invalidateSession(long userId) {
        logger.info("Invalidating session for user ID: {}", userId);
        Optional<Session> sessionOptional = sessionRepository.findByUserAccount_UserIdAndIsActive(userId, true);
        sessionOptional.ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
            logger.info("Session for user ID: {} has been invalidated", userId);
        });
    }

    @Override
    public void invalidateAllActiveSessions() {
        logger.info("Invalidating all active sessions");
        List<Session> allActiveSessions = sessionRepository.findByIsActive(true);
        for (Session session : allActiveSessions) {
            session.setActive(false);
            sessionRepository.save(session);
            logger.info("Session with ID: {} has been invalidated", session.getSessionId());
        }
    }

    private String generateSessionId() {
        String sessionId = java.util.UUID.randomUUID().toString();
        logger.debug("Generated session ID: {}", sessionId);
        return sessionId;
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    @Override
    public String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent;
    }

    private boolean checkHmac(Session session) {
        String hmacRaw = session.getSessionId() + session.getUserAccount().getUserId() + session.getIpAddress();
        boolean isValid = hashingService.matches(hmacRaw, session.getHmac());
        return isValid;
    }
}


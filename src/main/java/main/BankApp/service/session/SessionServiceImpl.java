package main.BankApp.service.session;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.expection.SessionNotFoundException;
import main.BankApp.model.session.Session;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.SessionRepository;


import main.BankApp.service.user.UserService;
import main.BankApp.service.hashing.HashingService;
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

    @Override
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

        logger.info("Creating session for user ID: {}", userAccount.getUserId());
        return sessionRepository.save(session);
    }

    @Override
    public void invalidateSession(String sessionId) {
        updateSessionStatus(sessionId, false);
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        logger.debug("Checking if session with ID: {} is active", sessionId);
        return sessionRepository.findBySessionId(sessionId)
                .map(Session::isActive)
                .orElse(false);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void invalidateExpiredSessions() {
        logger.info("Scheduled task started to invalidate expired sessions.");
        LocalDateTime now = LocalDateTime.now();

        List<Session> activeSessions = sessionRepository.findByIsActive(true);
        activeSessions.stream()
                .filter(session -> !checkHmac(session) || session.getExpiresAt().isBefore(now))
                .forEach(session -> {
                    session.setActive(false);
                    sessionRepository.save(session);

                    if (!checkHmac(session)) {
                        logger.warn("Session with ID: {} has invalid HMAC; locking account", session.getSessionId());
                        userService.lockAccount(session.getUserAccount());
                    } else {
                        logger.info("Session with ID: {} expired and invalidated", session.getSessionId());
                    }
                });
    }

    @Override
    public boolean checkSession(long userId, String ip, String userAgent) {
        logger.debug("Checking session for user ID: {} with IP: {} and User-Agent: {}", userId, ip, userAgent);
        return sessionRepository.findByUserAccount_UserIdAndIsActive(userId, true)
                .filter(session -> session.getIpAddress().equals(ip) && session.getUserAgent().equals(userAgent))
                .isPresent();
    }

    @Override
    public String getSessionId(long userId) {
        logger.debug("Fetching session ID for user ID: {}", userId);
        return sessionRepository.findByUserAccount_UserIdAndIsActive(userId, true)
                .map(Session::getSessionId)
                .orElseThrow(() -> new SessionNotFoundException("No active session for user ID: " + userId));
    }

    @Override
    public void invalidateSession(long userId) {
        logger.info("Invalidating session for user ID: {}", userId);
        sessionRepository.findByUserAccount_UserIdAndIsActive(userId, true)
                .ifPresent(session -> updateSessionStatus(session.getSessionId(), false));
    }

    @Override
    public void invalidateAllActiveSessions() {
        logger.info("Invalidating all active sessions.");
        List<Session> allActiveSessions = sessionRepository.findByIsActive(true);
        allActiveSessions.forEach(session -> updateSessionStatus(session.getSessionId(), false));
    }

    private String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }

    private boolean checkHmac(Session session) {
        String hmacRaw = session.getSessionId() + session.getUserAccount().getUserId() + session.getIpAddress();
        return hashingService.matches(hmacRaw, session.getHmac());
    }

    private void updateSessionStatus(String sessionId, boolean isActive) {
        sessionRepository.findBySessionId(sessionId).ifPresent(session -> {
            session.setActive(isActive);
            sessionRepository.save(session);
            logger.info("Session with ID: {} has been {}", sessionId, isActive ? "activated" : "invalidated");
        });
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
        return request.getHeader("User-Agent");
    }
}


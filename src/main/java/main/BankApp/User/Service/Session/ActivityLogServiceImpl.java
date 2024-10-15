package main.BankApp.User.Service.Session;

import lombok.RequiredArgsConstructor;
import main.BankApp.SecurityAlgorithms.Hash.HashingService;
import main.BankApp.User.ENTITY.ActivityLog;
import main.BankApp.User.ENTITY.ActivityLogAction;
import main.BankApp.User.ENTITY.Session;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.User.Repository.ActivityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public final class ActivityLogServiceImpl implements ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    private final ActivityLogRepository activityLogRepository;
    private final HashingService hashingService;

    @Override
    public ActivityLog createLog(Session session,
                                 ActivityLogAction action) {
        String logId = generateSessionId();
        String dataToHash = logId + session.getSessionId() + action;
        String hmac = hashingService.hash(dataToHash);

        ActivityLog log = ActivityLog.builder()
                .logId(logId)
                .session(session)
                .action(action)
                .timestamp(LocalDateTime.now())
                .hmac(hmac)
                .build();

        logger.info("Creating log: {}", logId);
        return activityLogRepository.save(log);
    }

    @Override
    public ActivityLog getLog(Long logId) {
        logger.info("Fetching log with ID: {}", logId);
        return activityLogRepository.findById(logId).orElse(null);
    }

    @Override
    public void deleteLog(Long logId) {
        logger.info("Deleting log with ID: {}", logId);
        activityLogRepository.deleteById(logId);
    }

    private String generateSessionId() {
        String sessionId = java.util.UUID.randomUUID().toString();
        logger.debug("Generated session ID: {}", sessionId);  // Debug log for session ID generation
        return sessionId;
    }
}

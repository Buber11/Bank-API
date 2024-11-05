package main.BankApp.service.activityLog;

import lombok.RequiredArgsConstructor;
import main.BankApp.expection.LogNotFoundException;
import main.BankApp.model.session.ActivityLog;
import main.BankApp.model.session.ActivityLogAction;
import main.BankApp.model.session.Session;
import main.BankApp.repository.ActivityLogRepository;
import main.BankApp.service.hashing.HashingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public final class ActivityLogServiceImpl implements ActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    private final ActivityLogRepository activityLogRepository;
    private final HashingService hashingService;

    @Override
    public ActivityLog createLog(Session session, ActivityLogAction action) {
        String logId = generateLogId(); // Renamed for clarity
        String dataToHash = logId + session.getSessionId() + action;
        String hmac = hashingService.hash(dataToHash);

        ActivityLog log = ActivityLog.builder()
                .logId(logId)
                .session(session)
                .action(action)
                .timestamp(LocalDateTime.now())
                .hmac(hmac)
                .build();

        logger.debug("Creating log with ID: {}", logId); // Changed to debug for detailed information
        return activityLogRepository.save(log);
    }

    @Override
    public ActivityLog getLog(String logId) {
        logger.info("Fetching log with ID: {}", logId);
        return activityLogRepository.findById(logId)
                .orElseThrow(() -> new LogNotFoundException("Log not found for ID: " + logId)); // Throwing custom exception
    }

    @Override
    public void deleteLog(String logId) {
        logger.info("Deleting log with ID: {}", logId);
        activityLogRepository.deleteById(logId);
    }

    private String generateLogId() { // Renamed for clarity
        String logId = java.util.UUID.randomUUID().toString();
        logger.debug("Generated log ID: {}", logId);
        return logId;
    }
}

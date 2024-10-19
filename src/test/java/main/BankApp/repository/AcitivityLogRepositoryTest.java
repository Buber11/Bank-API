package main.BankApp.repository;

import main.BankApp.model.session.ActivityLog;
import main.BankApp.model.session.ActivityLogAction;
import main.BankApp.model.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AcitivityLogRepositoryTest {
    @Autowired
    ActivityLogRepository activityLogRepository;
    @Autowired
    SessionRepository sessionRepository;

    private Session testSession;
    private ActivityLog activityLog;
    @BeforeEach
    public void setUp() {
        testSession = Session.builder()
                .sessionId("test-session-id")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .ipAddress("192.168.0.1")
                .userAgent("Mozilla/5.0")
                .isActive(true)
                .hmac("sample-hmac-value")
                .build();

        sessionRepository.save(testSession);

        activityLog = ActivityLog.builder()
                .logId("test-log-id")
                .session(testSession)
                .action(ActivityLogAction.LOGIN)
                .timestamp(LocalDateTime.now())
                .hmac("sample-hmac-value")
                .build();

        activityLogRepository.save(activityLog);
    }

    @Test
    @Rollback(value = true)
    public void testFindBySessionId() {

        List<ActivityLog> logs = activityLogRepository.findBySession_SessionId(testSession.getSessionId());

        assertThat(logs).isNotEmpty();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getLogId()).isEqualTo("test-log-id");
        assertThat(logs.get(0).getAction()).isEqualTo(ActivityLogAction.LOGIN);
    }
    @Test
    @Rollback(value = true)
    public void testActivityLogsForSession() {
        List<ActivityLog> logs = activityLogRepository.findBySession_SessionId(testSession.getSessionId());

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getSession().getSessionId()).isEqualTo(testSession.getSessionId());
    }


}

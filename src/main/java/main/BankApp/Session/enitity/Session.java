package main.BankApp.Session.enitity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.Session.enitity.ActivityLog;
import main.BankApp.User.ENTITY.UserAccount;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    private String sessionId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;
    private String hmac;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "session")
    private List<ActivityLog> activityLogs;

}

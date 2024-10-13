package main.BankApp.User.ENTITY;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Builder
@Data
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    private String action;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
    private String hmac;
}

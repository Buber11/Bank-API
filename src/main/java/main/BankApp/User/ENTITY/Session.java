package main.BankApp.User.ENTITY;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Builder
@Data
public class Session {
    @Id
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;
    private String hmac;

}

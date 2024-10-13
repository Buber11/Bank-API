package main.BankApp.User.ENTITY;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_accounts")
@Builder
@Data
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String passwordHash;
    private String email;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private int failedLoginAttempts;

    private LocalDateTime lastLogin;
    private boolean twoFactorEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean consentToCommunication;
    private boolean isBusinessAccount;
    private String hmac;

    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPersonalData userPersonalData;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Session> sessions;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityLog> activityLogs;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserConsent> userConsents;


}
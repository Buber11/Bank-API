package main.BankApp.User.ENTITY;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user_accounts")
@Builder
@Data
public class UserAccount implements UserDetails {
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


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != StatusEnum.CLOSED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != StatusEnum.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status != StatusEnum.SUSPENDED;
    }

    @Override
    public boolean isEnabled() {
        return status == StatusEnum.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
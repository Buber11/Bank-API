package main.BankApp.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.model.account.Account;
import main.BankApp.model.session.Session;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user_accounts")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String passwordHash;
    @Column(length = 500)
    private String email;

    @Enumerated(EnumType.STRING)
    private StatusAccount status;

    private int failedLoginAttempts;
    private LocalDateTime lastLogin;
    private boolean twoFactorEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean consentToCommunication;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "google_secret")
    private String googleSecret;

    private String hmac;


    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPersonalData userPersonalData;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Session> sessions;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserConsent> userConsents;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contact> contacts;


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()));
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
        return status != StatusAccount.CLOSED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != StatusAccount.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status != StatusAccount.SUSPENDED;
    }

    @Override
    public boolean isEnabled() {
        return status == StatusAccount.ACTIVE;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", failedLoginAttempts=" + failedLoginAttempts +
                ", lastLogin=" + lastLogin +
                ", twoFactorEnabled=" + twoFactorEnabled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", consentToCommunication=" + consentToCommunication +
                ", hmac='" + hmac + '\'' +
                '}';
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
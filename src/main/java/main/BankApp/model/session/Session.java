package main.BankApp.model.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.model.user.UserAccount;

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
    @JsonIgnore
    private UserAccount userAccount;

    @OneToMany(mappedBy = "session")
    private List<ActivityLog> activityLogs;

}

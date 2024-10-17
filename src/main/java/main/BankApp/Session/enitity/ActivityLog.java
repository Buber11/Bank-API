package main.BankApp.Session.enitity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    @Id
    private String logId;
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
    @Enumerated(EnumType.STRING)
    private ActivityLogAction action;
    private LocalDateTime timestamp;
    private String hmac;

}

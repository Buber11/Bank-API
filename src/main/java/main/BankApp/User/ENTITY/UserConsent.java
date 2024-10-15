package main.BankApp.User.ENTITY;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.User.ENTITY.ConsentTypeEnum;
import main.BankApp.User.ENTITY.UserAccount;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_consent")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Enumerated(EnumType.STRING)
    private ConsentTypeEnum consentType;

    private boolean isGranted;
    private LocalDateTime consentDate;
    private String hmac;


}

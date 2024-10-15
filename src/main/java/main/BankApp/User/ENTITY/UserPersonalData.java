package main.BankApp.User.ENTITY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.User.ENTITY.UserAccount;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_personal_data")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPersonalData {

    @Id
    private Long userId;

    private String firstName;
    private String lastName;
    private String countryOfOrigin;
    private String phoneNumber;
    private String pesel;
    @Column(name = "pesel_hash")
    private String peselHash;
    private String idCardNumber;

    private LocalDateTime updatedAt;
    private String hmac;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserAccount userAccount;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

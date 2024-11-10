package main.BankApp.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(length = 500)
    private String firstName;
    @Column(length = 500)
    private String lastName;
    @Column(length = 500)
    private String countryOfOrigin;
    @Column(length = 500)
    private String phoneNumber;
    @Column(length = 500)
    private String pesel;
    @Column(name = "pesel_hash")
    private String peselHash;
    @Column(length = 500)
    private String sex;
    @Column(length = 500)
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

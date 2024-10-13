package main.BankApp.User.ENTITY;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import main.BankApp.User.ENTITY.UserAccount;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_personal_data")
@Builder
@Data
public class UserPersonalData {

    @Id
    private Long userId;

    private String firstName;
    private String lastName;
    private String countryOfOrigin;
    private String phoneNumber;
    private String pesel;
    private String idCardNumber;

    private LocalDateTime updatedAt;
    private String hmac;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

}

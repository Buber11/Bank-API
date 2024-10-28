package main.BankApp.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.model.user.StatusAccount;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataView {

    private String username;
    private String email;
    private StatusAccount status;
    private LocalDateTime lastLogin;
    private boolean twoFactorEnabled;
    private boolean consentToCommunication;
    private boolean isBusinessAccount;

    // UserPersonalData
    private String firstName;
    private String lastName;
    private String countryOfOrigin;
    private String phoneNumber;
    private String pesel;
    private String sex;
}

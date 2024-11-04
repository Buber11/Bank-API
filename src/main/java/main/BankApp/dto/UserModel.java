package main.BankApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.model.user.Role;
import main.BankApp.model.user.StatusAccount;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName(value = "user")
@Relation(collectionRelation = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel extends RepresentationModel<UserModel> {

    private long userId;
    private String username;
    private String email;
    private StatusAccount status;
    private LocalDateTime lastLogin;
    private boolean twoFactorEnabled;
    private boolean consentToCommunication;
    private Role role;
    private String qRcode;

    private String firstName;
    private String lastName;
    private String countryOfOrigin;
    private String phoneNumber;
    private String pesel;
    private String sex;
}
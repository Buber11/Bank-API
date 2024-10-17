package main.BankApp.User.Contact.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.User.ENTITY.UserAccount;

import java.time.LocalDate;

@Entity()
@Table(name = "contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;
    private String name;
    private String numberAccount;
    private Long numberOfUse;
    private LocalDate dateOfLastUse;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

}

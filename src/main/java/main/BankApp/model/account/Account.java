package main.BankApp.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.BankApp.model.user.UserAccount;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Accounts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    private String accountType;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Column(name = "balance", nullable = false)
    private String balance;

    @Column(name = "account_status", nullable = false)
    private String accountStatus;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    @JsonIgnore
    private UserAccount userAccount;

    @OneToMany(mappedBy = "hostAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactionsOut;

    @OneToMany(mappedBy = "payeeAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactionsIn;


}
package main.BankApp.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionID;

    @Column(name = "reference_number", nullable = false)
    private String referenceNumber;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "Transaction_Type", nullable = false)
    private String transactionType;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    private String description;

    private String hmac;

    @ManyToOne
    @JoinColumn(name = "host_account_number", referencedColumnName = "account_number", nullable = false)
    @JsonIgnore
    private Account hostAccount;

    @ManyToOne
    @JoinColumn(name = "payee_account_number", referencedColumnName = "account_number", nullable = false)
    @JsonIgnore
    private Account payeeAccount;


}

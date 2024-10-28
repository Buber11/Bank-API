package main.BankApp.dto;

import lombok.*;
import main.BankApp.model.account.AccountType;
import main.BankApp.model.account.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountClientView {

    private String accountNumber;
    private AccountType accountType;
    private String balance;
    private List<TransactionClientView> transactions;
}

package main.BankApp.request.transaction;

import main.BankApp.model.account.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SingleTransactionRequest(
        String hostAccountNumber,
        BigDecimal amount,
        String payeeAccountNumber,
        String description,
        String transactionType,
        Currency currency
) implements TransactionRequest {
}

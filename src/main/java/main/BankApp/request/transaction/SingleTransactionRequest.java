package main.BankApp.request.transaction;

import jakarta.validation.constraints.Min;
import main.BankApp.model.account.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SingleTransactionRequest(
        String hostAccountNumber,
        @Min(value = 0)
        BigDecimal amount,
        String payeeAccountNumber,
        String description,
        String transactionType,
        Currency currency
) implements TransactionRequest {
}

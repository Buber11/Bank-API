package main.BankApp.request.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        LocalDateTime transactionDate,
        BigDecimal amount,
        String payeeAccountNumber,
        String description,
        String transactionType
) {
}

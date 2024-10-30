package main.BankApp.request.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SingleTransactionRequest(
        String hostAccountNumber,
        BigDecimal amount,
        String payeeAccountNumber,
        String description,
        String transactionType
) implements TransactionRequest {
}

package main.BankApp.request.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MultipleTransactionRequest (
        String hostAccountNumber,
        BigDecimal amount,
        List<String> payeeAccountNumber,
        String description,
        String transactionType
)implements TransactionRequest {
}

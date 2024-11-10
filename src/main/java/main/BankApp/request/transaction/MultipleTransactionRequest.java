package main.BankApp.request.transaction;

import main.BankApp.model.account.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MultipleTransactionRequest (
        String hostAccountNumber,
        BigDecimal amount,
        List<String> payeeAccountNumber,
        String description,
        String transactionType,
        Currency currency
)implements TransactionRequest {
}

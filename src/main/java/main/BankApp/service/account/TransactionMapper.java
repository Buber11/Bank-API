package main.BankApp.service.account;

import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Transaction;
import main.BankApp.service.rsa.RSAService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionMapper {



    public static TransactionClientView mapTransactionToView(Transaction transaction, RSAService rsaService) {
        BigDecimal formattedAmount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);

        LocalDateTime transactionDate = transaction.getTransactionDate();
        if (transactionDate == null || transactionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid transaction date");
        }

        try {
            return TransactionClientView.builder()
                    .referenceNumber(rsaService.decrypt(transaction.getReferenceNumber()))
                    .description(transaction.getDescription())
                    .transactionDate(transactionDate)
                    .amount(formattedAmount)
                    .payeeAccountNumber(transaction.getPayeeAccount().getAccountNumber())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

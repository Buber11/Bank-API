package main.BankApp.service.account;

import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class TransactionMapper {

    public static TransactionClientView mapTransactionToView(Transaction transaction) {
        BigDecimal formattedAmount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);

        LocalDate transactionDate = transaction.getTransactionDate();
        if (transactionDate == null || transactionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid transaction date");
        }

        return TransactionClientView.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .description(transaction.getDescription())
                .transactionDate(transactionDate)
                .amount(formattedAmount)
                .payeeAccountNumber(transaction.getPayeeAccount().getAccountNumber())
                .build();
    }

}

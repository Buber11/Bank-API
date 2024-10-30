package main.BankApp.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionClientView {
    private String referenceNumber;
    private BigDecimal amount;
    private String payeeAccountNumber;
    private String description;
    private LocalDateTime transactionDate;

}

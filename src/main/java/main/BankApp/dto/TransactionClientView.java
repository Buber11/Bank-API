package main.BankApp.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
public class TransactionClientView {
    private String referenceNumber;
    private BigDecimal amount;
    private String payeeAccountNumber;
    private String description;

}

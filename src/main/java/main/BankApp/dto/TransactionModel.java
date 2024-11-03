package main.BankApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName(value = "transaction")
@Relation(collectionRelation = "transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionModel extends RepresentationModel<TransactionModel> {
    private String hostAccountNumber;
    private String referenceNumber;
    private BigDecimal amount;
    private String payeeAccountNumber;
    private String description;
    private LocalDateTime transactionDate;
}


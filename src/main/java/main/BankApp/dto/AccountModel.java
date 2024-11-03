package main.BankApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import main.BankApp.model.account.AccountType;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName(value = "account")
@Relation(collectionRelation = "accounts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountModel extends RepresentationModel<AccountModel> {

    private String accountNumber;
    private AccountType accountType;
    private String balance;
    private List<TransactionModel> transactionsOut;
    private List<TransactionModel> transactionsIn;

}

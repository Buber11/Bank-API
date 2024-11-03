package main.BankApp.service.account;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import main.BankApp.controller.AccountController;
import main.BankApp.dto.AccountModel;
import main.BankApp.dto.TransactionModel;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.Account;
import main.BankApp.model.account.AccountType;
import main.BankApp.model.account.Transaction;
import main.BankApp.service.rsa.RSAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.awt.font.TextHitInfo;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler
        extends RepresentationModelAssemblerSupport<Account, AccountModel> {

    private final RSAService rsaService;

    public AccountModelAssembler(RSAService theRsaService) {
        super(AccountController.class, AccountModel.class);
        rsaService = theRsaService;
    }

    @Override
    public AccountModel toModel(Account entity) {
        AccountModel accountModel = instantiateModel(entity);

        accountModel.add(linkTo(
                methodOn(AccountController.class).getAllAccounts(null))
                .withSelfRel());


        accountModel.add(linkTo(
                methodOn(AccountController.class).getTransaction(
                        entity.getAccountNumber(),"in",0,10,null))
                .withRel("transactions-in"));

        accountModel.add(linkTo(methodOn(AccountController.class).getTransaction(
                entity.getAccountNumber(),"out",0,10,null))
                .withRel("transactions-out"));

        try {

            accountModel.setAccountNumber(entity.getAccountNumber());
            accountModel.setAccountType(AccountType.valueOf(rsaService.decrypt(entity.getAccountType())));
            accountModel.setBalance(rsaService.decrypt(entity.getBalance()));
            accountModel.setTransactionsIn( toTransactionModel( entity.getTransactionsIn() ) );
            accountModel.setTransactionsOut( toTransactionModel( entity.getTransactionsOut() ) );

        }catch (Exception e){
            throw  new RSAException(e.getMessage());
        }

        return accountModel;
    }

    @Override
    public CollectionModel<AccountModel> toCollectionModel(Iterable<? extends Account> entities) {
        CollectionModel<AccountModel> accountModels = super.toCollectionModel(entities);

        accountModels.add(linkTo(methodOn(AccountController.class).getAllAccounts(null)).withSelfRel());

        return accountModels;
    }

    public List<TransactionModel> toTransactionModel(List<Transaction> transactions) {
        if (transactions.isEmpty())
            return Collections.emptyList();

        return transactions.stream()
                .map(transaction -> {
                    try {
                        return TransactionModel.builder()
                                .referenceNumber(rsaService.decrypt(transaction.getReferenceNumber()))
                                .amount(transaction.getAmount())
                                .payeeAccountNumber(transaction.getPayeeAccount().getAccountNumber())
                                .hostAccountNumber(transaction.getHostAccount().getAccountNumber())
                                .description(transaction.getDescription())
                                .transactionDate(transaction.getTransactionDate())
                                .build()
                                .add(linkTo(
                                        methodOn(AccountController.class).doTransaction(null,null))
                                        .withRel("transactions"))
                                .add(linkTo(
                                        methodOn(AccountController.class).doTransactions(null,null))
                                        .withRel("transaction-group"));
                    } catch (Exception e) {
                        throw new RSAException(e.getMessage());
                    }
                })
                .toList();
    }

    public Page<TransactionModel> toTransactionModelPage(Page<Transaction> transactions) {
        List<TransactionModel> transactionModels = transactions.getContent().stream()
                .map(transaction -> {
                    try {
                        return TransactionModel.builder()
                                .referenceNumber(rsaService.decrypt(transaction.getReferenceNumber()))
                                .amount(transaction.getAmount())
                                .payeeAccountNumber(transaction.getPayeeAccount().getAccountNumber())
                                .hostAccountNumber(transaction.getHostAccount().getAccountNumber()) // Poprawka z payeeAccountNumber na hostAccountNumber
                                .description(transaction.getDescription())
                                .transactionDate(transaction.getTransactionDate())
                                .build()
                                .add(linkTo(methodOn(AccountController.class).doTransaction(null, null)).withRel("transactions"))
                                .add(linkTo(methodOn(AccountController.class).doTransactions(null, null)).withRel("transaction-group"));
                    } catch (Exception e) {
                        throw new RSAException(e.getMessage());
                    }
                })
                .collect(Collectors.toList());
        return new PageImpl<>(transactionModels, transactions.getPageable(), transactions.getTotalElements());
    }
}

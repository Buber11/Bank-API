package main.BankApp.service.account;

import main.BankApp.controller.AccountController;
import main.BankApp.dto.AccountModel;
import main.BankApp.dto.TransactionModel;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.Account;
import main.BankApp.model.account.AccountType;
import main.BankApp.model.account.Transaction;
import main.BankApp.service.rsa.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler extends RepresentationModelAssemblerSupport<Account, AccountModel> {

    private static final Logger logger = LoggerFactory.getLogger(AccountModelAssembler.class);
    private final VaultService vaultService;

    public AccountModelAssembler(VaultService vaultService) {
        super(AccountController.class, AccountModel.class);
        this.vaultService = vaultService;
    }

    @Override
    public AccountModel toModel(Account entity) {
        logger.info("Assembling AccountModel for account number: {}", entity.getAccountNumber());
        AccountModel accountModel = instantiateModel(entity);

        addSelfLink(accountModel);
        addTransactionLinks(accountModel, entity);

        try {
            populateAccountModel(entity, accountModel);
            logger.info("Successfully assembled AccountModel for account number: {}", entity.getAccountNumber());
        } catch (Exception e) {
            logger.error("Failed to assemble AccountModel for account number: {}", entity.getAccountNumber(), e);
            throw new RSAException("Failed to decrypt account data", e);
        }

        return accountModel;
    }

    private void addSelfLink(AccountModel accountModel) {
        accountModel.add(linkTo(methodOn(AccountController.class).getAllAccounts(null)).withSelfRel());
    }

    private void addTransactionLinks(AccountModel accountModel, Account entity) {
        accountModel.add(linkTo(methodOn(AccountController.class).getTransaction(
                entity.getAccountNumber(), "in", 0, 10, null)).withRel("transactions-in"));

        accountModel.add(linkTo(methodOn(AccountController.class).getTransaction(
                entity.getAccountNumber(), "out", 0, 10,null)).withRel("transactions-out"));
    }

    private void populateAccountModel(Account entity, AccountModel accountModel) {
        logger.info("Populating AccountModel for account number: {}", entity.getAccountNumber());

        accountModel.setAccountNumber(entity.getAccountNumber());

        try {
            String decryptedAccountType = vaultService.decrypt(entity.getAccountType());
            accountModel.setAccountType(AccountType.valueOf(decryptedAccountType));
            logger.info("Successfully set account type for account number: {}", entity.getAccountNumber());
        } catch (Exception e) {
            logger.error("Error decrypting account type for account number: {}", entity.getAccountNumber(), e);
            throw new RSAException("Error decrypting account type", e);
        }

        try {
            String decryptedBalance = vaultService.decrypt(entity.getBalance());
            accountModel.setBalance(decryptedBalance);
            accountModel.setCurrency(entity.getCurrency());
            logger.info("Successfully set balance for account number: {}", entity.getAccountNumber());
        } catch (Exception e) {
            logger.error("Error decrypting balance for account number: {}", entity.getAccountNumber(), e);
            throw new RSAException("Error decrypting balance", e);
        }

        accountModel.setTransactionsIn(toTransactionModel(entity.getTransactionsIn()));
        accountModel.setTransactionsOut(toTransactionModel(entity.getTransactionsOut()));

        logger.info("Successfully populated AccountModel for account number: {}", entity.getAccountNumber());
    }

    @Override
    public CollectionModel<AccountModel> toCollectionModel(Iterable<? extends Account> entities) {
        logger.info("Assembling collection of AccountModels");
        CollectionModel<AccountModel> accountModels = super.toCollectionModel(entities);
        accountModels.add(linkTo(methodOn(AccountController.class).getAllAccounts(null)).withSelfRel());
        logger.info("Successfully assembled collection of AccountModels");
        return accountModels;
    }

    public List<TransactionModel> toTransactionModel(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for conversion to TransactionModel");
            return Collections.emptyList();
        }

        return transactions.stream()
                .map(this::convertTransactionToModel)
                .collect(Collectors.toList());
    }

    private TransactionModel convertTransactionToModel(Transaction transaction) {
        try {
            return TransactionModel.builder()
                    .referenceNumber(vaultService.decrypt(transaction.getReferenceNumber()))
                    .amount(transaction.getAmount())
                    .payeeAccountNumber(transaction.getPayeeAccount().getAccountNumber())
                    .hostAccountNumber(transaction.getHostAccount().getAccountNumber())
                    .description(transaction.getDescription())
                    .transactionDate(transaction.getTransactionDate())
                    .currency(transaction.getCurrency())
                    .build()
                    .add(linkTo(methodOn(AccountController.class).doTransaction(null, null)).withRel("transactions"))
                    .add(linkTo(methodOn(AccountController.class).doTransactions(null, null)).withRel("transaction-group"));
        } catch (Exception e) {
            logger.error("Failed to convert transaction to TransactionModel", e);
            throw new RSAException("Failed to decrypt transaction data", e);
        }
    }

    public Page<TransactionModel> toTransactionModelPage(Page<Transaction> transactions) {
        logger.info("Converting Page<Transaction> to Page<TransactionModel>");
        List<TransactionModel> transactionModels = transactions.getContent().stream()
                .map(this::convertTransactionToModel)
                .collect(Collectors.toList());

        logger.info("Successfully converted Page<Transaction> to Page<TransactionModel>");
        return new PageImpl<>(transactionModels, transactions.getPageable(), transactions.getTotalElements());
    }
}
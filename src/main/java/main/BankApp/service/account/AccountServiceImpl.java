package main.BankApp.service.account;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.BankApp.common.Loggable;
import main.BankApp.dto.AccountModel;
import main.BankApp.dto.TransactionModel;
import main.BankApp.expection.AccountCreationException;
import main.BankApp.expection.BalanceUpdateException;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.*;
import main.BankApp.model.account.Currency;
import main.BankApp.repository.AccountRepository;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.UserRepository;
import main.BankApp.request.transaction.MultipleTransactionRequest;
import main.BankApp.request.transaction.SingleTransactionRequest;
import main.BankApp.service.currency.CurrencyService;
import main.BankApp.service.rsa.VaultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final String BANK_CODE = "6666";
    private final String COUNTRY_CODE_NUMERIC = "2521";
    private final String COUNTRY_CODE = "PL";
    private final AccountRepository accountRepository;
    private final VaultService vaultService;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final AccountModelAssembler accountModelAssembler;
    private final CurrencyService currencyService;

    private final Function<HttpServletRequest, Long> getUserIdFromJwt = e -> (Long) e.getAttribute("id");

    public String generateBankAccountNumber() {
        logger.info("Generating bank account number...");
        String clientAccountNumber = generateRandomClientAccountNumber();
        String partialAccountNumber = BANK_CODE + "6666" + clientAccountNumber;
        String controlSum = calculateControlSum(partialAccountNumber);
        String fullAccountNumber = controlSum + partialAccountNumber;
        logger.info("Generated bank account number: {}", fullAccountNumber);
        return fullAccountNumber;
    }

    private String generateRandomClientAccountNumber() {
        Random random = new Random();
        StringBuilder clientAccountNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            clientAccountNumber.append(random.nextInt(10));
        }
        logger.debug("Generated random client account number: {}", clientAccountNumber);
        return clientAccountNumber.toString();
    }

    private String calculateControlSum(String partialAccountNumber) {
        logger.debug("Calculating control sum for partial account number: {}", partialAccountNumber);
        String ibanWithoutControlSum = COUNTRY_CODE + "00" + partialAccountNumber;
        String shiftedIban = partialAccountNumber + COUNTRY_CODE_NUMERIC + "00";
        BigInteger numericIban = new BigInteger(shiftedIban);
        int mod = numericIban.mod(BigInteger.valueOf(97)).intValue();
        int controlSum = 98 - mod;
        logger.debug("Calculated control sum: {}", controlSum);
        return String.format("%02d", controlSum);
    }

    @Override
    public Account createAccount(UserAccount user) {
        logger.info("Creating account for user ID: {}", user.getUserId());
        try {
            Account account = Account.builder()
                    .accountNumber(generateBankAccountNumber())
                    .accountStatus(vaultService.encrypt(AccountStatus.ACTIVE.toString()))
                    .userAccount(user)
                    .balance(vaultService.encrypt("1000"))
                    .openDate(LocalDate.now())
                    .accountType(vaultService.encrypt(AccountType.PERSONAL.toString()))
                    .currency(Currency.PLN)
                    .build();
            logger.info("Account successfully created for User ID: {}", user.getUserId());
            return account;
        } catch (RSAException e) {
            logger.error("Encryption error while creating account for User ID: {}", user.getUserId(), e);
            throw new AccountCreationException("Error encrypting account data", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while creating account for User ID: {}", user.getUserId(), e);
            throw new AccountCreationException("An unexpected error occurred while creating the account", e);
        }
    }

    @Override
    public void createAccount(HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);
        logger.debug("Request to create account for user ID: {}", userId);
        Optional<UserAccount> userAccountOpt = userRepository.findById(userId);

        if (userAccountOpt.isEmpty()) {
            logger.warn("User account not found for ID: {}", userId);
            throw new EntityNotFoundException("User account not found for the given ID");
        }

        UserAccount userAccount = userAccountOpt.get();
        try {
            Account newAccount = createAccount(userAccount);
            logger.info("Account successfully saved for User ID: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to save account for User ID: {}", userId, e);
            throw new AccountCreationException("Failed to save the new account", e);
        }
    }

    @Override
    public List<AccountModel> getAlAccounts(HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);
        if (userId == null) {
            logger.error("User ID not found in the request attributes");
            throw new IllegalArgumentException("User ID is required");
        }

        logger.debug("Fetching accounts for user ID: {}", userId);
        List<Account> accountList = accountRepository.findByUserAccount_UserId(userId);
        if (accountList.isEmpty()) {
            logger.warn("No accounts found for user ID: {}", userId);
            return Collections.emptyList();
        }

        logger.info("Accounts found for user ID: {}", userId);
        return accountList.stream()
                .map(accountModelAssembler::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountModel> getAllAccountsActive() {
        logger.debug("Fetching all active accounts.");
        List<Account> activeAccounts = accountRepository.findByAccountStatus(AccountStatus.ACTIVE);
        logger.info("Number of active accounts found: {}", activeAccounts.size());
        return activeAccounts.stream()
                .map(accountModelAssembler::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Loggable
    public AccountModel convertCurrency(String accountNumber, Currency targetCurrency) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("The account number " + accountNumber + " not found"));

        try {
            BigDecimal currentBalance = new BigDecimal(vaultService.decrypt(account.getBalance()));

            if (account.getCurrency() != targetCurrency) {
                System.out.println(account.getCurrency());
                if (account.getCurrency() != Currency.PLN) {

                    currentBalance = convertToPLN(account.getCurrency(), currentBalance);
                    account.setCurrency(Currency.PLN);

                    if(targetCurrency == Currency.PLN){
                        String encryptedBalance = vaultService.encrypt(currentBalance.toString());
                        account.setBalance(encryptedBalance);
                        accountRepository.save(account);
                        return accountModelAssembler.toModel(account);
                    }

                }

                currentBalance = convertFromPLN(targetCurrency, currentBalance);
                account.setCurrency(targetCurrency);

                String encryptedBalance = vaultService.encrypt(currentBalance.toString());
                account.setBalance(encryptedBalance);
                accountRepository.save(account);
            }

            return accountModelAssembler.toModel(account);

        } catch (Exception e) {
            throw new RSAException("Error during currency conversion: " + e.getMessage());
        }
    }

    private BigDecimal convertToPLN(Currency sourceCurrency, BigDecimal amount) throws Exception {
        CurrencyRate currencyRate = currencyService.getCurrencyRate(sourceCurrency.toString());
        BigDecimal bidRate = BigDecimal.valueOf(currencyRate.getRates()[0].getBid());
        System.out.println(bidRate);
        return amount.multiply(bidRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal convertFromPLN(Currency targetCurrency, BigDecimal amount) throws Exception {
        CurrencyRate currencyRate = currencyService.getCurrencyRate(targetCurrency.toString());
        BigDecimal askRate = BigDecimal.valueOf(currencyRate.getRates()[0].getAsk());
        System.out.println(askRate);
        return amount.divide(askRate, 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void makeOwnSingleTransaction(HttpServletRequest request, SingleTransactionRequest transactionRequest) {
        long userId = getUserIdFromJwt.apply(request);
        logger.info("Making single transaction for user ID: {}", userId);
        AbstractMap.SimpleEntry<Account, Account> accountSimpleEntry = changeBalansAndGet(
                userId,
                transactionRequest.hostAccountNumber(),
                transactionRequest.payeeAccountNumber(),
                transactionRequest.amount(),
                transactionRequest.currency()
        );
        transactionService.saveTransaction(transactionRequest, accountSimpleEntry);
        logger.info("Single transaction made from account: {} to account: {}", transactionRequest.hostAccountNumber(), transactionRequest.payeeAccountNumber());
    }

    @Override
    public void doOwnMultipleTransaction(HttpServletRequest request, MultipleTransactionRequest multipleTransactionRequest) {
        long userId = getUserIdFromJwt.apply(request);
        logger.info("Making multiple transactions for user ID: {}", userId);
        for (String payeeAccountNumber : multipleTransactionRequest.payeeAccountNumber()) {
            AbstractMap.SimpleEntry<Account, Account> accountSimpleEntry = changeBalansAndGet(
                    userId,
                    multipleTransactionRequest.hostAccountNumber(),
                    payeeAccountNumber,
                    multipleTransactionRequest.amount(),
                    multipleTransactionRequest.currency()
                    );
            transactionService.saveTransaction(multipleTransactionRequest, accountSimpleEntry);
            logger.info("Multiple transaction made from account: {} to account: {}", multipleTransactionRequest.hostAccountNumber(), payeeAccountNumber);
        }
    }

    @Override
    public void makeCountryTransaction(HttpServletRequest request, SingleTransactionRequest transactionRequest) {
        // Implementation for making country transactions
    }

    @Override
    public void makeGroupTransaction(HttpServletRequest request, List<SingleTransactionRequest> transactionRequests) {
        // Implementation for group transactions
    }

    @Override
    public Page<TransactionModel> getTransactions(Pageable pageable, String accountNumber, String status) {
        logger.debug("Fetching transactions for account number: {} with status: {}", accountNumber, status);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return transactionService.getTransactions(pageable, accountNumber, status);
    }

    private AbstractMap.SimpleEntry<Account, Account> changeBalansAndGet(long userId, String accountNumber, String payeeAccountNumber, BigDecimal value, Currency currency) {
        logger.debug("Changing balance for user ID: {}, host account: {}, payee account: {}, amount: {}", userId, accountNumber, payeeAccountNumber, value);
        Optional<Account> hostTransactionAccount = accountRepository.findByAccountNumberAndUserAccount_UserId(accountNumber, userId);
        hostTransactionAccount.ifPresentOrElse(account -> {
            try {
                BigDecimal balans = new BigDecimal(vaultService.decrypt(account.getBalance()));
                if (balans.compareTo(value) < 0) {
                    logger.warn("Insufficient funds for account: {}. Current balance: {}, attempted deduction: {}", accountNumber, balans, value);
                    throw new BalanceUpdateException("Insufficient funds for account: " + accountNumber);
                }
                balans = balans.subtract(value);
                account.setBalance(vaultService.encrypt(balans.toString()));
                accountRepository.save(account);
                logger.info("Balance updated for account: {}. New balance: {}", accountNumber, balans);
            } catch (Exception e) {
                logger.error("Error while updating balance for account: {}", accountNumber, e);
                throw new BalanceUpdateException("Error while updating balance", e);
            }
        }, () -> {
            logger.error("Host account with number {} not found for user ID: {}", accountNumber, userId);
            throw new EntityNotFoundException("Account with number " + accountNumber + " not found for user ID: " + userId);
        });

        Optional<Account> accountToUpdate = accountRepository.findByAccountNumber(payeeAccountNumber);
        accountToUpdate.ifPresentOrElse(account -> {
            try {
                BigDecimal updatedValue = null;
                if(currency != account.getCurrency()){
                    updatedValue = convertToPLN(currency, value);
                    updatedValue = convertFromPLN(account.getCurrency(), updatedValue);
                }
                BigDecimal balans = new BigDecimal(vaultService.decrypt(account.getBalance()));
                balans = balans.add(updatedValue);
                account.setBalance(vaultService.encrypt(balans.toString()));
                accountRepository.save(account);
                logger.info("Balance updated for payee account: {}. New balance: {}", payeeAccountNumber, balans);
            } catch (Exception e) {
                logger.error("Error while updating balance for payee account: {}", payeeAccountNumber, e);
                throw new BalanceUpdateException("Error while updating balance", e);
            }
        }, () -> {
            logger.error("Payee account with number {} not found", payeeAccountNumber);
            throw new EntityNotFoundException("Account with number " + payeeAccountNumber + " not found");
        });

        return new AbstractMap.SimpleEntry<>(hostTransactionAccount.get(), accountToUpdate.get());
    }
}

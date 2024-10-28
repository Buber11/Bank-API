package main.BankApp.service.account;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.AccountClientView;
import main.BankApp.expection.AccountCreationException;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.Account;
import main.BankApp.model.account.AccountStatus;
import main.BankApp.model.account.AccountType;
import main.BankApp.repository.AccountRepository;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.UserRepository;
import main.BankApp.service.rsa.RSAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
    private final RSAService rsaService;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    private final Function<HttpServletRequest,Long> getUserIdFromJwt = e -> (Long) e.getAttribute("id");

    public String generateBankAccountNumber() {
        logger.info("Generating bank account number...");

        String clientAccountNumber = generateRandomClientAccountNumber();

        String partialAccountNumber = BANK_CODE + "6666" + clientAccountNumber;

        String controlSum = calculateControlSum(partialAccountNumber);

        String fullAccountNumber = controlSum + partialAccountNumber;

        return fullAccountNumber;
    }

    private String generateRandomClientAccountNumber() {
        Random random = new Random();
        StringBuilder clientAccountNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            clientAccountNumber.append(random.nextInt(10));
        }
        return clientAccountNumber.toString();
    }

    private String calculateControlSum(String partialAccountNumber) {
        String ibanWithoutControlSum = COUNTRY_CODE + "00" + partialAccountNumber;

        String shiftedIban = partialAccountNumber + COUNTRY_CODE_NUMERIC + "00";

        BigInteger numericIban = new BigInteger(shiftedIban);

        int mod = numericIban.mod(BigInteger.valueOf(97)).intValue();

        int controlSum = 98 - mod;

        return String.format("%02d", controlSum);
    }

    @Override
    public Account createAccount(UserAccount user) {
        logger.info("Creating account for user ID: {}", user.getUserId());
        try {
            Account account = Account.builder()
                    .accountNumber(generateBankAccountNumber())
                    .accountStatus(rsaService.encrypt(AccountStatus.ACTIVE.toString()))
                    .userAccount(user)
                    .balance(rsaService.encrypt(BigInteger.ZERO.toString()))
                    .openDate(LocalDate.now())
                    .accountType(rsaService.encrypt(AccountType.PERSONAL.toString()))
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
        Optional<UserAccount> userAccountOpt = userRepository.findById(userId);

        if (userAccountOpt.isEmpty()) {
            logger.warn("User account not found for ID: {}", userId);
            throw new EntityNotFoundException("User account not found for the given ID");
        }

        UserAccount userAccount = userAccountOpt.get();

        try {
            Account newAccount = createAccount(userAccount);
            accountRepository.save(newAccount);
            logger.info("Account successfully saved for User ID: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to save account for User ID: {}", userId, e);
            throw new AccountCreationException("Failed to save the new account", e);
        }
    }

    @Override
    public List<AccountClientView> getAlAccounts(HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);
        if (userId == null) {
            logger.error("User ID not found in the request attributes");
            throw new IllegalArgumentException("User ID is required");
        }

        List<Account> accountList = accountRepository.findByUserAccount_UserId(userId);
        if (accountList.isEmpty()) {
            logger.warn("No accounts found for user ID: {}", userId);
            return Collections.emptyList();
        }

        return accountList.stream()
                .map(this::mapAccountToClientView)
                .collect(Collectors.toList());
    }


    public AccountClientView mapAccountToClientView(Account account) {
        try {
            return AccountClientView.builder()
                    .accountNumber(account.getAccountNumber())
                    .accountType(AccountType.valueOf(rsaService.decrypt(account.getAccountType())))
                    .balance(rsaService.decrypt(account.getBalance()))
                    .transactions(account.getTransactions().stream()
                            .map(transactionService::mapTransactionToView)
                            .collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            logger.error("Decryption error: {}", e.getMessage(), e);
            throw new RSAException("Error while decrypting account data", e);
        }
    }

    @Override
    public List<AccountClientView> getAllAccountsActive() {
        return null;
    }




}


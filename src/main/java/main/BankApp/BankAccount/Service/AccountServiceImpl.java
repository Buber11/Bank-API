package main.BankApp.BankAccount.Service;

import lombok.RequiredArgsConstructor;
import main.BankApp.BankAccount.entity.Account;
import main.BankApp.BankAccount.entity.AccountStatus;
import main.BankApp.BankAccount.entity.AccountType;
import main.BankApp.BankAccount.repository.AccountRepository;
import main.BankApp.SecurityAlgorithms.RSA.RSAService;
import main.BankApp.User.ENTITY.UserAccount;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final String BANK_CODE = "6666";
    private final String COUNTRY_CODE_NUMERIC = "2521";
    private final String COUNTRY_CODE = "PL";

    private final AccountRepository accountRepository;
    private final RSAService rsaService;

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
                    .balance(rsaService.encrypt(BigInteger.ONE.toString()))
                    .openDate(LocalDate.now())
                    .accountType(rsaService.encrypt(AccountType.PERSONAL.toString()))
                    .build();
            logger.info("Account created for user ID: {}", user.getUserId());
            return account;
        } catch (Exception e) {
            logger.error("Error occurred while creating account for user ID: {}", user.getUserId(), e);
            throw new RuntimeException(e);
        }
    }
}


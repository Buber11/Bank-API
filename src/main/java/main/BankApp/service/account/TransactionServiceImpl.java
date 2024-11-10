package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.TransactionModel;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.Account;
import main.BankApp.model.account.Currency;
import main.BankApp.model.account.Transaction;
import main.BankApp.repository.TransactionRepository;
import main.BankApp.request.transaction.MultipleTransactionRequest;
import main.BankApp.request.transaction.SingleTransactionRequest;
import main.BankApp.request.transaction.TransactionRequest;
import main.BankApp.service.rsa.VaultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final Function<HttpServletRequest,Long> getUserIdFromJwt = e -> (Long) e.getAttribute("id");
    private final VaultService vaultService;
    private final AccountModelAssembler accountModelAssembler;

    @Override
    public List<TransactionModel> getAllForClient(HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);
        return null;
    }

    @Override
    public void saveTransaction(TransactionRequest request, AbstractMap.SimpleEntry<Account,Account> accountSimpleEntry) {
        Transaction transaction = createTransaction(request);

        transaction.setHostAccount(accountSimpleEntry.getKey());
        transaction.setPayeeAccount(accountSimpleEntry.getValue());
        transactionRepository.save(transaction);


    }

    @Override
    public Page<TransactionModel> getTransactions(Pageable pageable, String accountNumber, String status) {
        Page<Transaction> transactions;
        if (status.equalsIgnoreCase("in")) {
            transactions = transactionRepository.findByPayeeAccount_AccountNumber(accountNumber, pageable);
        } else if (status.equalsIgnoreCase("out")) {
            transactions = transactionRepository.findByHostAccount_AccountNumber(accountNumber, pageable);
        } else {
            throw new IllegalArgumentException("Invalid status: use 'in' or 'out'");
        }
        return accountModelAssembler.toTransactionModelPage(transactions);
    }


    private Transaction createTransaction(TransactionRequest transactionRequest) {
        String referenceNumber = generateReferenceNumber();

        if (transactionRequest instanceof SingleTransactionRequest(String hostAccountNumber,
                                                                   BigDecimal amount,
                                                                   String payeeAccountNumber,
                                                                   String description,
                                                                   String transactionType,
                                                                   Currency currency)) {
            try {
                return Transaction.builder()
                        .referenceNumber( vaultService.encrypt(referenceNumber) )
                        .transactionDate(LocalDateTime.now())
                        .transactionType(vaultService.encrypt(transactionType.toString()))
                        .amount(amount)
                        .description(description)
                        .currency(currency)
                        .hmac( vaultService.encrypt( new StringBuilder()
                                .append(referenceNumber)
                                .append(amount.toString())
                                .append(payeeAccountNumber)
                                .toString() )
                        )
                        .build();
            } catch (Exception e) {
                throw new RSAException(e.getMessage(),e);
            }
        } else if (transactionRequest instanceof MultipleTransactionRequest(String hostAccountNumber,
                                                                            BigDecimal amount,
                                                                            List<String> payeeAccountNumber,
                                                                            String description,
                                                                            String transactionType,
                                                                            Currency currency)) {
            try {
                return Transaction.builder()
                        .referenceNumber(vaultService.encrypt(referenceNumber))
                        .transactionDate(LocalDateTime.now())
                        .transactionType(vaultService.encrypt(transactionType.toString()))
                        .amount(amount)
                        .description(description)
                        .currency(currency)
                        .hmac(vaultService.encrypt(new StringBuilder()
                                .append(referenceNumber)
                                .append(amount.toString())
                                .append(payeeAccountNumber)
                                .toString())
                        )
                        .build();
            } catch (Exception e) {
                throw new RSAException(e.getMessage(), e);
            }
        }else {
            throw new IllegalArgumentException("Invalid transaction request type");
        }
    }

    private String generateReferenceNumber() {
        return "REF-" + System.currentTimeMillis();
    }

}

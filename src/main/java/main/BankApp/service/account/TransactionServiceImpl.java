package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.TransactionClientView;
import main.BankApp.expection.RSAException;
import main.BankApp.model.account.Account;
import main.BankApp.model.account.Transaction;
import main.BankApp.repository.TransactionRepository;
import main.BankApp.request.transaction.TransactionRequest;
import main.BankApp.service.rsa.RSAService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final Function<HttpServletRequest,Long> getUserIdFromJwt = e -> (Long) e.getAttribute("id");
    private final RSAService rsaService;

    @Override
    public List<TransactionClientView> getAllForClient(HttpServletRequest request) {
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
    public Page<Transaction> getTransactions(Pageable pageable, String accountNumber, String status) {

        if (status.equalsIgnoreCase("in")) {
            return transactionRepository.findByPayeeAccount_AccountNumber(accountNumber, pageable);
        } else if (status.equalsIgnoreCase("out")) {
            return transactionRepository.findByHostAccount_AccountNumber(accountNumber, pageable);
        } else {
            throw new IllegalArgumentException("Invalid status: use 'in' or 'out'");
        }

    }


    private Transaction createTransaction(TransactionRequest transactionRequest) {
        String referenceNumber = generateReferenceNumber();
        LocalDate transactionDate = transactionRequest.transactionDate().toLocalDate();
        System.out.println(transactionDate);
        try {
            return Transaction.builder()
                    .referenceNumber( rsaService.encrypt(referenceNumber) )
                    .transactionDate(transactionDate)
                    .transactionType(rsaService.encrypt(transactionRequest.transactionType().toString()))
                    .amount(transactionRequest.amount())
                    .description(transactionRequest.description())
                    .hmac( rsaService.encrypt( new StringBuilder()
                            .append(referenceNumber)
                            .append(transactionRequest.amount().toString())
                            .append(transactionRequest.payeeAccountNumber())
                            .toString() )
                    )
                    .build();
        } catch (Exception e) {
            throw new RSAException(e.getMessage(),e);
        }

    }

    private String generateReferenceNumber() {
        return "REF-" + System.currentTimeMillis();
    }

}

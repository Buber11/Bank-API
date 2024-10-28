package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Transaction;
import main.BankApp.repository.TransactionRepository;
import main.BankApp.request.transaction.TransactionRequest;
import main.BankApp.service.rsa.RSAService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final Function<HttpServletRequest,Long> getUserIdFromJwt = e -> (Long) e.getAttribute("id");
    @Override
    public List<TransactionClientView> getAllForClient(HttpServletRequest request) {
        Long userId = getUserIdFromJwt.apply(request);
        return null;
    }

    @Override
    public void saveTransaction(HttpServletRequest httpServletRequest, TransactionRequest transactionRequest) {

    }

    public Function<Transaction,TransactionClientView> mappingForView = transaction -> {
        BigDecimal formattedAmount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);

        LocalDate transactionDate = transaction.getTransactionDate();
        if (transactionDate == null || transactionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid transaction date");
        }

        return TransactionClientView.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .description(transaction.getDescription())
                .transactionDate(transactionDate)
                .amount(formattedAmount)
                .payeeAccountNumber(transaction.getPayeeAccountNumber())
                .build();
    };

    public TransactionClientView mapTransactionToView(Transaction transaction) {
        BigDecimal formattedAmount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);

        LocalDate transactionDate = transaction.getTransactionDate();
        if (transactionDate == null || transactionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid transaction date");
        }

        return TransactionClientView.builder()
                .referenceNumber(transaction.getReferenceNumber())
                .description(transaction.getDescription())
                .transactionDate(transactionDate)
                .amount(formattedAmount)
                .payeeAccountNumber(transaction.getPayeeAccountNumber())
                .build();
    }


}

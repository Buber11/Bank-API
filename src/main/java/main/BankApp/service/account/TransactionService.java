package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Transaction;
import main.BankApp.request.transaction.TransactionRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public interface TransactionService {

    List<TransactionClientView> getAllForClient(HttpServletRequest request);
    void saveTransaction(HttpServletRequest httpServletRequest, TransactionRequest transactionRequest);
    TransactionClientView mapTransactionToView(Transaction transaction);


}

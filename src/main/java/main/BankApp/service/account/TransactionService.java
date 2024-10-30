package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Account;
import main.BankApp.model.account.Transaction;
import main.BankApp.request.transaction.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.function.Function;

public interface TransactionService {

    List<TransactionClientView> getAllForClient(HttpServletRequest request);
    void saveTransaction(TransactionRequest request, AbstractMap.SimpleEntry<Account,Account> accountSimpleEntry);
    Page<Transaction> getTransactions(Pageable pageable, String accountNumber, String status);
}

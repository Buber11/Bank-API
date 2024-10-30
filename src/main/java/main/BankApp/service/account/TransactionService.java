package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Account;
import main.BankApp.request.transaction.SingleTransactionRequest;
import main.BankApp.request.transaction.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.AbstractMap;
import java.util.List;

public interface TransactionService {

    List<TransactionClientView> getAllForClient(HttpServletRequest request);
    void saveTransaction(TransactionRequest request, AbstractMap.SimpleEntry<Account,Account> accountSimpleEntry);
    Page<TransactionClientView> getTransactions(Pageable pageable, String accountNumber, String status);
}

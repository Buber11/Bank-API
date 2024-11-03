package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.TransactionModel;
import main.BankApp.model.account.Account;
import main.BankApp.request.transaction.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.AbstractMap;
import java.util.List;

public interface TransactionService {

    List<TransactionModel> getAllForClient(HttpServletRequest request);
    void saveTransaction(TransactionRequest request, AbstractMap.SimpleEntry<Account,Account> accountSimpleEntry);
    Page<TransactionModel> getTransactions(Pageable pageable, String accountNumber, String status);
}

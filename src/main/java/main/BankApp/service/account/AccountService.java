package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.AccountClientView;
import main.BankApp.dto.TransactionClientView;
import main.BankApp.model.account.Account;
import main.BankApp.model.user.UserAccount;
import main.BankApp.request.transaction.MultipleTransactionRequest;
import main.BankApp.request.transaction.SingleTransactionRequest;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {

    Account createAccount(UserAccount user);
    void createAccount(HttpServletRequest request);

    List<AccountClientView> getAlAccounts(HttpServletRequest request);
    List<AccountClientView> getAllAccountsActive();

    void makeOwnSingleTransaction(HttpServletRequest request, SingleTransactionRequest transactionRequest);
    void doOwnMultipleTransaction(HttpServletRequest request, MultipleTransactionRequest multipleTransactionRequest);
    void makeCountryTransaction(HttpServletRequest request, SingleTransactionRequest transactionRequest);
    void makeGroupTransaction(HttpServletRequest request, List<SingleTransactionRequest> transactionRequests);

    Page<TransactionClientView> getTransactions(Pageable pageable, String accountNumber, String status);


}

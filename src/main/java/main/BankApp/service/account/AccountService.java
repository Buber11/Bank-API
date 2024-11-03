package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.AccountModel;
import main.BankApp.dto.TransactionModel;
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

    List<AccountModel> getAlAccounts(HttpServletRequest request);
    List<AccountModel> getAllAccountsActive();

    void makeOwnSingleTransaction(HttpServletRequest request, SingleTransactionRequest transactionRequest);
    void doOwnMultipleTransaction(HttpServletRequest request, MultipleTransactionRequest multipleTransactionRequest);
    void makeCountryTransaction(HttpServletRequest request, SingleTransactionRequest transactionRequest);
    void makeGroupTransaction(HttpServletRequest request, List<SingleTransactionRequest> transactionRequests);

    Page<TransactionModel> getTransactions(Pageable pageable, String accountNumber, String status);


}

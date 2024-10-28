package main.BankApp.service.account;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.AccountClientView;
import main.BankApp.model.account.Account;
import main.BankApp.model.user.UserAccount;

import java.util.List;

public interface AccountService {

    Account createAccount(UserAccount user);
    void createAccount(HttpServletRequest request);

    List<AccountClientView> getAlAccounts(HttpServletRequest request);
    List<AccountClientView> getAllAccountsActive();

}

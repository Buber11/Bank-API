package main.BankApp.service.account;

import main.BankApp.model.account.Account;
import main.BankApp.model.user.UserAccount;

public interface AccountService {

    Account createAccount(UserAccount user);

}

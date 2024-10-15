package main.BankApp.BankAccount.Service;

import main.BankApp.BankAccount.entity.Account;
import main.BankApp.User.ENTITY.UserAccount;

public interface AccountService {

    Account createAccount(UserAccount user);

}

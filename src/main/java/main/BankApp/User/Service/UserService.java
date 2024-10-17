package main.BankApp.User.Service;

import main.BankApp.User.ENTITY.UserAccount;

public interface UserService {

    void lockAccount(UserAccount userAccount);
    UserAccount get(long id);

}

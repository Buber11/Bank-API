package main.BankApp.service.user;

import main.BankApp.model.user.UserAccount;

public interface UserService {

    void lockAccount(UserAccount userAccount);
    UserAccount get(long id);

}

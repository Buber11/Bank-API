package main.BankApp.service.user;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.UserDataView;
import main.BankApp.model.user.UserAccount;

public interface UserService {

    void lockAccount(UserAccount userAccount);

    UserAccount getUser(long id);

    UserDataView getUserView(HttpServletRequest request);
}

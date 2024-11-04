package main.BankApp.service.user;

import jakarta.servlet.http.HttpServletRequest;
import main.BankApp.dto.UserModel;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.model.user.UserAccount;
import org.apache.catalina.User;

import java.util.List;

public interface UserService {

    void lockAccount(UserAccount userAccount);

    UserAccount getUser(long id);

    UserModel getUserView(HttpServletRequest request);

    List<UserModel> getUsersView(StatusAccount statusAccount);

    List<UserModel> getAllUsers();

    void changeUserStatus(long userId, StatusAccount statusAccount);

    void delete(long userId);
}

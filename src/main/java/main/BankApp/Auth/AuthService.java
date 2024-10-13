package main.BankApp.Auth;

import main.BankApp.User.Request.SignupRequest;

public interface AuthService {

    void signup(SignupRequest request) throws Exception;

}

package main.BankApp.Auth;

import main.BankApp.Auth.Request.SignupRequest;

public interface AuthService {

    void signup(SignupRequest request) throws Exception;

}

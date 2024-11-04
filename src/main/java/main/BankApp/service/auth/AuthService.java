package main.BankApp.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.BankApp.dto.UserModel;
import main.BankApp.request.auth.LoginRequest;
import main.BankApp.request.auth.SignupRequest;

public interface AuthService {

    String signup(SignupRequest request);

    UserModel authenticate(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response);

    void refreshToken(HttpServletRequest request,  HttpServletResponse response);

    void logout(HttpServletRequest request);

    void deactivate(HttpServletRequest request);

}

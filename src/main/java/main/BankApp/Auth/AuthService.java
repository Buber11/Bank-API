package main.BankApp.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.BankApp.Auth.Request.LoginRequest;
import main.BankApp.Auth.Request.SignupRequest;
import org.springframework.security.core.AuthenticationException;

public interface AuthService {

    void signup(SignupRequest request);

    void authenticate(LoginRequest request, HttpServletResponse response);

    void refreshToken(HttpServletRequest request,  HttpServletResponse response);

}
package main.BankApp.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.BankApp.Auth.Request.LoginRequest;
import main.BankApp.Auth.Request.SignupRequest;
import main.BankApp.app.Loggable;
import org.springframework.security.core.AuthenticationException;

public interface AuthService {

    void signup(SignupRequest request);

    void authenticate(LoginRequest request, HttpServletRequest httpServletRequest,HttpServletResponse response);

    void refreshToken(HttpServletRequest request,  HttpServletResponse response);

    void logout(HttpServletRequest request);

}

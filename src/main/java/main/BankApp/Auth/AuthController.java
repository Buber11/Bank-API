package main.BankApp.Auth;

import com.sun.jdi.event.ExceptionEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import main.BankApp.Auth.Request.LoginRequest;
import main.BankApp.Auth.Request.SignupRequest;
import main.BankApp.Response.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public final class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseUtil.buildSuccessResponse("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response){
        authService.authenticate(request, response);
        return ResponseUtil.buildSuccessResponse("Authentication successful");
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response){
        authService.refreshToken(request,response);
        return ResponseUtil.buildSuccessResponse("The token is refreshed");
    }


}



package main.BankApp.Auth;

import com.sun.jdi.event.ExceptionEvent;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseUtil.buildSuccessResponse("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request,HttpServletRequest httpServletRequest, HttpServletResponse response){
        authService.authenticate(request, httpServletRequest ,response);
        return ResponseUtil.buildSuccessResponse("Authentication successful");
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response){
        authService.refreshToken(request,response);
        return ResponseUtil.buildSuccessResponse("The token is refreshed");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        authService.logout(request);
        return ResponseUtil.buildSuccessResponse("Your session isn't active");
    }


}



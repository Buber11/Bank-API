package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserModel;
import main.BankApp.service.auth.AuthService;
import main.BankApp.request.auth.LoginRequest;
import main.BankApp.request.auth.SignupRequest;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest signupRequest) {
        String barCodeUrl = authService.signup(signupRequest);
        return ResponseUtil.buildSuccessResponse(barCodeUrl);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,HttpServletRequest httpServletRequest, HttpServletResponse response){
        UserModel userModel = authService.authenticate(request, httpServletRequest ,response);
        return ResponseEntity.ok(userModel);
    }

    @PostMapping("/verify")
    public ResponseEntity verify(){

        return null;
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

    @PatchMapping("/deactivate")
    public ResponseEntity deactivate(HttpServletRequest request){
        authService.deactivate(request);
        return ResponseEntity.noContent().build();
    }


}



package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserDataView;
import main.BankApp.request.auth.SignupRequest;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.UserRepository;
import main.BankApp.service.user.UserService;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("api/user/getData")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Object> getUserView(HttpServletRequest request){
        UserDataView view = userService.getUserView(request);
        return ResponseUtil.buildSuccessResponse(view);
    }

}

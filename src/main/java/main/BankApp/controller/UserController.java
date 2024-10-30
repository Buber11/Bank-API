package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserDataView;
import main.BankApp.service.user.UserService;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getData")
    public ResponseEntity<Object> getUserView(HttpServletRequest request){
        UserDataView view = userService.getUserView(request);
        return ResponseUtil.buildSuccessResponse(view);
    }

}

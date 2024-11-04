package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserModel;
import main.BankApp.model.user.StatusAccount;
import main.BankApp.service.user.UserService;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<Object> getUserView(HttpServletRequest request){
        UserModel view = userService.getUserView(request);
        return ResponseUtil.buildSuccessResponse(view);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false, name = "st") StatusAccount statusAccount
    ) {

        var accounts = (statusAccount == null) ?
                userService.getAllUsers() :
                userService.getUsersView(statusAccount);

        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/users/{id}/{status}")
    public ResponseEntity changeUserStatus(@PathVariable("id") long userId,
                                           @PathVariable("status") StatusAccount statusAccount){
        userService.changeUserStatus(userId,statusAccount);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("users/{userId}")
    public ResponseEntity deleteUser(@PathVariable long userId){
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }


}

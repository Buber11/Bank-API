package main.BankApp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import main.BankApp.dto.UserDataView;
import main.BankApp.service.user.UserService;
import main.BankApp.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<Object> getUserView(HttpServletRequest request){
        UserDataView view = userService.getUserView(request);
        return ResponseUtil.buildSuccessResponse(view);
    }



}

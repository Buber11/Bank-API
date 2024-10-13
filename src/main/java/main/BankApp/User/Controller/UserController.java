package main.BankApp.User.Controller;

import jakarta.servlet.http.HttpServletResponse;
import main.BankApp.User.Request.UserRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/add")
    public String addUser(@RequestBody UserRequest userRequest){
        System.out.println(userRequest);

        return "GIT";
    }

}

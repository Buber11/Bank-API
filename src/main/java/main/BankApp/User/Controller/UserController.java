package main.BankApp.User.Controller;

import main.BankApp.Auth.Request.SignupRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/add")
    public String addUser(@RequestBody SignupRequest signupRequest){
        System.out.println(signupRequest);

        return "GIT";
    }

}

package main.BankApp.controller;

import main.BankApp.request.auth.SignupRequest;
import main.BankApp.model.user.UserAccount;
import main.BankApp.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public UserAccount addUser(@RequestBody SignupRequest signupRequest){
        return userRepository.findByUsername("jan.kowal").get();
    }

}

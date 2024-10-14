package main.BankApp.User.Controller;

import main.BankApp.Auth.Request.SignupRequest;
import main.BankApp.User.ENTITY.UserAccount;
import main.BankApp.User.Repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping()
    public UserAccount addUser(@RequestBody SignupRequest signupRequest){
        return userRepository.findByUsername("jan.kowalski").get();
    }

}

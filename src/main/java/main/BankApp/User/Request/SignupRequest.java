package main.BankApp.User.Request;

import lombok.Builder;

@Builder
public record SignupRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String countryOfOrigin,
        String phoneNumber,
        String pesel

) {

}

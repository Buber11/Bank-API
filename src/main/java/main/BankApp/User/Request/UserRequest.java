package main.BankApp.User.Request;

import lombok.Builder;

@Builder
public record UserRequest(
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

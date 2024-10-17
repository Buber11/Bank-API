package main.BankApp.Auth.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import main.BankApp.Auth.Request.annotation.Pesel;

@Builder
@Pesel
public record SignupRequest(
        @Size(min = 5, max = 10, message = "Username must be between 5 and 10 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Username cannot contain numbers or special characters")
        String username,

        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(regexp = ".*[A-Za-z].*", message = "Password must contain at least one letter")
        @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
        String password,

        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format")
        String email,

        @NotNull(message = "First name cannot be null")
        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
        String firstName,

        @NotNull(message = "Last name cannot be null")
        @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
        String lastName,

        @NotNull(message = "Country of origin cannot be null")
        String countryOfOrigin,
        @Pattern(regexp = "man|woman", message = "Sex must be 'man' or 'woman'")
        String sex,

        @Pattern(regexp = "^\\d{9}$", message = "Phone number must be exactly 9 digits")
        String phoneNumber,

        String pesel

) {

}

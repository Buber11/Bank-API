package main.BankApp.User.Contact.Model.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ContactRequest(

        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Name can only contain letters, spaces, numbers")
        String name,
        @NotNull(message = "Account number cannot be null")
        @Pattern(regexp = "^\\d{26}$", message = "Account number must be exactly 26 digits")
        String numberAccount

) {
}

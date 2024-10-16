package main.BankApp.User.Contact.Model.Response;

import lombok.Builder;

@Builder
public record ContactResponse(
        String name,
        String numberAccount
) {
}

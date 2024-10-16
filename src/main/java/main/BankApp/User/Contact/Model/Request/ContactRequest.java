package main.BankApp.User.Contact.Model.Request;

import lombok.Builder;

@Builder
public record ContactRequest(
        String name,
        String numberAccount,
        String numberOfUse,
        String dateOfLastUse
) {
}

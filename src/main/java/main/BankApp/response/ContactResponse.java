package main.BankApp.response;

import lombok.Builder;

public record ContactResponse(
        long id,
        String name,
        String numberAccount
) {
}

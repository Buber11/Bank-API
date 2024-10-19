package main.BankApp.response;

import lombok.Builder;

@Builder
public record ContactResponse(
        String name,
        String numberAccount
) {
}

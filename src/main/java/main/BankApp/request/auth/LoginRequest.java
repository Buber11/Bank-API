package main.BankApp.request.auth;

public record LoginRequest(String username, String password, String code) {
}

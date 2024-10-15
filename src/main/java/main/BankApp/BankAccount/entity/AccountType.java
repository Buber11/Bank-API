package main.BankApp.BankAccount.entity;

public enum AccountType {
    BUSINESS("Business account"),
    PERSONAL("Personal account");

    private final String description;

    // Constructor for each account type
    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}

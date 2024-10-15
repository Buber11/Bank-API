package main.BankApp.BankAccount.entity;

public enum AccountStatus {
    ACTIVE("Account is active"),
    INACTIVE("Account is inactive"),
    SUSPENDED("Account is suspended");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
}

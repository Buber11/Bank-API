package main.BankApp.User.ENTITY;

public enum ActivityLogAction {
    LOGIN("User logged in"),
    LOGOUT("User logged out"),
    TRANSACTION_COMPLETED("Transaction completed"),
    TRANSACTION_FAILED("Transaction failed"),
    PASSWORD_CHANGED("Password changed"),
    ACCOUNT_DELETED("Account deleted"),
    PROFILE_UPDATED("Profile updated");

    private final String description;

    ActivityLogAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

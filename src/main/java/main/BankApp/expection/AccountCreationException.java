package main.BankApp.expection;

public class AccountCreationException extends RuntimeException{


    public AccountCreationException(String message) {
        super(message);
    }

    public AccountCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

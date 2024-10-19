package main.BankApp.expection;

public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }
    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

}

package main.BankApp.expection;

public class LogNotFoundException extends RuntimeException {

    public LogNotFoundException() {
        super("Log not found.");
    }

    public LogNotFoundException(String message) {
        super(message);
    }

    public LogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogNotFoundException(Throwable cause) {
        super(cause);
    }
}

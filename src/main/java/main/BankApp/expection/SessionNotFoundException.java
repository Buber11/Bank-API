package main.BankApp.expection;

public class SessionNotFoundException extends RuntimeException {


    public SessionNotFoundException() {
        super("Session not found");
    }


    public SessionNotFoundException(String message) {
        super(message);
    }


    public SessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


    public SessionNotFoundException(Throwable cause) {
        super(cause);
    }
}

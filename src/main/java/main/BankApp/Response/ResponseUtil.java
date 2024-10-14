package main.BankApp.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    private ResponseUtil(){
        throw new AssertionError();
    }
    public static ResponseEntity<String> buildSuccessResponse(String message) {
        return ResponseEntity.ok(message);
    }

    public static ResponseEntity<String> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(message);
    }

}

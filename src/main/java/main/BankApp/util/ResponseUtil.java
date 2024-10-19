package main.BankApp.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public final class ResponseUtil {

    private ResponseUtil(){
        throw new AssertionError();
    }
    public static ResponseEntity<String> buildSuccessResponse(String message) {
        return ResponseEntity.ok(message);
    }

    public static ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
    public static ResponseEntity<Object> buildErrorResponse(HttpStatus status, List messages) {
        return ResponseEntity.status(status).body(Map.of("errors", messages));
    }
    public static ResponseEntity<List<?>> buildSuccessResponse(List objects){
        return ResponseEntity.ok(objects);
    }

}

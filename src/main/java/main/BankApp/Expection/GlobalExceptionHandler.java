package main.BankApp.Expection;

import jakarta.persistence.EntityNotFoundException;
import main.BankApp.Expection.DuplicateException;

import main.BankApp.Expection.RSAException;
import main.BankApp.Response.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(Exception ex) {

        HttpStatus status = mapExceptionToStatus(ex);
        String message = ex.getMessage();

        return ResponseUtil.buildErrorResponse(status,message);
    }

    private HttpStatus mapExceptionToStatus(Exception ex) {
        if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof DuplicateException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof RSAException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof RuntimeException) {
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ex.getBindingResult().getGlobalErrors().stream()
                .map(globalError -> globalError.getObjectName() + " - " + globalError.getDefaultMessage())
                .forEach(errors::add);

        return ResponseUtil.buildErrorResponse(status,errors);
    }


}

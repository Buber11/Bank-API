package main.BankApp.expection;

import jakarta.persistence.EntityNotFoundException;

import main.BankApp.service.account.AccountServiceImpl;
import main.BankApp.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        HttpStatus status = mapExceptionToStatus(ex);
        String message = ex.getMessage();

        logger.error("Exception handled: {} - {}", ex.getClass().getSimpleName(), message, ex);
        return ResponseUtil.buildErrorResponse(status, message);
    }

    private HttpStatus mapExceptionToStatus(Exception ex) {
        if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof EntityNotFoundException || ex instanceof UserNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof DuplicateException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof RSAException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (ex instanceof BalanceUpdateException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof MethodArgumentNotValidException) {
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

        logger.warn("Validation failed: {}", errors);
        return ResponseUtil.buildErrorResponse(status, errors);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleUnhandledExceptions(Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred. Please contact support.";

        logger.error("Unhandled exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseUtil.buildErrorResponse(status, message);
    }

    @ExceptionHandler({AccountCreationException.class, BalanceUpdateException.class})
    public ResponseEntity<Object> handleCustomExceptions(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        logger.error("Custom exception handled: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseUtil.buildErrorResponse(status, ex.getMessage());
    }
}

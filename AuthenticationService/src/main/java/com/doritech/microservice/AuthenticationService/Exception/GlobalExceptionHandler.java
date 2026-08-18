package com.doritech.microservice.AuthenticationService.Exception;

import com.doritech.microservice.AuthenticationService.Entity.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity(ex.getMessage(), 404, null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ResponseEntity(ex.getMessage(), 401, null);
    }

    // NEW: Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity("Validation failed", 400, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        return new ResponseEntity("Internal server error: " + ex.getMessage(), 500, null);
    }
}
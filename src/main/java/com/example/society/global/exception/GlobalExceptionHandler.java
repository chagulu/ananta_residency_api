package com.example.society.global.exception;


import org.springframework.http.HttpStatus; // Import HttpStatus for setting status codes
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus; // Import ResponseStatus for clarity
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors (e.g., from @Valid annotation in DTOs).
     * Returns a 400 Bad Request with a message containing all validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Explicitly set HTTP status to 400
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors()
            .stream()
            .map(error -> {
                if (error instanceof FieldError) {
                    return ((FieldError) error).getDefaultMessage();
                }
                return error.getDefaultMessage(); // Fallback for non-field errors
            })
            .collect(Collectors.joining("; ")); // Join multiple errors with a semicolon

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", errorMessage);
        response.put("data", null); // No specific data for validation errors

        return ResponseEntity.badRequest().body(response);
    }



    /**
     * Handles UserAlreadyExistsException, typically thrown when a user tries to register
     * with an existing mobile number or username.
     * Returns a 400 Bad Request with the specific error message.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Explicitly set HTTP status to 400
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage()); // Get the message directly from the exception
        response.put("data", null); // No specific data for this error

        return ResponseEntity.badRequest().body(response);
    }

 

    /**
     * A generic exception handler for any other unhandled exceptions that occur
     * within the application.
     * Returns a 500 Internal Server Error with a generic message.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Explicitly set HTTP status to 500
    public ResponseEntity<Map<String, Object>> handleAllUncaughtException(Exception ex) {
        // Log the full exception for debugging in your server logs
        System.err.println("An unexpected server error occurred: " + ex.getMessage());
        ex.printStackTrace(); // Print stack trace for detailed error analysis

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "An unexpected server error occurred. Please try again later.");
        response.put("data", null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
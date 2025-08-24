package com.appointment.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the backend.
 * Catches exceptions thrown by controllers and services,
 * and returns a user-friendly JSON response with the appropriate HTTP status
 * code.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException.
     * This usually happens when validation fails (ID too long, description
     * missing, etc.).
     * Returns a 400 Bad Request, or 409 Conflict if the error message mentions
     * "already exists".
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        // If the exception has no message, provide a default one
        body.put("error", ex.getMessage() == null ? "Invalid request." : ex.getMessage());

        // If the error is about duplication, return HTTP 409 instead of 400
        HttpStatus status = (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("already exists"))
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(body);
    }

    /**
     * Handles JSON parsing errors.
     * This usually happens when the client sends invalid JSON or an incorrectly
     * formatted date.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleParseError(HttpMessageNotReadableException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Malformed JSON or invalid date. Use yyyy-MM-dd.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles any other unexpected exceptions.
     * Returns a generic error message with a 500 status code.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Unexpected server error. Please try again.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

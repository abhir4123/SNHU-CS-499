package com.appointment.backend.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler.
 * Converts common runtime errors into clean JSON responses
 * with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Helper to wrap error messages in JSON + status
    private ResponseEntity<Map<String, String>> json(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message == null ? "Invalid request." : message);
        return ResponseEntity.status(status).body(body);
    }

    /** Handles bean validation errors (e.g. invalid login/register DTOs). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        // Collect field errors into one readable message
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        if (msg.isBlank())
            msg = "Validation failed.";
        return json(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * Handles explicit IllegalArgumentExceptions thrown in services/controllers.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        String msg = ex.getMessage();
        HttpStatus status = (msg != null && msg.toLowerCase().contains("already exists"))
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return json(status, msg == null ? "Invalid request." : msg);
    }

    /** Handles malformed JSON or invalid date format in request bodies. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleParseError(HttpMessageNotReadableException ex) {
        return json(HttpStatus.BAD_REQUEST,
                "Malformed JSON or invalid date. Use yyyy-MM-dd, >= 2000-01-01.");
    }

    /** Handles missing query parameters (e.g., range start/end). */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
        return json(HttpStatus.BAD_REQUEST, "Missing required query parameter: " + ex.getParameterName());
    }

    /** Handles wrong parameter type/format (e.g., badly formatted date). */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, DateTimeParseException.class })
    public ResponseEntity<Map<String, String>> handleTypeMismatch(Exception ex) {
        return json(HttpStatus.BAD_REQUEST, "Invalid parameter value. Dates must use yyyy-MM-dd.");
    }

    /** Fallback handler for unexpected errors. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        return json(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error. Please try again.");
    }
}

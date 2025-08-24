package com.appointment.backend.auth;

/**
 * Simple runtime exception used when a request is unauthorized.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

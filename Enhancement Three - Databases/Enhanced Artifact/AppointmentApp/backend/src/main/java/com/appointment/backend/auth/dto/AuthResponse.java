package com.appointment.backend.auth.dto;

/**
 * DTO for login success responses.
 * Contains only the generated JWT token.
 */
public class AuthResponse {
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

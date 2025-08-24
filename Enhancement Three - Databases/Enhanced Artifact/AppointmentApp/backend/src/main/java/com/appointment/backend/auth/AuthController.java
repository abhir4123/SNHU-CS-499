package com.appointment.backend.auth;

import com.appointment.backend.auth.dto.AuthResponse;
import com.appointment.backend.auth.dto.LoginRequest;
import com.appointment.backend.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication endpoints.
 * Supports register and login with JWT generation.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${app.cors.allowed-origin}", allowCredentials = "true")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    /** Registers a new user if email is not taken. */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            // Duplicate email -> 409 Conflict
            return ResponseEntity.status(409).body(Map.of("error", "Email already registered."));
        }
        // Store hashed password
        String hash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
        userRepo.save(new User(req.getEmail(), hash));
        // Success -> return confirmation message
        return ResponseEntity.ok(Map.of("message", "Registration successful."));
    }

    /** Logs user in and returns JWT if credentials are valid. */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail()).orElse(null);
        if (user == null || !BCrypt.checkpw(req.getPassword(), user.getPasswordHash())) {
            // Wrong credentials -> 401 Unauthorized
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password."));
        }
        // Return signed JWT
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

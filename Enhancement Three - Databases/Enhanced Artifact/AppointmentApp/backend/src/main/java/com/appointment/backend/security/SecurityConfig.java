package com.appointment.backend.security;

import com.appointment.backend.auth.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Spring Security configuration.
 * - CORS: allows the configured frontend origin.
 * - CSRF: disabled for stateless API.
 * - AuthZ: GET /appointments/** is public; POST/DELETE require authentication.
 * - JWT filter: validates tokens and sets the security context.
 */
@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final String allowedOrigin;

    public SecurityConfig(JwtService jwtService, @Value("${app.cors.allowed-origin}") String allowedOrigin) {
        this.jwtService = jwtService;
        this.allowedOrigin = allowedOrigin;
    }

    /** Defines the HTTP security pipeline and authorization rules. */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService);

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    // Restrict cross-origin requests to the configured frontend origin
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of(allowedOrigin));
                    cfg.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }))
                .authorizeHttpRequests(auth -> auth
                        // Public: authentication endpoints
                        .requestMatchers("/auth/**").permitAll()
                        // Public: read endpoints for appointments
                        .requestMatchers(HttpMethod.GET, "/appointments/**").permitAll()
                        // Protected: write endpoints require a valid JWT
                        .requestMatchers(HttpMethod.POST, "/appointments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/appointments/**").authenticated()
                        // Allow everything else (for example docs root page)
                        .anyRequest().permitAll())
                // Inject JWT validation before username/password filter
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Exposes the AuthenticationManager bean (not actively used for JWT
     * verification,
     * but useful if username/password flows want to be added later).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}

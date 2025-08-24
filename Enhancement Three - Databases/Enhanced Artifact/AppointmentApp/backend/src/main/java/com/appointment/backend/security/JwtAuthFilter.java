package com.appointment.backend.security;

import com.appointment.backend.auth.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that reads the Authorization header, validates a Bearer token,
 * and populates the Spring Security context with a simple authenticated user.
 * If the token is missing/invalid, the request proceeds unauthenticated.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // Validate token and use subject (email) as the principal
                String subject = jwtService.validateAndGetSubject(token);
                AbstractAuthenticationToken auth = new AbstractAuthenticationToken(
                        AuthorityUtils.createAuthorityList("ROLE_USER")) {
                    @Override
                    public Object getCredentials() {
                        return token;
                    }

                    @Override
                    public Object getPrincipal() {
                        return subject;
                    }
                };
                auth.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                // Invalid token -> clear context and continue;
                // protected endpoints will still be blocked by SecurityConfig rules.
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}

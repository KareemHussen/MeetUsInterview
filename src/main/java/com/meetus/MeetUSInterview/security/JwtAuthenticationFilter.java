package com.meetus.MeetUSInterview.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtUtil jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtUtil jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        
        log.debug("Processing request to: {}", requestURI);
        log.debug("Authorization header: {}", authHeader != null ? "Present" : "Missing");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.debug("Extracted JWT token: {}...", jwt.substring(0, Math.min(20, jwt.length())));
            
            final String userId = jwtService.extractUserId(jwt);
            log.debug("Extracted user ID from token: {}", userId);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("Current authentication: {}", authentication != null ? "Already authenticated" : "Not authenticated");

            if (userId != null && authentication == null) {
                log.debug("Loading user details for ID: {}", userId);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);
                log.debug("User details loaded successfully for ID: {}", userId);

                boolean isTokenValid = jwtService.isTokenValid(jwt, userDetails);
                log.debug("Token validation result: {}", isTokenValid);
                
                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Successfully authenticated user ID: {}", userId);
                } else {
                    log.warn("Token validation failed for user ID: {}", userId);
                }
            } else {
                if (userId == null) {
                    log.warn("Could not extract user ID from token");
                }
                if (authentication != null) {
                    log.debug("User already authenticated, skipping");
                }
            }

            filterChain.doFilter(request, response);
            log.debug("Request processing completed for: {}", requestURI);
        } catch (Exception exception) {
            log.error("Error during JWT authentication for {}: {}", requestURI, exception.getMessage(), exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
package com.meetus.MeetUSInterview.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.meetus.MeetUSInterview.entity.User;

@Component
@Slf4j
public class JwtUtil {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUserId(String token) {
        try {
            String userId = extractClaim(token, Claims::getSubject);
            log.debug("Extracted user ID from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        User user = (User) userDetails;
        log.debug("Generating token for user ID: {}", user.getId());
        
        Map<String, Object> claims = new HashMap<>();
        
        String token = buildToken(claims, String.valueOf(user.getId()), jwtExpiration);
        log.debug("Token generated successfully for user ID: {}", user.getId());
        return token;
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        User user = (User) userDetails;
        return buildToken(extraClaims, String.valueOf(user.getId()), jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String subject,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            User user = (User) userDetails;
            final String userId = extractUserId(token);
            boolean userIdMatches = userId.equals(String.valueOf(user.getId()));
            boolean tokenNotExpired = !isTokenExpired(token);
            
            log.debug("Token validation - User ID matches: {}, Token expired: {}", userIdMatches, !tokenNotExpired);
            
            if (!userIdMatches) {
                log.warn("Token user ID '{}' does not match user ID '{}'", userId, user.getId());
            }
            if (!tokenNotExpired) {
                log.warn("Token has expired for user ID: {}", user.getId());
            }
            
            return userIdMatches && tokenNotExpired;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

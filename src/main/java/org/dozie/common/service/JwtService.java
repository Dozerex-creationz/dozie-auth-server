package org.dozie.common.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JwtService {

    private static final long ACCESS_TOKEN_EXPIRY = 3600; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRY = 604800; // 7 days
    
    // Use a simple secret key for HS256
    private static final String SECRET_KEY = "mysupersecretkey123456789012345678901234";
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateAccessToken(String userId, List<String> roles, List<String> scopes) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(ACCESS_TOKEN_EXPIRY));

        return Jwts.builder()
            .subject(userId)
            .issuer("dozie-auth-server")
            .issuedAt(issuedAt)
            .expiration(expiresAt)
            .claim("token_type", "access")
            .claim("roles", roles)
            .claim("scope", String.join(" ", scopes))
            .signWith(key)
            .compact();
    }

    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(REFRESH_TOKEN_EXPIRY));

        return Jwts.builder()
            .subject(userId)
            .issuer("dozie-auth-server")
            .issuedAt(issuedAt)
            .expiration(expiresAt)
            .claim("token_type", "refresh")
            .signWith(key)
            .compact();
    }

    /**
     * Validates a refresh token and returns the user ID if valid
     * @param refreshToken The refresh token to validate
     * @return The user ID if token is valid, null otherwise
     */
    public String validateRefreshToken(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return null;
            }
            
            // Parse and verify the JWT token
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
            
            // Check if it's a refresh token
            String tokenType = claims.get("token_type", String.class);
            if (!"refresh".equals(tokenType)) {
                return null;
            }
            
            return claims.getSubject();
            
        } catch (Exception e) {
            // Token is invalid (malformed, wrong signature, expired, etc.)
            return null;
        }
    }

    /**
     * Refreshes an access token using a valid refresh token
     * @param refreshToken The refresh token
     * @param roles User roles for the new access token
     * @param scopes User scopes for the new access token
     * @return New access token if refresh token is valid, null otherwise
     */
    public String refreshAccessToken(String refreshToken, List<String> roles, List<String> scopes) {
        String userId = validateRefreshToken(refreshToken);
        if (userId == null) {
            return null; // Invalid or expired refresh token
        }
        
        return generateAccessToken(userId, roles, scopes);
    }

    // Legacy method for backward compatibility
    public String generateToken(String userId, List<String> roles, List<String> scopes) {
        return generateAccessToken(userId, roles, scopes);
    }
}

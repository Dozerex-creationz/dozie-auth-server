package org.dozie.auth.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Refresh token response containing new access token")
public class RefreshTokenResponse {
    
    @Schema(description = "New JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Token expiration time", example = "2024-01-01T12:00:00")
    private LocalDateTime expiresAt;
    
    @Schema(description = "Token generation timestamp", example = "2024-01-01T10:00:00")
    private LocalDateTime issuedAt;

    public RefreshTokenResponse() {}

    public RefreshTokenResponse(String accessToken, LocalDateTime expiresAt, LocalDateTime issuedAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
        this.issuedAt = issuedAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
} 
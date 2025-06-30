package org.dozie.auth.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Refresh token request to obtain new access token")
public class RefreshTokenRequest {
    
    @Schema(description = "Refresh token to exchange for new access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    private String refreshToken;

    public RefreshTokenRequest() {}

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 
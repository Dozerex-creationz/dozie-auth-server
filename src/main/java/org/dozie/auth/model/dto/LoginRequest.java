package org.dozie.auth.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Login request containing user credentials")
public class LoginRequest {
    
    @Schema(description = "Username for authentication", example = "john.doe", required = true)
    private String username;
    
    @Schema(description = "Password for authentication", example = "password123", required = true)
    private String password;

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}

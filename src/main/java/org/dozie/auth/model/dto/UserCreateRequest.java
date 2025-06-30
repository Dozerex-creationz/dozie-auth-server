package org.dozie.auth.model.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request DTO for creating a new user")
public class UserCreateRequest {
    
    @Schema(description = "Unique username for the user", example = "john.doe", required = true)
    public String username;
    
    @Schema(description = "Plain text password that will be hashed", example = "mySecurePassword123", required = true)
    public String password;
    
    @Schema(description = "Unique email address for the user", example = "john.doe@example.com", required = true)
    public String email;
    
    @Schema(description = "JSON string containing additional user data", example = "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"phone\": \"+1234567890\"}")
    public String userData;
    
    public UserCreateRequest() {}
    
    public UserCreateRequest(String username, String password, String email, String userData) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userData = userData;
    }
} 
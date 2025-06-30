package org.dozie.common.model.error;

/**
 * Centralized error codes for the entire application
 * Organized by domain and functionality
 */
public enum ErrorCode {
    // Authentication Errors (1000-1099)
    USER_NOT_FOUND("User not found", "1001"),
    INVALID_PASSWORD("Invalid password", "1002"),
    INVALID_CREDENTIALS("Invalid credentials", "1003"),
    ACCOUNT_LOCKED("Account is locked", "1004"),
    ACCOUNT_DISABLED("Account is disabled", "1005"),
    
    // Token Errors (1100-1199)
    INVALID_REFRESH_TOKEN("Invalid refresh token", "1101"),
    EXPIRED_REFRESH_TOKEN("Refresh token has expired", "1102"),
    TOKEN_NOT_FOUND("Token not found", "1103"),
    INVALID_TOKEN_TYPE("Invalid token type", "1104"),
    TOKEN_EXPIRED("Token has expired", "1105"),
    INVALID_TOKEN_FORMAT("Invalid token format", "1106"),
    
    // OAuth Errors (1200-1299)
    OAUTH_INVALID_REQUEST("Invalid request", "1201"),
    OAUTH_UNAUTHORIZED_CLIENT("Unauthorized client", "1202"),
    OAUTH_ACCESS_DENIED("Access denied", "1203"),
    OAUTH_UNSUPPORTED_RESPONSE_TYPE("Unsupported response type", "1204"),
    OAUTH_INVALID_SCOPE("Invalid scope", "1205"),
    OAUTH_SERVER_ERROR("Server error", "1206"),
    OAUTH_TEMPORARILY_UNAVAILABLE("Temporarily unavailable", "1207"),
    
    // OAuth Token Endpoint Errors (1250-1299)
    OAUTH_INVALID_GRANT("Invalid grant", "1251"),
    OAUTH_INVALID_CLIENT("Invalid client", "1252"),
    OAUTH_INVALID_AUTHORIZATION_CODE("Invalid authorization code", "1253"),
    OAUTH_AUTHORIZATION_CODE_EXPIRED("Authorization code has expired", "1254"),
    OAUTH_AUTHORIZATION_CODE_USED("Authorization code was already used", "1255"),
    OAUTH_REDIRECT_URI_MISMATCH("Redirect URI mismatch", "1256"),
    OAUTH_MISSING_CODE("Authorization code is required", "1257"),
    OAUTH_MISSING_CLIENT_ID("Client ID is required", "1258"),
    OAUTH_MISSING_CLIENT_SECRET("Client secret is required", "1259"),
    OAUTH_MISSING_REDIRECT_URI("Redirect URI is required", "1260"),
    OAUTH_MISSING_RESPONSE_TYPE("Response type is required", "1261"),
    OAUTH_UNSUPPORTED_GRANT_TYPE("Only 'authorization_code' grant type is supported", "1262"),
    
    // User Management Errors (2000-2099)
    USER_CREATION_FAILED("Failed to create user", "2001"),
    USER_UPDATE_FAILED("Failed to update user", "2002"),
    USER_DELETION_FAILED("Failed to delete user", "2003"),
    USER_ALREADY_EXISTS("User already exists", "2004"),
    USER_EMAIL_EXISTS("Email address already exists", "2005"),
    USER_USERNAME_EXISTS("Username already exists", "2006"),
    USER_INVALID_DATA("Invalid user data", "2007"),
    USER_MISSING_REQUIRED_FIELDS("Username, email, and password are required", "2008"),
    
    // Role Management Errors (3000-3099)
    ROLE_NOT_FOUND("Role not found", "3001"),
    ROLE_CREATION_FAILED("Failed to create role", "3002"),
    ROLE_UPDATE_FAILED("Failed to update role", "3003"),
    ROLE_DELETION_FAILED("Failed to delete role", "3004"),
    ROLE_ALREADY_EXISTS("Role already exists", "3005"),
    ROLE_INVALID_DATA("Invalid role data", "3006"),
    ROLE_MISSING_REQUIRED_FIELDS("Role name is required", "3007"),
    
    // Validation Errors (4000-4099)
    VALIDATION_FAILED("Validation failed", "4001"),
    INVALID_EMAIL_FORMAT("Invalid email format", "4002"),
    INVALID_PASSWORD_FORMAT("Password does not meet requirements", "4003"),
    INVALID_USERNAME_FORMAT("Username does not meet requirements", "4004"),
    REQUIRED_FIELD_MISSING("Required field is missing", "4005"),
    INVALID_ID_FORMAT("Invalid ID format", "4006"),
    
    // Database Errors (5000-5099)
    DATABASE_ERROR("Database error occurred", "5001"),
    DATABASE_CONNECTION_ERROR("Database connection error", "5002"),
    DATABASE_CONSTRAINT_VIOLATION("Database constraint violation", "5003"),
    
    // General Errors (9000-9999)
    INTERNAL_SERVER_ERROR("Internal server error", "9001"),
    SERVICE_UNAVAILABLE("Service temporarily unavailable", "9002"),
    METHOD_NOT_ALLOWED("Method not allowed", "9003"),
    RESOURCE_NOT_FOUND("Resource not found", "9004"),
    BAD_REQUEST("Bad request", "9005"),
    UNAUTHORIZED("Unauthorized", "9006"),
    FORBIDDEN("Forbidden", "9007"),
    CONFLICT("Resource conflict", "9008");

    private final String message;
    private final String code;

    ErrorCode(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", code, message);
    }
} 
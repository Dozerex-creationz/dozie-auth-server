package org.dozie.auth.model.message;

public enum ErrorMessage {
    USER_NOT_FOUND("User not found", "2001"),
    INVALID_PASSWORD("Invalid password", "2002"),
    INVALID_REFRESH_TOKEN("Invalid refresh token", "2003"),
    EXPIRED_REFRESH_TOKEN("Refresh token has expired", "2004"),
    TOKEN_NOT_FOUND("Token not found", "2005"),
    INVALID_TOKEN_TYPE("Invalid token type", "2006");

    private final String message;
    private final String code;

    ErrorMessage(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
} 
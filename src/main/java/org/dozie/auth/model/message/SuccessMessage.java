package org.dozie.auth.model.message;

public enum SuccessMessage {
    LOGIN_SUCCESS("Login successful", "1000");

    private final String message;
    private final String code;

    SuccessMessage(String message, String code) {
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
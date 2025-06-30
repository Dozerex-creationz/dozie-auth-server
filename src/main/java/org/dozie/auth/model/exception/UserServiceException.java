package org.dozie.auth.model.exception;

public class UserServiceException extends RuntimeException {
    
    public enum ErrorType {
        INVALID_EMAIL("Invalid email format"),
        DUPLICATE_EMAIL("Email already exists"),
        DUPLICATE_USERNAME("Username already exists"),
        USER_NOT_FOUND("User not found"),
        INVALID_INPUT("Invalid input data");
        
        private final String message;
        
        ErrorType(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private final ErrorType errorType;
    
    public UserServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
    
    public UserServiceException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
} 
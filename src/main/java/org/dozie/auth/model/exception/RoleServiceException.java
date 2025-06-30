package org.dozie.auth.model.exception;

public class RoleServiceException extends RuntimeException {
    
    public enum ErrorType {
        INVALID_NAME("Invalid role name"),
        DUPLICATE_NAME("Role name already exists"),
        ROLE_NOT_FOUND("Role not found"),
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
    
    public RoleServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
    
    public RoleServiceException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
} 
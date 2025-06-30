package org.dozie.common.model.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response for all API endpoints
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("error_description")
    private String errorDescription;
    
    @JsonProperty("error_code")
    private String errorCode;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("details")
    private Map<String, Object> details;

    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(ErrorCode errorCode) {
        this();
        this.error = "error";
        this.errorDescription = errorCode.getMessage();
        this.errorCode = errorCode.getCode();
    }

    public ErrorResponse(ErrorCode errorCode, String customDescription) {
        this();
        this.error = "error";
        this.errorDescription = customDescription != null ? customDescription : errorCode.getMessage();
        this.errorCode = errorCode.getCode();
    }

    public ErrorResponse(String error, String errorDescription, String errorCode) {
        this();
        this.error = error;
        this.errorDescription = errorDescription;
        this.errorCode = errorCode;
    }

    public ErrorResponse(String error, String errorDescription, String errorCode, String path) {
        this(error, errorDescription, errorCode);
        this.path = path;
    }

    // OAuth specific constructor
    public ErrorResponse(String error, String errorDescription) {
        this();
        this.error = error;
        this.errorDescription = errorDescription;
    }

    // Builder pattern for complex error responses
    public static class Builder {
        private ErrorResponse response;

        public Builder() {
            response = new ErrorResponse();
        }

        public Builder errorCode(ErrorCode errorCode) {
            response.error = "error";
            response.errorDescription = errorCode.getMessage();
            response.errorCode = errorCode.getCode();
            return this;
        }

        public Builder error(String error) {
            response.error = error;
            return this;
        }

        public Builder errorDescription(String errorDescription) {
            response.errorDescription = errorDescription;
            return this;
        }

        public Builder customErrorCode(String errorCode) {
            response.errorCode = errorCode;
            return this;
        }

        public Builder path(String path) {
            response.path = path;
            return this;
        }

        public Builder details(Map<String, Object> details) {
            response.details = details;
            return this;
        }

        public ErrorResponse build() {
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return String.format("ErrorResponse{error='%s', errorDescription='%s', errorCode='%s', timestamp=%s, path='%s'}", 
            error, errorDescription, errorCode, timestamp, path);
    }
} 
package org.dozie.common.model.error;

import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * Factory class for creating standardized error responses
 */
public class ErrorResponseFactory {

    /**
     * Create a simple error response with ErrorCode
     */
    public static ErrorResponse create(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    /**
     * Create an error response with custom description
     */
    public static ErrorResponse create(ErrorCode errorCode, String customDescription) {
        return new ErrorResponse(errorCode, customDescription);
    }

    /**
     * Create an OAuth-compliant error response
     */
    public static ErrorResponse createOAuthError(String error, String errorDescription) {
        return new ErrorResponse(error, errorDescription);
    }

    /**
     * Create a JAX-RS Response with ErrorCode
     */
    public static Response createResponse(Response.Status status, ErrorCode errorCode) {
        return Response.status(status)
                .entity(create(errorCode))
                .build();
    }

    /**
     * Create a JAX-RS Response with custom description
     */
    public static Response createResponse(Response.Status status, ErrorCode errorCode, String customDescription) {
        return Response.status(status)
                .entity(create(errorCode, customDescription))
                .build();
    }

    /**
     * Create a JAX-RS Response with OAuth error
     */
    public static Response createOAuthResponse(Response.Status status, String error, String errorDescription) {
        return Response.status(status)
                .entity(createOAuthError(error, errorDescription))
                .build();
    }

    /**
     * Create a bad request response
     */
    public static Response badRequest(ErrorCode errorCode) {
        return createResponse(Response.Status.BAD_REQUEST, errorCode);
    }

    /**
     * Create a bad request response with custom description
     */
    public static Response badRequest(ErrorCode errorCode, String customDescription) {
        return createResponse(Response.Status.BAD_REQUEST, errorCode, customDescription);
    }

    /**
     * Create an unauthorized response
     */
    public static Response unauthorized(ErrorCode errorCode) {
        return createResponse(Response.Status.UNAUTHORIZED, errorCode);
    }

    /**
     * Create a forbidden response
     */
    public static Response forbidden(ErrorCode errorCode) {
        return createResponse(Response.Status.FORBIDDEN, errorCode);
    }

    /**
     * Create a not found response
     */
    public static Response notFound(ErrorCode errorCode) {
        return createResponse(Response.Status.NOT_FOUND, errorCode);
    }

    /**
     * Create a conflict response
     */
    public static Response conflict(ErrorCode errorCode) {
        return createResponse(Response.Status.CONFLICT, errorCode);
    }

    /**
     * Create an internal server error response
     */
    public static Response internalServerError(ErrorCode errorCode) {
        return createResponse(Response.Status.INTERNAL_SERVER_ERROR, errorCode);
    }

    /**
     * Create a service unavailable response
     */
    public static Response serviceUnavailable(ErrorCode errorCode) {
        return createResponse(Response.Status.SERVICE_UNAVAILABLE, errorCode);
    }

    /**
     * Create an OAuth-compliant error response with specific OAuth error codes
     */
    public static Response createOAuthErrorResponse(Response.Status status, String oauthError, String errorDescription) {
        ErrorResponse errorResponse = new ErrorResponse(oauthError, errorDescription);
        return Response.status(status)
                .entity(errorResponse)
                .build();
    }

    /**
     * Create a Map for OAuth redirect URLs (for backward compatibility)
     */
    public static Map<String, String> createOAuthErrorMap(String error, String errorDescription) {
        return Map.of("error", error, "error_description", errorDescription);
    }

    /**
     * Create a builder for complex error responses
     */
    public static ErrorResponse.Builder builder() {
        return ErrorResponse.builder();
    }
} 
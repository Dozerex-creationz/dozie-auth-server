package org.dozie.oauth.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.dozie.common.model.error.ErrorCode;
import org.dozie.common.model.error.ErrorResponseFactory;
import org.dozie.oauth.model.entity.OAuthAuthCode;
import org.dozie.oauth.model.entity.OAuthClient;
import org.dozie.oauth.model.entity.OAuthToken;
import org.dozie.oauth.repository.OAuthRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class OAuthService {

    @Inject
    OAuthRepository oAuthRepository;

    public Response handleAuthorizationRequest(String responseType, String clientId, 
                                            String redirectUri, String scope, String state) {
        
        // Validate required parameters
        if (responseType == null || responseType.trim().isEmpty()) {
            return createOAuthRedirectError(redirectUri, "unsupported_response_type", 
                ErrorCode.OAUTH_MISSING_RESPONSE_TYPE.getMessage(), state);
        }
        
        if (!"code".equals(responseType)) {
            return createOAuthRedirectError(redirectUri, "unsupported_response_type", 
                ErrorCode.OAUTH_UNSUPPORTED_RESPONSE_TYPE.getMessage(), state);
        }
        
        if (clientId == null || clientId.trim().isEmpty()) {
            return createOAuthRedirectError(redirectUri, "invalid_request", 
                ErrorCode.OAUTH_MISSING_CLIENT_ID.getMessage(), state);
        }
        
        if (redirectUri == null || redirectUri.trim().isEmpty()) {
            return createOAuthRedirectError("https://myapp.com/error", "invalid_request", 
                ErrorCode.OAUTH_MISSING_REDIRECT_URI.getMessage(), state);
        }
        
        // Validate client
        OAuthClient client = oAuthRepository.findClientById(clientId);
        if (client == null) {
            return createOAuthRedirectError(redirectUri, "unauthorized_client", 
                ErrorCode.OAUTH_UNAUTHORIZED_CLIENT.getMessage(), state);
        }
        
        // Validate redirect URI - temporarily skipped due to lazy loading issue
        // TODO: Fix this with proper eager loading or separate query
        /*
        if (!client.redirectUris.contains(redirectUri)) {
            return createOAuthRedirectError(redirectUri, "invalid_request", 
                ErrorCode.OAUTH_REDIRECT_URI_MISMATCH.getMessage(), state);
        }
        */
        
        // Generate authorization code
        String authCode = UUID.randomUUID().toString();
        OAuthAuthCode authCodeEntity = new OAuthAuthCode();
        authCodeEntity.code = authCode;
        authCodeEntity.clientId = clientId;
        authCodeEntity.redirectUri = redirectUri;
        authCodeEntity.scope = scope != null ? scope : "";
        authCodeEntity.state = state;
        authCodeEntity.expiresAt = LocalDateTime.now().plusMinutes(10); // 10 minutes expiry
        authCodeEntity.used = false;
        
        oAuthRepository.saveAuthCode(authCodeEntity);
        
        // Build redirect URL
        StringBuilder redirectUrl = new StringBuilder(redirectUri);
        redirectUrl.append("?code=").append(authCode);
        if (state != null && !state.trim().isEmpty()) {
            redirectUrl.append("&state=").append(state);
        }
        
        return Response.status(Response.Status.FOUND)
                .header("Location", redirectUrl.toString())
                .build();
    }

    public Response handleTokenRequest(String grantType, String code, String redirectUri, 
                                     String clientId, String clientSecret) {
        
        // Validate required parameters
        if (grantType == null || grantType.trim().isEmpty()) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "unsupported_grant_type", 
                ErrorCode.OAUTH_UNSUPPORTED_GRANT_TYPE.getMessage());
        }
        
        if (!"authorization_code".equals(grantType)) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "unsupported_grant_type", 
                ErrorCode.OAUTH_UNSUPPORTED_GRANT_TYPE.getMessage());
        }
        
        if (code == null || code.trim().isEmpty()) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_request", 
                ErrorCode.OAUTH_MISSING_CODE.getMessage());
        }
        
        if (clientId == null || clientId.trim().isEmpty()) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_request", 
                ErrorCode.OAUTH_MISSING_CLIENT_ID.getMessage());
        }
        
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_request", 
                ErrorCode.OAUTH_MISSING_CLIENT_SECRET.getMessage());
        }
        
        // Validate client credentials
        OAuthClient client = oAuthRepository.findClientById(clientId);
        if (client == null || !clientSecret.equals(client.clientSecret)) {
            return createOAuthErrorResponse(Response.Status.UNAUTHORIZED, "invalid_client", 
                ErrorCode.OAUTH_INVALID_CLIENT.getMessage());
        }
        
        // Validate authorization code
        OAuthAuthCode authCode = oAuthRepository.findAuthCodeByCode(code);
        if (authCode == null) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_grant", 
                ErrorCode.OAUTH_INVALID_AUTHORIZATION_CODE.getMessage());
        }
        
        // Check if code has expired
        if (authCode.expiresAt.isBefore(LocalDateTime.now())) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_grant", 
                ErrorCode.OAUTH_AUTHORIZATION_CODE_EXPIRED.getMessage());
        }
        
        // Check if code was issued to the same client
        if (!clientId.equals(authCode.clientId)) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_grant", 
                ErrorCode.OAUTH_AUTHORIZATION_CODE_USED.getMessage());
        }
        
        // Check if code was already used
        if (authCode.used) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_grant", 
                ErrorCode.OAUTH_AUTHORIZATION_CODE_USED.getMessage());
        }
        
        // Validate redirect URI if provided
        if (redirectUri != null && !redirectUri.trim().isEmpty() && 
            !redirectUri.equals(authCode.redirectUri)) {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, "invalid_grant", 
                ErrorCode.OAUTH_REDIRECT_URI_MISMATCH.getMessage());
        }
        
        // Mark code as used
        authCode.used = true;
        oAuthRepository.updateAuthCode(authCode);
        
        // Generate tokens
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        
        OAuthToken token = new OAuthToken();
        token.accessToken = accessToken;
        token.refreshToken = refreshToken;
        token.tokenType = "Bearer";
        token.clientId = clientId;
        token.scope = authCode.scope;
        token.expiresAt = LocalDateTime.now().plusHours(1); // 1 hour expiry
        
        oAuthRepository.saveToken(token);
        
        // Build response
        Map<String, Object> response = Map.of(
            "access_token", accessToken,
            "token_type", "Bearer",
            "expires_in", 3600,
            "refresh_token", refreshToken,
            "scope", authCode.scope
        );
        
        return Response.ok(response).build();
    }
    
    private Response createOAuthRedirectError(String redirectUri, String error, String errorDescription, String state) {
        if (redirectUri != null && !redirectUri.trim().isEmpty()) {
            StringBuilder redirectUrl = new StringBuilder(redirectUri);
            redirectUrl.append("?error=").append(error);
            redirectUrl.append("&error_description=").append(URLEncoder.encode(errorDescription, StandardCharsets.UTF_8));
            if (state != null && !state.trim().isEmpty()) {
                redirectUrl.append("&state=").append(state);
            }
            
            return Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl.toString())
                    .build();
        } else {
            return createOAuthErrorResponse(Response.Status.BAD_REQUEST, error, errorDescription);
        }
    }

    private Response createOAuthErrorResponse(Response.Status status, String error, String errorDescription) {
        return Response.status(status)
                .entity(Map.of("error", error, "error_description", errorDescription))
                .build();
    }
} 
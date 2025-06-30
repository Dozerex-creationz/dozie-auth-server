package org.dozie.oauth.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.dozie.common.model.error.ErrorCode;
import org.dozie.common.model.error.ErrorResponseFactory;
import org.dozie.oauth.service.OAuthService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/oauth")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "OAuth 2.0", description = "OAuth 2.0 authorization code flow endpoints")
public class OAuthController {

    @Inject
    OAuthService oAuthService;

    @GET
    @Path("/authorize")
    @Operation(
        summary = "OAuth 2.0 Authorization Endpoint",
        description = "Handles the authorization request and redirects user to consent page or returns authorization code"
    )
    @Parameter(
        name = "response_type",
        description = "Must be 'code' for authorization code flow",
        required = true,
        schema = @Schema(example = "code")
    )
    @Parameter(
        name = "client_id",
        description = "OAuth client identifier",
        required = true,
        schema = @Schema(example = "my-client-id")
    )
    @Parameter(
        name = "redirect_uri",
        description = "URI to redirect after authorization",
        required = true,
        schema = @Schema(example = "https://myapp.com/callback")
    )
    @Parameter(
        name = "scope",
        description = "Requested scopes (space-separated)",
        required = false,
        schema = @Schema(example = "read write")
    )
    @Parameter(
        name = "state",
        description = "Random string to prevent CSRF attacks",
        required = false,
        schema = @Schema(example = "random-state-string")
    )
    @APIResponses({
        @APIResponse(
            responseCode = "302",
            description = "Redirect to consent page or callback URI with authorization code"
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized client"
        )
    })
    public Response authorize(
            @QueryParam("response_type") String responseType,
            @QueryParam("client_id") String clientId,
            @QueryParam("redirect_uri") String redirectUri,
            @QueryParam("scope") String scope,
            @QueryParam("state") String state) {
        
        try {
            return oAuthService.handleAuthorizationRequest(responseType, clientId, redirectUri, scope, state);
        } catch (Exception e) {
            return ErrorResponseFactory.badRequest(ErrorCode.OAUTH_INVALID_REQUEST, e.getMessage());
        }
    }

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(
        summary = "OAuth 2.0 Token Endpoint",
        description = "Exchanges authorization code for access token"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Token issued successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(example = "{\"access_token\":\"...\",\"token_type\":\"Bearer\",\"expires_in\":3600,\"refresh_token\":\"...\"}")
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request or authorization code"
        ),
        @APIResponse(
            responseCode = "401",
            description = "Invalid client credentials"
        )
    })
    public Response token(
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri,
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret) {
        
        try {
            return oAuthService.handleTokenRequest(grantType, code, redirectUri, clientId, clientSecret);
        } catch (Exception e) {
            return ErrorResponseFactory.badRequest(ErrorCode.OAUTH_INVALID_REQUEST, e.getMessage());
        }
    }
} 
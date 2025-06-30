package org.dozie.common.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.dozie.auth.model.dto.RefreshTokenRequest;
import org.dozie.auth.model.dto.RefreshTokenResponse;
import org.dozie.auth.repository.user.UserRepository;
import org.dozie.auth.repository.userrole.UserRoleRepositoryAdapter;
import org.dozie.auth.model.entity.User;
import org.dozie.auth.model.entity.UserRole;
import org.dozie.common.model.response.ApiResponse;
import org.dozie.common.service.JwtService;
import org.dozie.common.model.error.ErrorCode;
import org.dozie.common.model.error.ErrorResponseFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Token Management", description = "Token management endpoints for JWT operations")
public class TokenController {

    @Inject
    JwtService jwtService;

    @Inject
    UserRepository userRepository;

    @Inject
    UserRoleRepositoryAdapter userRoleRepository;

    @POST
    @Path("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Generate a new access token using a valid refresh token"
    )
    @RequestBody(
        description = "Refresh token request",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = RefreshTokenRequest.class),
            examples = {
                @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(
                    name = "Refresh Token Request",
                    value = "{\"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                )
            }
        )
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Access token refreshed successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(
                        name = "Successful Refresh",
                        value = "{\"data\": {\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"expiresAt\": \"2024-01-01T12:00:00\", \"issuedAt\": \"2024-01-01T10:00:00\"}, \"message\": \"Access token refreshed successfully - 200\", \"code\": 123}"
                    )
                }
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid refresh token",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "Expired refresh token",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response refreshToken(RefreshTokenRequest request) {
        // Validate request
        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            return ErrorResponseFactory.badRequest(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Validate refresh token and get user ID
        String userId = jwtService.validateRefreshToken(request.getRefreshToken());
        if (userId == null) {
            return ErrorResponseFactory.unauthorized(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // Find user
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return ErrorResponseFactory.notFound(ErrorCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();

        // Get user roles
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.id);
        List<String> roleNames = userRoles.stream()
            .map(userRole -> userRole.role.name)
            .collect(Collectors.toList());
        List<String> scopes = List.of("read", "write");

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(user.id.toString(), roleNames, scopes);
        if (newAccessToken == null) {
            return ErrorResponseFactory.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1 hour from now
        LocalDateTime issuedAt = LocalDateTime.now();

        // Create response
        RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, expiresAt, issuedAt);
        
        return Response.ok(new ApiResponse(response, "Access token refreshed successfully - 200")).build();
    }
} 
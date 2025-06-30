package org.dozie.auth.controller;

import org.dozie.auth.model.dto.LoginRequest;
import org.dozie.auth.service.LoginService;
import org.dozie.common.model.response.ApiResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/login")
@Tag(name = "Authentication", description = "Authentication endpoints for user login")
public class LoginController {

    @Inject
    LoginService loginService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
        summary = "Health check endpoint",
        description = "Simple health check endpoint to verify the login service is running"
    )
    @APIResponse(
        responseCode = "200",
        description = "Service is running",
        content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))
    )
    public String hello() {
        return "Hello from Quarkus Login Controller";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "User login",
        description = "Authenticate a user with username and password"
    )
    @RequestBody(
        description = "Login credentials",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = LoginRequest.class),
            examples = {
                @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(
                    name = "Valid Login",
                    value = "{\"username\": \"john.doe\", \"password\": \"password123\"}"
                )
            }
        )
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(
                        name = "Successful Login",
                        value = "{\"data\": {\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"userId\": 1, \"username\": \"john.doe\", \"email\": \"john.doe@example.com\", \"roleName\": \"ADMIN\", \"expiresAt\": \"2024-01-01T12:00:00\", \"loginTime\": \"2024-01-01T10:00:00\"}, \"message\": \"Login successful - 200\", \"code\": 123}"
                    )
                }
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid credentials",
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
    public Response login(LoginRequest loginRequest) {
        ApiResponse response = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return Response.ok(response).build();
    }

}

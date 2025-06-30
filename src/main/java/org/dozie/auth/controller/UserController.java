package org.dozie.auth.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.dozie.auth.model.entity.User;
import org.dozie.auth.model.dto.UserCreateRequest;
import org.dozie.auth.model.exception.UserServiceException;
import org.dozie.auth.service.UserService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Management", description = "User management endpoints for CRUD operations")
public class UserController {

    @Inject
    UserService userService;

    @GET
    @Operation(
        summary = "Get all users",
        description = "Retrieve a list of all users in the system"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "List of users retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user by their ID"
    )
    @Parameter(
        name = "id",
        description = "User ID",
        required = true,
        schema = @Schema(example = "1")
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response getUserById(@PathParam("id") Long id) {
        try {
            User user = userService.getUserById(id);
            return Response.ok(user).build();
        } catch (UserServiceException e) {
            if (e.getErrorType() == UserServiceException.ErrorType.USER_NOT_FOUND) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
            }
            throw e; // Re-throw other exceptions
        }
    }

    @POST
    @Operation(
        summary = "Create a new user",
        description = "Create a new user with the provided information. Password will be automatically hashed."
    )
    @RequestBody(
        description = "User information",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = UserCreateRequest.class),
            examples = {
                @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(
                    name = "New User",
                    value = "{\"username\": \"john.doe\", \"email\": \"john.doe@example.com\", \"password\": \"mySecurePassword123\", \"userData\": \"{\\\"firstName\\\": \\\"John\\\", \\\"lastName\\\": \\\"Doe\\\"}\"}"
                )
            }
        )
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid input data, duplicate email, or duplicate username"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response createUser(UserCreateRequest request) {
        // Validate input
        if (request == null || request.username == null || request.username.trim().isEmpty() ||
            request.email == null || request.email.trim().isEmpty() ||
            request.password == null || request.password.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Username, email, and password are required")
                .build();
        }
        
        try {
            // Convert DTO to entity
            User user = new User();
            user.username = request.username.trim();
            user.passwordHash = request.password; // UserService will hash this
            user.email = request.email.trim();
            user.userData = request.userData;
            
            User created = userService.createUser(user);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (UserServiceException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Update user",
        description = "Update an existing user's information"
    )
    @Parameter(
        name = "id",
        description = "User ID",
        required = true,
        schema = @Schema(example = "1")
    )
    @RequestBody(
        description = "Updated user information",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = User.class)
        )
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid input data, duplicate email, or duplicate username"
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response updateUser(@PathParam("id") Long id, User user) {
        // Validate input
        if (user == null || user.username == null || user.username.trim().isEmpty() ||
            user.email == null || user.email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Username and email are required")
                .build();
        }
        
        try {
            User updated = userService.updateUser(id, user);
            return Response.ok(updated).build();
        } catch (UserServiceException e) {
            if (e.getErrorType() == UserServiceException.ErrorType.USER_NOT_FOUND) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
            }
        }
    }

    @DELETE
    @Path("/{email}")
    @Operation(
        summary = "Delete user by email",
        description = "Delete a user from the system using their email address"
    )
    @Parameter(
        name = "email",
        description = "User email address",
        required = true,
        schema = @Schema(example = "john.doe@example.com")
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @APIResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response deleteUser(@PathParam("email") String email) {
        boolean deleted = userService.deleteUser(null, email);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
} 
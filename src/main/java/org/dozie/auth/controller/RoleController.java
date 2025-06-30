package org.dozie.auth.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.dozie.auth.model.entity.Role;
import org.dozie.auth.model.exception.RoleServiceException;
import org.dozie.auth.service.RoleService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Role Management", description = "Role management endpoints for CRUD operations")
public class RoleController {

    @Inject
    RoleService roleService;

    @GET
    @Operation(
        summary = "Get all roles",
        description = "Retrieve a list of all roles in the system"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "List of roles retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Role.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return Response.ok(roles).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "Get role by ID",
        description = "Retrieve a specific role by its ID"
    )
    @Parameter(
        name = "id",
        description = "Role ID",
        required = true,
        schema = @Schema(example = "1")
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Role found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Role.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "Role not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response getRoleById(@PathParam("id") Long id) {
        try {
            Role role = roleService.getRoleById(id);
            return Response.ok(role).build();
        } catch (RoleServiceException e) {
            if (e.getErrorType() == RoleServiceException.ErrorType.ROLE_NOT_FOUND) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
            }
            throw e;
        }
    }

    @POST
    @Operation(
        summary = "Create a new role",
        description = "Create a new role with the provided information"
    )
    @RequestBody(
        description = "Role information",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Role.class),
            examples = {
                @org.eclipse.microprofile.openapi.annotations.media.ExampleObject(
                    name = "New Role",
                    value = "{\"name\": \"ADMIN\", \"rolePermission\": \"{\\\"canRead\\\": true, \\\"canWrite\\\": true, \\\"canDelete\\\": true}\"}"
                )
            }
        )
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "Role created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Role.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid role data or duplicate name"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response createRole(Role role) {
        try {
            Role created = roleService.createRole(role);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (RoleServiceException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "Update role",
        description = "Update an existing role's information"
    )
    @Parameter(
        name = "id",
        description = "Role ID",
        required = true,
        schema = @Schema(example = "1")
    )
    @RequestBody(
        description = "Updated role information",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Role.class)
        )
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Role updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Role.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid role data"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Role not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response updateRole(@PathParam("id") Long id, Role role) {
        try {
            Role updated = roleService.updateRole(id, role);
            return Response.ok(updated).build();
        } catch (RoleServiceException e) {
            if (e.getErrorType() == RoleServiceException.ErrorType.ROLE_NOT_FOUND) {
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
    @Path("/{id}")
    @Operation(
        summary = "Delete role by ID",
        description = "Delete a role from the system using its ID"
    )
    @Parameter(
        name = "id",
        description = "Role ID",
        required = true,
        schema = @Schema(example = "1")
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "Role deleted successfully"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Role not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public Response deleteRole(@PathParam("id") Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
} 
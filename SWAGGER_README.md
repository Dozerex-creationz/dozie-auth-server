# Swagger Documentation for Dozie Auth Server

This document explains how to access and use the Swagger/OpenAPI documentation for the Dozie Auth Server.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Development**: http://localhost:8080/swagger-ui
- **Production**: https://api.dozie.com/swagger-ui

## Available Endpoints

The API documentation is organized into the following categories:

### 1. Authentication

- **GET** `/login` - Health check endpoint
- **POST** `/login` - User authentication with username and password

### 2. User Management

- **GET** `/users` - Get all users
- **GET** `/users/{id}` - Get user by ID
- **POST** `/users` - Create a new user
- **PUT** `/users/{id}` - Update user information
- **DELETE** `/users/{email}` - Delete user by email

### 3. Role Management

- **GET** `/roles` - Get all roles
- **GET** `/roles/{id}` - Get role by ID
- **POST** `/roles` - Create a new role
- **PUT** `/roles/{id}` - Update role information
- **DELETE** `/roles/{id}` - Delete role by ID

## Features

### Interactive Documentation

- **Try it out**: Test API endpoints directly from the Swagger UI
- **Request/Response Examples**: See example requests and responses
- **Schema Documentation**: Detailed information about data models
- **Authentication**: Support for different authentication methods

### Data Models

The following data models are documented:

1. **LoginRequest**

   - `username` (String, required): Username for authentication
   - `password` (String, required): Password for authentication

2. **User**

   - `id` (Long): Unique identifier
   - `username` (String, required): Unique username
   - `email` (String, required): Unique email address
   - `passwordHash` (String, required): Hashed password
   - `userData` (String): JSON string with additional user data

3. **Role**

   - `id` (Long): Unique identifier
   - `name` (String, required): Unique role name
   - `rolePermission` (String): JSON string with role permissions

4. **ApiResponse**
   - `data` (Object): Response data payload
   - `message` (String): Response message
   - `code` (Integer): Response code

## Configuration

The Swagger UI is configured in `application.properties`:

```properties
# Swagger UI Configuration
quarkus.swagger-ui.always-include=true
quarkus.swagger-ui.path=/swagger-ui
quarkus.swagger-ui.enable=true

# OpenAPI Configuration
mp.openapi.extensions.enabled=true
```

## Development

### Adding New Endpoints

When adding new endpoints, make sure to include the following OpenAPI annotations:

```java
@Operation(
    summary = "Brief description",
    description = "Detailed description"
)
@APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Success response"
    ),
    @APIResponse(
        responseCode = "400",
        description = "Bad request"
    )
})
```

### Adding Schema Documentation

For data models, use the `@Schema` annotation:

```java
@Schema(description = "Model description")
public class MyModel {
    @Schema(description = "Field description", example = "example value")
    private String field;
}
```

## Troubleshooting

1. **Swagger UI not accessible**: Ensure the application is running and check the configured path
2. **Missing endpoints**: Verify that all controllers have proper OpenAPI annotations
3. **Schema not showing**: Check that all DTOs and entities have `@Schema` annotations

## Additional Resources

- [OpenAPI Specification](https://swagger.io/specification/)
- [Quarkus OpenAPI Documentation](https://quarkus.io/guides/openapi-swaggerui)
- [MicroProfile OpenAPI](https://microprofile.io/project/eclipse/microprofile-open-api)

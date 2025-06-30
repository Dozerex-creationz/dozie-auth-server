package org.dozie.auth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Dozie Auth Server API",
        version = "1.0.0",
        description = "Authentication and authorization server API for user and role management",
        contact = @Contact(
            name = "Dozie Auth Team",
            email = "support@dozie.com",
            url = "https://dozie.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Development server"
        ),
        @Server(
            url = "https://api.dozie.com",
            description = "Production server"
        )
    }
)
@RegisterForReflection
public class OpenApiConfig {
    // Configuration class for OpenAPI documentation
} 
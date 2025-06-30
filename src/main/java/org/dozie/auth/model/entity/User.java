package org.dozie.auth.model.entity;

import org.dozie.common.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name="tb_users")
@Schema(description = "User entity representing a system user")
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    @Schema(description = "Unique username for the user", example = "john.doe", required = true)
    public String username;

    @Column(nullable = false)
    @Schema(description = "Hashed password for security", example = "$2a$10$hashedPasswordString", required = true)
    public String passwordHash;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "JSON string containing additional user data", example = "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"phone\": \"+1234567890\"}")
    public String userData; // JSON string containing all user data

    @Column(unique = true, nullable = false)
    @Schema(description = "Unique email address for the user", example = "john.doe@example.com", required = true)
    public String email;
}
package org.dozie.auth.model.entity;

import org.dozie.common.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name="tb_roles")
@Schema(description = "Role entity representing a system role with permissions")
public class Role extends BaseEntity {
    @Column(nullable = false, unique = true)
    @Schema(description = "Unique role name", example = "ADMIN", required = true)
    public String name;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "JSON string containing role permissions", example = "{\"canRead\": true, \"canWrite\": true, \"canDelete\": true, \"canManageUsers\": true}")
    public String rolePermission; // JSON string containing role permissions
}
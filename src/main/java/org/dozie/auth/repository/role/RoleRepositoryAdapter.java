package org.dozie.auth.repository.role;

import org.dozie.auth.model.entity.Role;
import java.util.Optional;

public interface RoleRepositoryAdapter {
    Optional<Role> findByName(String name);
    Optional<Role> findById(Long id);
    void save(Role role);
    void deleteById(Long id);
} 
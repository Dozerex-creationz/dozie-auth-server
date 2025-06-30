package org.dozie.auth.repository.role;

import org.dozie.auth.model.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    Optional<Role> findById(Long id);
    void save(Role role);
    void deleteById(Long id);
    List<Role> findAll();
} 
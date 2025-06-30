package org.dozie.auth.repository.role;

import jakarta.enterprise.context.ApplicationScoped;
import org.dozie.auth.model.entity.Role;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleRepositoryImpl implements RoleRepository {
    @Override
    public Optional<Role> findByName(String name) {
        return Role.find("name", name).firstResultOptional();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Role.findByIdOptional(id);
    }

    @Override
    public void save(Role role) {
        role.persist();
    }

    @Override
    public void deleteById(Long id) {
        Role.deleteById(id);
    }

    @Override
    public List<Role> findAll() {
        return Role.listAll();
    }
} 
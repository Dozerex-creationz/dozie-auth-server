package org.dozie.auth.repository.role;

import org.dozie.auth.model.entity.Role;
import java.util.Optional;

public class RoleRepositoryAdapterImpl implements RoleRepositoryAdapter {
    private final RoleRepository roleRepository;

    public RoleRepositoryAdapterImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        // Middleware logic here
        return roleRepository.findByName(name);
    }

    @Override
    public Optional<Role> findById(Long id) {
        // Middleware logic here
        return roleRepository.findById(id);
    }

    @Override
    public void save(Role role) {
        // Middleware logic here
        roleRepository.save(role);
    }

    @Override
    public void deleteById(Long id) {
        // Middleware logic here
        roleRepository.deleteById(id);
    }
} 
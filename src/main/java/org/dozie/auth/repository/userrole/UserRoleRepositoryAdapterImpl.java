package org.dozie.auth.repository.userrole;

import jakarta.enterprise.context.ApplicationScoped;
import org.dozie.auth.model.entity.UserRole;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRoleRepositoryAdapterImpl implements UserRoleRepositoryAdapter {
    private final UserRoleRepository userRoleRepository;

    public UserRoleRepositoryAdapterImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Optional<UserRole> findById(Long id) {
        // Middleware logic here
        return userRoleRepository.findById(id);
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        // Middleware logic here
        return userRoleRepository.findByUserId(userId);
    }

    @Override
    public void save(UserRole userRole) {
        // Middleware logic here
        userRoleRepository.save(userRole);
    }

    @Override
    public void deleteById(Long id) {
        // Middleware logic here
        userRoleRepository.deleteById(id);
    }
} 
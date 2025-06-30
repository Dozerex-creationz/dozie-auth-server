package org.dozie.auth.repository.userrole;

import jakarta.enterprise.context.ApplicationScoped;
import org.dozie.auth.model.entity.UserRole;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRoleRepositoryImpl implements UserRoleRepository {
    @Override
    public Optional<UserRole> findById(Long id) {
        return UserRole.findByIdOptional(id);
    }

    @Override
    public List<UserRole> findByUserId(Long userId) {
        return UserRole.find("FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.id = ?1", userId).list();
    }

    @Override
    public void save(UserRole userRole) {
        userRole.persist();
    }

    @Override
    public void deleteById(Long id) {
        UserRole.deleteById(id);
    }
} 
package org.dozie.auth.repository.userrole;

import org.dozie.auth.model.entity.UserRole;
import java.util.List;
import java.util.Optional;
 
public interface UserRoleRepository {
    Optional<UserRole> findById(Long id);
    List<UserRole> findByUserId(Long userId);
    void save(UserRole userRole);
    void deleteById(Long id);
} 
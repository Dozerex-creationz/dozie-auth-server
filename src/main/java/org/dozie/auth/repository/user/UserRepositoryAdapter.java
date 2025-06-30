package org.dozie.auth.repository.user;

import org.dozie.auth.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryAdapter {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    void save(User user);
    void deleteById(Long id);
    List<User> findAll();
} 
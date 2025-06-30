package org.dozie.auth.repository.user;

import jakarta.enterprise.context.ApplicationScoped;
import org.dozie.auth.model.entity.User;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findByUsername(String username) {
        return User.find("username", username).firstResultOptional();
    }

    @Override
    public Optional<User> findById(Long id) {
        return User.findByIdOptional(id);
    }

    @Override
    public void save(User user) {
        user.persist();
    }

    @Override
    public void deleteById(Long id) {
        User.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        return User.listAll();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return User.find("email", email).firstResultOptional();
    }
} 
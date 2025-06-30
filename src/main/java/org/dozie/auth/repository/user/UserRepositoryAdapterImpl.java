package org.dozie.auth.repository.user;

import jakarta.enterprise.context.ApplicationScoped;
import org.dozie.auth.model.entity.User;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryAdapterImpl implements UserRepositoryAdapter {
    private final UserRepository userRepository;

    public UserRepositoryAdapterImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Middleware logic here
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        // Middleware logic here
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Middleware logic here
        return userRepository.findByEmail(email);
    }

    @Override
    public void save(User user) {
        // Middleware logic here
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        // Middleware logic here
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAll() {
        // Middleware logic here
        return userRepository.findAll();
    }
} 
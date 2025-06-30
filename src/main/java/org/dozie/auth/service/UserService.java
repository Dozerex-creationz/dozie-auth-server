package org.dozie.auth.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.dozie.auth.model.entity.User;
import org.dozie.auth.model.exception.UserServiceException;
import org.dozie.auth.repository.user.UserRepository;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hash) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hash).verified;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new UserServiceException(UserServiceException.ErrorType.USER_NOT_FOUND, 
                "User with ID " + id + " not found");
        }
        return userOpt.get();
    }

    @Transactional
    public User createUser(User user) {
        // Validate email format
        if (!isValidEmail(user.email)) {
            throw new UserServiceException(UserServiceException.ErrorType.INVALID_EMAIL, 
                "Invalid email format: " + user.email);
        }
        
        // Check for duplicate email
        if (userRepository.findByEmail(user.email).isPresent()) {
            throw new UserServiceException(UserServiceException.ErrorType.DUPLICATE_EMAIL, 
                "Email already exists: " + user.email);
        }
        
        // Check for duplicate username
        if (userRepository.findByUsername(user.username).isPresent()) {
            throw new UserServiceException(UserServiceException.ErrorType.DUPLICATE_USERNAME, 
                "Username already exists: " + user.username);
        }
        
        user.passwordHash = hashPassword(user.passwordHash);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUser(Long id, User user) {
        Optional<User> entityOpt = userRepository.findById(id);
        if (entityOpt.isEmpty()) {
            throw new UserServiceException(UserServiceException.ErrorType.USER_NOT_FOUND, 
                "User with ID " + id + " not found");
        }
        
        // Validate email format
        if (!isValidEmail(user.email)) {
            throw new UserServiceException(UserServiceException.ErrorType.INVALID_EMAIL, 
                "Invalid email format: " + user.email);
        }
        
        // Check uniqueness for email (except for the current user)
        Optional<User> existingEmail = userRepository.findByEmail(user.email);
        if (existingEmail.isPresent() && !existingEmail.get().id.equals(id)) {
            throw new UserServiceException(UserServiceException.ErrorType.DUPLICATE_EMAIL, 
                "Email already exists for another user: " + user.email);
        }
        
        // Check uniqueness for username (except for the current user)
        Optional<User> existingUsername = userRepository.findByUsername(user.username);
        if (existingUsername.isPresent() && !existingUsername.get().id.equals(id)) {
            throw new UserServiceException(UserServiceException.ErrorType.DUPLICATE_USERNAME, 
                "Username already exists for another user: " + user.username);
        }
        
        User entity = entityOpt.get();
        entity.username = user.username;
        if (user.passwordHash != null && !user.passwordHash.isEmpty()) {
            entity.passwordHash = hashPassword(user.passwordHash);
        }
        entity.userData = user.userData;
        entity.email = user.email;
        userRepository.save(entity);
        return entity;
    }

    @Transactional
    public boolean deleteUser(Long id, String email) {
        Optional<User> entityOpt = userRepository.findByEmail(email);
        if (entityOpt.isEmpty()) {
            return false;
        }
        userRepository.deleteById(entityOpt.get().id);
        return true;
    }
} 
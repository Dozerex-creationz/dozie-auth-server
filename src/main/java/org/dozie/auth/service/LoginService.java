package org.dozie.auth.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.dozie.auth.repository.user.UserRepositoryAdapter;
import org.dozie.auth.repository.userrole.UserRoleRepositoryAdapter;
import org.dozie.common.model.response.ApiResponse;
import org.dozie.auth.model.entity.User;
import org.dozie.auth.model.entity.UserRole;
import org.dozie.auth.model.dto.LoginResponse;
import org.dozie.auth.model.message.SuccessMessage;
import org.dozie.auth.model.message.ErrorMessage;
import org.dozie.common.service.JwtService;
import org.dozie.common.model.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class LoginService {

    @Inject
    UserRepositoryAdapter userRepository;

    @Inject
    UserRoleRepositoryAdapter userRoleRepository;

    @Inject
    JwtService jwtService;

    /**
     * Authenticates a user based on username and password and generates JWT tokens.
     *
     * @param username The username to authenticate.
     * @param password The password for the given username.
     * @return ApiResponse containing login response with tokens and user details.
     */
    @Transactional
    public ApiResponse login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return new ApiResponse(null, ErrorCode.USER_NOT_FOUND.getMessage() + " - " + ErrorCode.USER_NOT_FOUND.getCode());
        }
        
        User user = userOpt.get();
        
        // Use BCrypt to verify password
        if (!BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash).verified) {
            return new ApiResponse(null, ErrorCode.INVALID_PASSWORD.getMessage() + " - " + ErrorCode.INVALID_PASSWORD.getCode());
        }

        // Get user roles
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.id);
        List<String> roleNames = userRoles.stream()
            .map(userRole -> userRole.role.name)
            .collect(Collectors.toList());
        List<String> scopes = List.of("read", "write");

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user.id.toString(), roleNames, scopes);
        String refreshToken = jwtService.generateRefreshToken(user.id.toString());

        if (accessToken == null || refreshToken == null) {
            return new ApiResponse(null, "Failed to generate tokens");
        }

        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1 hour from now
        LocalDateTime loginTime = LocalDateTime.now();

        // Create login response
        LoginResponse loginResponse = new LoginResponse(
            accessToken,
            refreshToken,
            user.id,
            user.username,
            user.email,
            roleNames.isEmpty() ? "USER" : roleNames.get(0),
            expiresAt,
            loginTime
        );

        return new ApiResponse(loginResponse, "Login successful - 200");
    }
}

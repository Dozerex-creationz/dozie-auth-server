package org.dozie.oauth.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_oauth_tokens")
public class OAuthToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "access_token", nullable = false, unique = true)
    public String accessToken;

    @Column(name = "refresh_token", unique = true)
    public String refreshToken;

    @Column(name = "token_type", nullable = false)
    public String tokenType;

    @Column(name = "expires_in")
    public Integer expiresIn;

    @Column(name = "scope")
    public String scope;

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "client_id", nullable = false)
    public String clientId;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "expires_at")
    public LocalDateTime expiresAt;

    public OAuthToken() {
        this.createdAt = LocalDateTime.now();
    }
} 
package org.dozie.oauth.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_oauth_auth_codes")
public class OAuthAuthCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "code", nullable = false, unique = true)
    public String code;

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "client_id", nullable = false)
    public String clientId;

    @Column(name = "redirect_uri")
    public String redirectUri;

    @Column(name = "scope")
    public String scope;

    @Column(name = "state")
    public String state;

    @Column(name = "expires_at", nullable = false)
    public LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    public Boolean used = false;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    public OAuthAuthCode() {
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }
} 
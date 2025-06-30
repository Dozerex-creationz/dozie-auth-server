package org.dozie.oauth.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_oauth_clients")
public class OAuthClient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "client_id", nullable = false, unique = true)
    public String clientId;

    @Column(name = "client_secret", nullable = false)
    public String clientSecret;

    @Column(name = "client_name")
    public String clientName;

    @ElementCollection
    @CollectionTable(name = "tb_oauth_clients_redirect_uris", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "redirect_uri")
    public List<String> redirectUris;

    @ElementCollection
    @CollectionTable(name = "tb_oauth_clients_scopes", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "scope")
    public List<String> scopes;

    @Column(name = "grant_types")
    public String grantTypes;

    @Column(name = "is_active")
    public Boolean isActive;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    public OAuthClient() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
} 
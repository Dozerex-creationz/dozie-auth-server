package org.dozie.oauth.model.entity;

import jakarta.persistence.*;
import org.dozie.common.model.entity.BaseEntity;

@Entity
@Table(name = "tb_oauth_clients_redirect_uris")
public class OAuthClientRedirectUri extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    public OAuthClient client;

    @Column(name = "redirect_uri", nullable = false)
    public String redirectUri;
} 
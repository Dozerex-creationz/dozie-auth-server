package org.dozie.oauth.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.dozie.oauth.model.entity.OAuthAuthCode;
import org.dozie.oauth.model.entity.OAuthClient;
import org.dozie.oauth.model.entity.OAuthToken;

import java.util.List;

@ApplicationScoped
public class OAuthRepository {

    @Inject
    EntityManager entityManager;

    @Transactional
    public OAuthClient findClientById(String clientId) {
        TypedQuery<OAuthClient> query = entityManager.createQuery(
            "SELECT c FROM OAuthClient c WHERE c.clientId = :clientId", OAuthClient.class);
        query.setParameter("clientId", clientId);
        
        List<OAuthClient> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public List<OAuthClient> findAllClients() {
        TypedQuery<OAuthClient> query = entityManager.createQuery(
            "SELECT c FROM OAuthClient c", OAuthClient.class);
        return query.getResultList();
    }

    @Transactional
    public void saveClient(OAuthClient client) {
        entityManager.persist(client);
    }

    @Transactional
    public void updateClient(OAuthClient client) {
        entityManager.merge(client);
    }

    @Transactional
    public void deleteClient(Long id) {
        OAuthClient client = entityManager.find(OAuthClient.class, id);
        if (client != null) {
            entityManager.remove(client);
        }
    }

    @Transactional
    public OAuthAuthCode findAuthCodeByCode(String code) {
        TypedQuery<OAuthAuthCode> query = entityManager.createQuery(
            "SELECT a FROM OAuthAuthCode a WHERE a.code = :code", OAuthAuthCode.class);
        query.setParameter("code", code);
        
        List<OAuthAuthCode> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void saveAuthCode(OAuthAuthCode authCode) {
        entityManager.persist(authCode);
    }

    @Transactional
    public void updateAuthCode(OAuthAuthCode authCode) {
        entityManager.merge(authCode);
    }

    @Transactional
    public void deleteAuthCode(Long id) {
        OAuthAuthCode authCode = entityManager.find(OAuthAuthCode.class, id);
        if (authCode != null) {
            entityManager.remove(authCode);
        }
    }

    @Transactional
    public OAuthToken findTokenByAccessToken(String accessToken) {
        TypedQuery<OAuthToken> query = entityManager.createQuery(
            "SELECT t FROM OAuthToken t WHERE t.accessToken = :accessToken", OAuthToken.class);
        query.setParameter("accessToken", accessToken);
        
        List<OAuthToken> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public OAuthToken findTokenByRefreshToken(String refreshToken) {
        TypedQuery<OAuthToken> query = entityManager.createQuery(
            "SELECT t FROM OAuthToken t WHERE t.refreshToken = :refreshToken", OAuthToken.class);
        query.setParameter("refreshToken", refreshToken);
        
        List<OAuthToken> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void saveToken(OAuthToken token) {
        entityManager.persist(token);
    }

    @Transactional
    public void updateToken(OAuthToken token) {
        entityManager.merge(token);
    }

    @Transactional
    public void deleteToken(Long id) {
        OAuthToken token = entityManager.find(OAuthToken.class, id);
        if (token != null) {
            entityManager.remove(token);
        }
    }

    @Transactional
    public List<OAuthToken> findTokensByClientId(String clientId) {
        TypedQuery<OAuthToken> query = entityManager.createQuery(
            "SELECT t FROM OAuthToken t WHERE t.clientId = :clientId", OAuthToken.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Transactional
    public List<String> findRedirectUrisByClientId(String clientId) {
        TypedQuery<String> query = entityManager.createQuery(
            "SELECT r.redirectUri FROM OAuthClient c JOIN c.redirectUris r WHERE c.clientId = :clientId", String.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Transactional
    public List<String> findScopesByClientId(String clientId) {
        TypedQuery<String> query = entityManager.createQuery(
            "SELECT s.scope FROM OAuthClient c JOIN c.scopes s WHERE c.clientId = :clientId", String.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Transactional
    public boolean isValidRedirectUri(String clientId, String redirectUri) {
        Query query = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM tb_oauth_clients_redirect_uris r " +
            "JOIN tb_oauth_clients c ON r.client_id = c.id " +
            "WHERE c.client_id = :clientId AND r.redirect_uri = :redirectUri");
        query.setParameter("clientId", clientId);
        query.setParameter("redirectUri", redirectUri);
        
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }
} 
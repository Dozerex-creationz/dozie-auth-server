-- Test OAuth Client for testing
INSERT INTO tb_oauth_clients (client_id, client_secret, client_name, grant_types, is_active, created_at, updated_at) 
VALUES ('test-client', 'test-secret', 'Test OAuth Client', 'authorization_code', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert redirect URIs for the test client
INSERT INTO tb_oauth_clients_redirect_uris (client_id, redirect_uri) 
VALUES (1, 'http://myapp.com/callback');

INSERT INTO tb_oauth_clients_redirect_uris (client_id, redirect_uri) 
VALUES (1, 'http://localhost:3000/callback');

-- Insert scopes for the test client
INSERT INTO tb_oauth_clients_scopes (client_id, scope) 
VALUES (1, 'read');

INSERT INTO tb_oauth_clients_scopes (client_id, scope) 
VALUES (1, 'write');

INSERT INTO tb_oauth_clients_scopes (client_id, scope) 
VALUES (1, 'admin'); 
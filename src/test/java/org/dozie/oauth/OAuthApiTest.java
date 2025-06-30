package org.dozie.oauth;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import io.quarkus.test.junit.QuarkusTest;
import org.dozie.auth.BaseApiTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Order(6)
public class OAuthApiTest extends BaseApiTest {

    private static final String OAUTH_PATH = "/oauth";
    private static final String AUTHORIZE_PATH = OAUTH_PATH + "/authorize";
    private static final String TOKEN_PATH = OAUTH_PATH + "/token";

    @Test
    public void testAuthorizeEndpointSuccess() {
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read")
            .queryParam("state", "test-state")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("http://localhost:3000/callback"))
            .header("Location", containsString("code="))
            .header("Location", containsString("state=test-state"));
    }

    @Test
    public void testAuthorizeEndpointMissingResponseType() {
        given()
            .redirects().follow(false)
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("http://localhost:3000/callback"))
            .header("Location", containsString("error=unsupported_response_type"))
            .header("Location", containsString("error_description=Response+type+is+required"));
    }

    @Test
    public void testAuthorizeEndpointInvalidResponseType() {
        given()
            .redirects().follow(false)
            .queryParam("response_type", "token")
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("http://localhost:3000/callback"))
            .header("Location", containsString("error=unsupported_response_type"));
    }

    @Test
    public void testAuthorizeEndpointMissingClientId() {
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("http://localhost:3000/callback"))
            .header("Location", containsString("error=invalid_request"));
    }

    @Test
    public void testAuthorizeEndpointMissingRedirectUri() {
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "test-client")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("error=invalid_request"))
            .header("Location", containsString("error_description=Redirect+URI+is+required"));
    }

    @Test
    public void testAuthorizeEndpointInvalidClient() {
        // Test with non-existent client_id
        given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=invalid-client&redirect_uri=http://localhost:3000/callback")
            .then()
            .statusCode(302)
            .header("Location", containsString("http://localhost:3000/callback"))
            .header("Location", containsString("error=unauthorized_client"));
    }

    @Test
    public void testTokenEndpointSuccess() {
        // First get an authorization code
        Response authResponse = given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=test-client&redirect_uri=http://localhost:3000/callback&scope=read&state=test-state");
        
        authResponse.then().statusCode(302);
        
        // Extract the authorization code from the redirect URL
        String location = authResponse.getHeader("Location");
        String authCode = extractAuthCodeFromLocation(location);
        
        // Exchange authorization code for access token
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", authCode)
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("expires_in", equalTo(3600))
            .body("refresh_token", notNullValue())
            .body("scope", equalTo("read"));
    }

    @Test
    public void testTokenEndpointInvalidGrantType() {
        // Test with invalid grant_type
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "client_credentials")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(400)
            .body("error", equalTo("unsupported_grant_type"))
            .body("error_description", containsString("Only 'authorization_code' grant type is supported"));
    }

    @Test
    public void testTokenEndpointMissingCode() {
        // Test missing authorization code
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(400)
            .body("error", equalTo("invalid_request"))
            .body("error_description", containsString("Authorization code is required"));
    }

    @Test
    public void testTokenEndpointMissingClientId() {
        // Test missing client_id
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", "test-code")
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(400)
            .body("error", equalTo("invalid_request"))
            .body("error_description", containsString("Client ID is required"));
    }

    @Test
    public void testTokenEndpointMissingClientSecret() {
        // Test missing client_secret
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", "test-code")
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_id", "test-client")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(400)
            .body("error", equalTo("invalid_request"))
            .body("error_description", containsString("Client secret is required"));
    }

    @Test
    public void testTokenEndpointInvalidClientCredentials() {
        // Test with invalid client credentials
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", "test-code")
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "wrong-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(401)
            .body("error", equalTo("invalid_client"))
            .body("error_description", containsString("Invalid client"));
    }

    @Test
    public void testTokenEndpointInvalidAuthCode() {
        // Test with invalid authorization code
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", "invalid-code")
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(400)
            .body("error", equalTo("invalid_grant"))
            .body("error_description", containsString("Invalid authorization code"));
    }

    @Test
    public void testTokenEndpointRedirectUriMismatch() {
        // First get an authorization code
        Response authResponse = given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=test-client&redirect_uri=http://localhost:3000/callback&scope=read");
        
        authResponse.then().statusCode(302);
        
        // Extract the authorization code
        String location = authResponse.getHeader("Location");
        String authCode = extractAuthCodeFromLocation(location);
        
        // Try to exchange with different redirect_uri
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", authCode)
            .formParam("redirect_uri", "http://different-app.com/callback")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(400)
            .body("error", equalTo("invalid_grant"))
            .body("error_description", containsString("Redirect URI mismatch"));
    }

    @Test
    public void testTokenEndpointResponseStructure() {
        // First get an authorization code
        Response authResponse = given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=test-client&redirect_uri=http://localhost:3000/callback&scope=read write");
        
        authResponse.then().statusCode(302);
        
        // Extract the authorization code
        String location = authResponse.getHeader("Location");
        String authCode = extractAuthCodeFromLocation(location);
        
        // Exchange for token and validate response structure
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", authCode)
            .formParam("redirect_uri", "http://localhost:3000/callback")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("access_token", isA(String.class))
            .body("token_type", isA(String.class))
            .body("expires_in", isA(Integer.class))
            .body("refresh_token", isA(String.class))
            .body("scope", isA(String.class));
    }

    @Test
    public void testAuthorizeEndpointWithState() {
        // Test authorization with state parameter
        given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=test-client&redirect_uri=http://localhost:3000/callback&state=random-state-123")
            .then()
            .statusCode(302)
            .header("Location", containsString("state=random-state-123"));
    }

    @Test
    public void testAuthorizeEndpointWithoutState() {
        // Test authorization without state parameter
        given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=test-client&redirect_uri=http://localhost:3000/callback")
            .then()
            .statusCode(302)
            .header("Location", containsString("code="))
            .header("Location", not(containsString("state=")));
    }

    @Test
    public void testTokenEndpointWithoutRedirectUri() {
        // First get an authorization code
        Response authResponse = given()
            .redirects().follow(false)
            .when()
            .get(AUTHORIZE_PATH + "?response_type=code&client_id=test-client&redirect_uri=http://localhost:3000/callback&scope=read");
        
        authResponse.then().statusCode(302);
        
        // Extract the authorization code
        String location = authResponse.getHeader("Location");
        String authCode = extractAuthCodeFromLocation(location);
        
        // Exchange without redirect_uri (should work since it's optional in token request)
        given()
            .contentType(ContentType.URLENC)
            .formParam("grant_type", "authorization_code")
            .formParam("code", authCode)
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post(TOKEN_PATH)
            .then()
            .statusCode(200)
            .body("access_token", notNullValue());
    }

    // Helper method to extract authorization code from redirect URL
    private String extractAuthCodeFromLocation(String location) {
        if (location != null && location.contains("code=")) {
            String[] parts = location.split("code=");
            if (parts.length > 1) {
                String codePart = parts[1];
                if (codePart.contains("&")) {
                    return codePart.split("&")[0];
                }
                return codePart;
            }
        }
        return "test-code"; // fallback for testing
    }
} 
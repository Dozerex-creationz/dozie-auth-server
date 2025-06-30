package org.dozie.oauth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.dozie.auth.BaseApiTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Order(8)
public class OAuthIntegrationTest extends BaseApiTest {

    private static final String OAUTH_PATH = "/oauth";
    private static final String AUTHORIZE_PATH = OAUTH_PATH + "/authorize";
    private static final String TOKEN_PATH = OAUTH_PATH + "/token";

    @Test
    public void testCompleteOAuthFlow() {
        // Step 1: Authorization request
        Response authResponse = given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read write")
            .queryParam("state", "random-state")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .extract().response();

        // Extract authorization code from redirect URL
        String location = authResponse.getHeader("Location");
        String authCode = extractAuthCodeFromUrl(location);

        // Step 2: Token exchange
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
            .body("access_token", notNullValue())
            .body("token_type", equalTo("Bearer"))
            .body("expires_in", isA(Integer.class))
            .body("refresh_token", notNullValue());
    }

    @Test
    public void testOAuthFlowErrorHandling() {
        // Test missing client_id
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read write")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("error=invalid_request"))
            .header("Location", containsString("error_description=Client+ID+is+required"));
    }

    @Test
    public void testOAuthFlowWithDifferentScopes() {
        // Test with different scopes
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "admin")
            .queryParam("state", "test-state")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("code="));
    }

    @Test
    public void testOAuthFlowSecurity() {
        // Test with invalid client_id
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "invalid-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read write")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("error=unauthorized_client"));
    }

    @Test
    public void testOAuthFlowWithStateParameter() {
        // Test with state parameter
        given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read write")
            .queryParam("state", "security-token-123")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .header("Location", containsString("code="))
            .header("Location", containsString("state=security-token-123"));
    }

    @Test
    public void testOAuthFlowResponseValidation() {
        // Test response structure validation
        Response authResponse = given()
            .redirects().follow(false)
            .queryParam("response_type", "code")
            .queryParam("client_id", "test-client")
            .queryParam("redirect_uri", "http://localhost:3000/callback")
            .queryParam("scope", "read write")
            .when()
            .get(AUTHORIZE_PATH)
            .then()
            .statusCode(302)
            .extract().response();

        String location = authResponse.getHeader("Location");
        assertTrue(location.contains("code="));
        assertTrue(location.startsWith("http://localhost:3000/callback"));
    }

    // Helper method to extract authorization code from redirect URL
    private String extractAuthCodeFromUrl(String location) {
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
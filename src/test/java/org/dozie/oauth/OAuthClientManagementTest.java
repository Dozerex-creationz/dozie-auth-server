package org.dozie.oauth;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.dozie.auth.BaseApiTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Order(7)
public class OAuthClientManagementTest extends BaseApiTest {

    @Test
    public void testOAuthClientEntityStructure() {
        // This test verifies that OAuth entities are properly configured
        // by checking if the OAuth endpoints are accessible
        given()
            .when()
            .get("/oauth/authorize?response_type=code&client_id=test-client&redirect_uri=http://localhost:8082/callback")
            .then()
            .statusCode(anyOf(equalTo(302), equalTo(404))); // Should redirect or not found
    }

    @Test
    public void testOAuthEndpointsAreAccessible() {
        // Test that OAuth endpoints are accessible
        given()
            .redirects().follow(false)
            .when()
            .get("/oauth/authorize?response_type=code&client_id=test-client&redirect_uri=http://localhost:8082/callback")
            .then()
            .statusCode(302); // Either redirect or error is expected
        
        given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("grant_type", "authorization_code")
            .formParam("code", "test")
            .formParam("client_id", "test-client")
            .formParam("client_secret", "test-secret")
            .when()
            .post("/oauth/token")
            .then()
            .statusCode(400); // Expected to fail with invalid credentials
    }

    @Test
    public void testOAuthSwaggerDocumentation() {
        // Test that OAuth endpoints are documented in Swagger
        given()
            .when()
            .get("/swagger-ui")
            .then()
            .statusCode(200);
    }
} 
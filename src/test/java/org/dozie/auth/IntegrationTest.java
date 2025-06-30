package org.dozie.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Order(5)
public class IntegrationTest extends BaseApiTest {

    @Test
    public void testCompleteAuthenticationFlow() {
        // Step 1: Create a user
        Response createUserResponse = createUser("integrationuser", "integration@example.com", "password123");
        createUserResponse.then().statusCode(201);
        
        String userId = createUserResponse.jsonPath().getString("id");

        // Step 2: Create a role
        Response createRoleResponse = createRole("USER_ROLE", "{\"canRead\": true, \"canWrite\": false}");
        createRoleResponse.then().statusCode(201);
        
        String roleId = createRoleResponse.jsonPath().getString("id");

        // Step 3: Login to get tokens
        Response loginResponse = loginUser("integrationuser", "password123");
        loginResponse.then().statusCode(200);
        
        String accessToken = extractAccessToken(loginResponse);
        String refreshToken = extractRefreshToken(loginResponse);

        // Step 4: Verify tokens are valid JWT format
        loginResponse.then()
            .body("data.accessToken", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"))
            .body("data.refreshToken", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"));

        // Step 5: Refresh the access token
        Response refreshResponse = refreshToken(refreshToken);
        refreshResponse.then().statusCode(200);
        
        String newAccessToken = refreshResponse.jsonPath().getString("data.accessToken");
        
        // Step 6: Verify new access token is different and valid
        refreshResponse.then()
            .body("data.accessToken", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"))
            .body("data.expiresAt", notNullValue())
            .body("data.issuedAt", notNullValue());

        // Step 7: Get user details
        Response getUserResponse = given()
            .when()
            .get(USERS_PATH + "/" + userId);
        
        getUserResponse.then()
            .statusCode(200)
            .body("id", equalTo(Integer.parseInt(userId)))
            .body("username", equalTo("integrationuser"))
            .body("email", equalTo("integration@example.com"));

        // Step 8: Get role details
        Response getRoleResponse = given()
            .when()
            .get(ROLES_PATH + "/" + roleId);
        
        getRoleResponse.then()
            .statusCode(200)
            .body("id", equalTo(Integer.parseInt(roleId)))
            .body("name", equalTo("USER_ROLE"))
            .body("rolePermission", containsString("canRead"))
            .body("rolePermission", containsString("canWrite"));

        // Step 9: Update user
        Response updateUserResponse = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"updatedintegrationuser\",\"email\":\"updatedintegration@example.com\"}")
            .when()
            .put(USERS_PATH + "/" + userId);
        
        updateUserResponse.then()
            .statusCode(200)
            .body("id", equalTo(Integer.parseInt(userId)))
            .body("username", equalTo("updatedintegrationuser"))
            .body("email", equalTo("updatedintegration@example.com"));

        // Step 10: Update role
        Response updateRoleResponse = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATED_USER_ROLE\",\"rolePermission\":\"{\\\"canRead\\\": true, \\\"canWrite\\\": true, \\\"canDelete\\\": false}\"}")
            .when()
            .put(ROLES_PATH + "/" + roleId);
        
        updateRoleResponse.then()
            .statusCode(200)
            .body("id", equalTo(Integer.parseInt(roleId)))
            .body("name", equalTo("UPDATED_USER_ROLE"))
            .body("rolePermission", containsString("canRead"))
            .body("rolePermission", containsString("canWrite"))
            .body("rolePermission", containsString("canDelete"));

        // Step 11: Login with updated credentials
        Response updatedLoginResponse = loginUser("updatedintegrationuser", "password123");
        updatedLoginResponse.then().statusCode(200);
        
        String updatedAccessToken = extractAccessToken(updatedLoginResponse);
        String updatedRefreshToken = extractRefreshToken(updatedLoginResponse);

        // Step 12: Verify new tokens are generated
        updatedLoginResponse.then()
            .body("data.accessToken", not(equalTo(accessToken)))
            .body("data.refreshToken", not(equalTo(refreshToken)))
            .body("data.username", equalTo("updatedintegrationuser"))
            .body("data.email", equalTo("updatedintegration@example.com"));

        // Step 13: Clean up - Delete role and user
        given()
            .when()
            .delete(ROLES_PATH + "/" + roleId)
            .then()
            .statusCode(204);

        given()
            .when()
            .delete(USERS_PATH + "/updatedintegration@example.com")
            .then()
            .statusCode(204);
    }

    @Test
    public void testErrorHandlingFlow() {
        // Test various error scenarios in sequence
        
        // 1. Try to login with non-existent user
        Response loginResponse = loginUser("nonexistentuser", "password123");
        loginResponse.then()
            .statusCode(200)
            .body("data", nullValue())
            .body("message", containsString("User not found"));

        // 2. Create user and try to login with wrong password
        Response createResponse = createUser("erroruser", "error@example.com", "password123");
        createResponse.then().statusCode(201);

        Response wrongPasswordResponse = loginUser("erroruser", "wrongpassword");
        wrongPasswordResponse.then()
            .statusCode(200)
            .body("data", nullValue())
            .body("message", containsString("Invalid password"));

        // 3. Try to refresh with invalid token
        Response invalidRefreshResponse = refreshToken("invalid.token.here");
        invalidRefreshResponse.then()
            .statusCode(401)
            .body("data", nullValue())
            .body("error_description", containsString("Refresh token has expired"));

        // 4. Try to get non-existent user
        given()
            .when()
            .get(USERS_PATH + "/99999")
            .then()
            .statusCode(404);

        // 5. Try to get non-existent role
        given()
            .when()
            .get(ROLES_PATH + "/99999")
            .then()
            .statusCode(404);

        // Clean up
        given()
            .when()
            .delete(USERS_PATH + "/error@example.com")
            .then()
            .statusCode(204);
    }

    @Test
    public void testConcurrentOperations() {
        // Test multiple operations happening in sequence
        
        // Create multiple users
        Response user1Response = createUser("concurrentuser1", "concurrent1@example.com", "password123");
        user1Response.then().statusCode(201);
        
        Response user2Response = createUser("concurrentuser2", "concurrent2@example.com", "password123");
        user2Response.then().statusCode(201);
        
        Response user3Response = createUser("concurrentuser3", "concurrent3@example.com", "password123");
        user3Response.then().statusCode(201);

        // Create multiple roles
        Response role1Response = createRole("ROLE_1", "{\"canRead\": true}");
        role1Response.then().statusCode(201);
        
        Response role2Response = createRole("ROLE_2", "{\"canWrite\": true}");
        role2Response.then().statusCode(201);

        // Login all users
        Response login1Response = loginUser("concurrentuser1", "password123");
        login1Response.then().statusCode(200);
        
        Response login2Response = loginUser("concurrentuser2", "password123");
        login2Response.then().statusCode(200);
        
        Response login3Response = loginUser("concurrentuser3", "password123");
        login3Response.then().statusCode(200);

        // Refresh tokens for all users
        String refreshToken1 = extractRefreshToken(login1Response);
        String refreshToken2 = extractRefreshToken(login2Response);
        String refreshToken3 = extractRefreshToken(login3Response);

        Response refresh1Response = refreshToken(refreshToken1);
        refresh1Response.then().statusCode(200);
        
        Response refresh2Response = refreshToken(refreshToken2);
        refresh2Response.then().statusCode(200);
        
        Response refresh3Response = refreshToken(refreshToken3);
        refresh3Response.then().statusCode(200);

        // Get all users and roles
        given()
            .when()
            .get(USERS_PATH)
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(3)));

        given()
            .when()
            .get(ROLES_PATH)
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(2)));

        // Clean up
        given()
            .when()
            .delete(USERS_PATH + "/concurrent1@example.com")
            .then()
            .statusCode(204);
            
        given()
            .when()
            .delete(USERS_PATH + "/concurrent2@example.com")
            .then()
            .statusCode(204);
            
        given()
            .when()
            .delete(USERS_PATH + "/concurrent3@example.com")
            .then()
            .statusCode(204);
    }
} 
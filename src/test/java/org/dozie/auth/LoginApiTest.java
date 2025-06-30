package org.dozie.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.dozie.auth.model.dto.LoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Order(1)
public class LoginApiTest extends BaseApiTest {

    @Test
    public void testLoginHealthCheck() {
        given()
            .when()
            .get(LOGIN_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(containsString("Hello from Quarkus Login Controller"));
    }

    @Test
    public void testSuccessfulLogin() {
        // First create a user
        Response createResponse = createUser("testuser", "test@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then attempt to login
        Response loginResponse = loginUser("testuser", "password123");
        
        loginResponse.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("data", notNullValue())
            .body("data.accessToken", notNullValue())
            .body("data.refreshToken", notNullValue())
            .body("data.userId", notNullValue())
            .body("data.username", equalTo("testuser"))
            .body("data.email", equalTo("test@example.com"))
            .body("data.expiresAt", notNullValue())
            .body("data.loginTime", notNullValue())
            .body("message", containsString("Login successful"))
            .body("code", notNullValue());
    }

    @Test
    public void testLoginWithInvalidUsername() {
        Response response = loginUser("nonexistent", "password123");
        
        response.then()
            .statusCode(200) // API returns 200 with error message in body
            .body("message", containsString("1001")); // Updated error code
    }

    @Test
    public void testLoginWithInvalidPassword() {
        // First create a user
        Response createResponse = createUser("testuser2", "test2@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then attempt to login with wrong password
        Response loginResponse = loginUser("testuser2", "wrongpassword");
        
        loginResponse.then()
            .statusCode(200) // API returns 200 with error message in body
            .body("message", containsString("1002")); // Updated error code
    }

    @Test
    public void testLoginWithEmptyUsername() {
        Response response = loginUser("", "password123");
        
        response.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("data", nullValue())
            .body("message", containsString("User not found"));
    }

    @Test
    public void testLoginWithEmptyPassword() {
        // First create a user
        Response createResponse = createUser("testuser3", "test3@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then attempt to login with empty password
        Response loginResponse = loginUser("testuser3", "");
        
        loginResponse.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("data", nullValue())
            .body("message", containsString("Invalid password"));
    }

    @Test
    public void testLoginWithNullCredentials() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":null,\"password\":null}")
            .when()
            .post(LOGIN_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("data", nullValue())
            .body("message", containsString("User not found"));
    }

    @Test
    public void testLoginResponseStructure() {
        // First create a user
        Response createResponse = createUser("testuser4", "test4@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then login and verify response structure
        Response loginResponse = loginUser("testuser4", "password123");
        
        loginResponse.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("data.accessToken", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"))
            .body("data.refreshToken", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$"))
            .body("data.userId", isA(Integer.class))
            .body("data.username", isA(String.class))
            .body("data.email", isA(String.class))
            .body("data.expiresAt", isA(String.class))
            .body("data.loginTime", isA(String.class))
            .body("message", isA(String.class))
            .body("code", isA(Integer.class));
    }
} 
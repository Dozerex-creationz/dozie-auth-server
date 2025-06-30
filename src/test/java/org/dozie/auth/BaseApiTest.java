package org.dozie.auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public abstract class BaseApiTest {

    protected static final String BASE_PATH = "";
    protected static final String LOGIN_PATH = "/login";
    protected static final String USERS_PATH = "/users";
    protected static final String ROLES_PATH = "/roles";
    protected static final String TOKENS_PATH = "/tokens";

    @BeforeAll
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void setUp() {
        // Reset database state before each test
        // This will be handled by @Transactional or manual cleanup
    }

    protected Response loginUser(String username, String password) {
        return given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}")
            .when()
            .post(LOGIN_PATH);
    }

    protected Response createUser(String username, String email, String password) {
        return given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
            .when()
            .post(USERS_PATH);
    }

    protected Response createRole(String name, String permissions) {
        return given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"" + name + "\",\"rolePermission\":\"" + permissions.replace("\"", "\\\"") + "\"}")
            .when()
            .post(ROLES_PATH);
    }

    protected Response refreshToken(String refreshToken) {
        return given()
            .contentType(ContentType.JSON)
            .body("{\"refreshToken\":\"" + refreshToken + "\"}")
            .when()
            .post(TOKENS_PATH + "/refresh");
    }

    protected String extractAccessToken(Response loginResponse) {
        return loginResponse.jsonPath().getString("data.accessToken");
    }

    protected String extractRefreshToken(Response loginResponse) {
        return loginResponse.jsonPath().getString("data.refreshToken");
    }

    protected String extractUserId(Response loginResponse) {
        return loginResponse.jsonPath().getString("data.userId");
    }
} 
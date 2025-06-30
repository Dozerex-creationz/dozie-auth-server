package org.dozie.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Order(2)
public class UserApiTest extends BaseApiTest {

    @Test
    public void testGetAllUsers() {
        // First create a user
        Response createResponse = createUser("testuser1", "test1_getall@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then get all users
        given()
            .when()
            .get(USERS_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(greaterThan(0)))
            .body("[0].id", notNullValue())
            .body("[0].username", notNullValue())
            .body("[0].email", notNullValue());
    }

    @Test
    public void testGetUserById() {
        // First create a user
        Response createResponse = createUser("testuser2_gbu", "test2_getbyid@example.com", "password123");
        createResponse.then().statusCode(201);
        
        String userId = createResponse.jsonPath().getString("id");

        // Then get user by ID
        given()
            .when()
            .get(USERS_PATH + "/" + userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(Integer.parseInt(userId)))
            .body("username", equalTo("testuser2_gbu"))
            .body("email", equalTo("test2_getbyid@example.com"));
    }

    @Test
    public void testGetUserByIdNotFound() {
        given()
            .when()
            .get(USERS_PATH + "/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateUser() {
        Response response = createUser("newuser", "newuser_create@example.com", "password123");
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("username", equalTo("newuser"))
            .body("email", equalTo("newuser_create@example.com"))
            .body("passwordHash", notNullValue());
    }

    @Test
    public void testCreateUserWithDuplicateEmail() {
        // First create a user
        Response createResponse = createUser("user1_duplicate", "duplicate@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then try to create another user with the same email
        Response duplicateResponse = createUser("user2_duplicate", "duplicate@example.com", "password456");
        
        duplicateResponse.then()
            .statusCode(400)
            .body(containsString("Email already exists: duplicate@example.com"));
    }

    @Test
    public void testCreateUserWithInvalidEmail() {
        Response response = createUser("invaliduser", "invalid-email", "password123");
        
        response.then()
            .statusCode(400)
            .body(containsString("Invalid email format: invalid-email"));
    }

    @Test
    public void testUpdateUser() {
        // First create a user
        Response createResponse = createUser("updateuser", "update@example.com", "password123");
        createResponse.then().statusCode(201);
        
        String userId = createResponse.jsonPath().getString("id");

        // Then update the user
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"updateduser\",\"email\":\"updated@example.com\",\"passwordHash\":\"newpassword123\"}")
            .when()
            .put(USERS_PATH + "/" + userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(Integer.parseInt(userId)))
            .body("username", equalTo("updateduser"))
            .body("email", equalTo("updated@example.com"));
    }

    @Test
    public void testUpdateUserNotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"updateduser\",\"email\":\"updated@example.com\"}")
            .when()
            .put(USERS_PATH + "/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testUpdateUserWithDuplicateEmail() {
        // First create two users
        Response createResponse1 = createUser("user1", "user1_update@example.com", "password123");
        createResponse1.then().statusCode(201);
        
        Response createResponse2 = createUser("user2", "user2_update@example.com", "password123");
        createResponse2.then().statusCode(201);
        
        String userId1 = createResponse1.jsonPath().getString("id");

        // Then try to update user1 with user2's email
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"user1\",\"email\":\"user2_update@example.com\"}")
            .when()
            .put(USERS_PATH + "/" + userId1)
            .then()
            .statusCode(400)
            .body(containsString("Email already exists for another user: user2_update@example.com"));
    }

    @Test
    public void testDeleteUser() {
        // First create a user
        Response createResponse = createUser("deleteuser", "delete@example.com", "password123");
        createResponse.then().statusCode(201);

        // Then delete the user
        given()
            .when()
            .delete(USERS_PATH + "/delete@example.com")
            .then()
            .statusCode(204);
    }

    @Test
    public void testDeleteUserNotFound() {
        given()
            .when()
            .delete(USERS_PATH + "/nonexistent@example.com")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateUserWithUserData() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"userwithdata\",\"email\":\"userdata@example.com\",\"password\":\"password123\",\"userData\":\"{\\\"firstName\\\":\\\"John\\\",\\\"lastName\\\":\\\"Doe\\\"}\"}")
            .when()
            .post(USERS_PATH);
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("username", equalTo("userwithdata"))
            .body("email", equalTo("userdata@example.com"))
            .body("userData", containsString("John"))
            .body("userData", containsString("Doe"));
    }

    @Test
    public void testUserResponseStructure() {
        Response response = createUser("structureuser", "structure@gmail.com", "password123");
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", isA(Integer.class))
            .body("username", isA(String.class))
            .body("email", isA(String.class))
            .body("passwordHash", isA(String.class))
            .body("userData", anyOf(isA(String.class), nullValue()));
    }
} 
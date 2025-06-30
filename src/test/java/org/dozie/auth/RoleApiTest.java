package org.dozie.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@Order(3)
public class RoleApiTest extends BaseApiTest {

    @Test
    public void testGetAllRoles() {
        // First create a role
        Response createResponse = createRole("TEST_ROLE", "{\"canRead\": true}");
        createResponse.then().statusCode(201);

        // Then get all roles
        given()
            .when()
            .get(ROLES_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(greaterThan(0)))
            .body("[0].id", notNullValue())
            .body("[0].name", notNullValue());
    }

    @Test
    public void testGetRoleById() {
        // First create a role
        Response createResponse = createRole("TEST_ROLE_2", "{\"canWrite\": true}");
        createResponse.then().statusCode(201);
        
        String roleId = createResponse.jsonPath().getString("id");

        // Then get role by ID
        given()
            .when()
            .get(ROLES_PATH + "/" + roleId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(Integer.parseInt(roleId)))
            .body("name", equalTo("TEST_ROLE_2"))
            .body("rolePermission", containsString("canWrite"));
    }

    @Test
    public void testGetRoleByIdNotFound() {
        given()
            .when()
            .get(ROLES_PATH + "/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateRole() {
        Response response = createRole("NEW_ROLE", "{\"canRead\": true, \"canWrite\": false}");
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("name", equalTo("NEW_ROLE"))
            .body("rolePermission", containsString("canRead"))
            .body("rolePermission", containsString("canWrite"));
    }

    @Test
    public void testCreateRoleWithDuplicateName() {
        // First create a role
        Response createResponse = createRole("DUPLICATE_ROLE", "{\"canRead\": true}");
        createResponse.then().statusCode(201);

        // Then try to create another role with the same name
        Response duplicateResponse = createRole("DUPLICATE_ROLE", "{\"canWrite\": true}");
        
        duplicateResponse.then()
            .statusCode(400);
    }

    @Test
    public void testCreateRoleWithEmptyName() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"\",\"rolePermission\":\"{\\\"canRead\\\": true}\"}")
            .when()
            .post(ROLES_PATH)
            .then()
            .statusCode(400);
    }

    @Test
    public void testUpdateRole() {
        // First create a role
        Response createResponse = createRole("UPDATE_ROLE", "{\"canRead\": true}");
        createResponse.then().statusCode(201);
        
        String roleId = createResponse.jsonPath().getString("id");

        // Then update the role
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATED_ROLE\",\"rolePermission\":\"{\\\"canRead\\\": true, \\\"canDelete\\\": true}\"}")
            .when()
            .put(ROLES_PATH + "/" + roleId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(Integer.parseInt(roleId)))
            .body("name", equalTo("UPDATED_ROLE"))
            .body("rolePermission", containsString("canRead"))
            .body("rolePermission", containsString("canDelete"));
    }

    @Test
    public void testUpdateRoleNotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"UPDATED_ROLE\",\"rolePermission\":\"{\\\"canRead\\\": true}\"}")
            .when()
            .put(ROLES_PATH + "/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testDeleteRole() {
        // First create a role
        Response createResponse = createRole("DELETE_ROLE", "{\"canRead\": true}");
        createResponse.then().statusCode(201);
        
        String roleId = createResponse.jsonPath().getString("id");

        // Then delete the role
        given()
            .when()
            .delete(ROLES_PATH + "/" + roleId)
            .then()
            .statusCode(204);
    }

    @Test
    public void testDeleteRoleNotFound() {
        given()
            .when()
            .delete(ROLES_PATH + "/99999")
            .then()
            .statusCode(404);
    }

    @Test
    public void testCreateRoleWithComplexPermissions() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"ADMIN_ROLE\",\"rolePermission\":\"{\\\"canRead\\\": true, \\\"canWrite\\\": true, \\\"canDelete\\\": true, \\\"canManageUsers\\\": true, \\\"canManageRoles\\\": true}\"}")
            .when()
            .post(ROLES_PATH);
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("name", equalTo("ADMIN_ROLE"))
            .body("rolePermission", containsString("canRead"))
            .body("rolePermission", containsString("canWrite"))
            .body("rolePermission", containsString("canDelete"))
            .body("rolePermission", containsString("canManageUsers"))
            .body("rolePermission", containsString("canManageRoles"));
    }

    @Test
    public void testRoleResponseStructure() {
        Response response = createRole("STRUCTURE_ROLE", "{\"canRead\": true}");
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", isA(Integer.class))
            .body("name", isA(String.class))
            .body("rolePermission", anyOf(isA(String.class), nullValue()));
    }

    @Test
    public void testCreateRoleWithNullPermissions() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"NULL_PERM_ROLE\",\"rolePermission\":null}")
            .when()
            .post(ROLES_PATH);
        
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("name", equalTo("NULL_PERM_ROLE"));
    }

    @Test
    public void testUpdateRoleWithEmptyPermissions() {
        // First create a role
        Response createResponse = createRole("EMPTY_PERM_ROLE", "{\"canRead\": true}");
        createResponse.then().statusCode(201);
        
        String roleId = createResponse.jsonPath().getString("id");

        // Then update with empty permissions
        given()
            .contentType(ContentType.JSON)
            .body("{\"name\":\"EMPTY_PERM_ROLE\",\"rolePermission\":\"\"}")
            .when()
            .put(ROLES_PATH + "/" + roleId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(Integer.parseInt(roleId)))
            .body("name", equalTo("EMPTY_PERM_ROLE"))
            .body("rolePermission", equalTo(""));
    }
} 
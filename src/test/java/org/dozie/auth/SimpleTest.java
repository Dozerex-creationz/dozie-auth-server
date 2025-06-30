package org.dozie.auth;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class SimpleTest {

    @Test
    public void testHealthCheck() {
        given()
            .when()
            .get("/login")
            .then()
            .statusCode(200)
            .body(containsString("Hello from Quarkus Login Controller"));
    }
} 
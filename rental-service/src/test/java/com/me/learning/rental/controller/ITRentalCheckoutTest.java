package com.me.learning.rental.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

/**
 * Integration tests for the Sakila checkout flow exposed by {@link RentalController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Rental checkout — REST-Assured Integration Tests")
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.UnitTestShouldIncludeAssert"})
class ITRentalCheckoutTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void configureRestAssured () {
        RestAssured.port = port;
        RestAssured.basePath = "/rental/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("201 creates both rental and payment in one transaction")
    void checkout_withValidPayload_createsRentalAndPayment() {
        String body = """
                {
                  "rental": {
                    "rentalDate": "2025-02-01T10:00:00Z",
                    "inventoryId": 10,
                    "customerId": 3,
                    "staffId": 1,
                    "lastUpdate": "2025-02-01T10:00:00Z"
                  },
                  "payment": {
                    "customerId": 3,
                    "staffId": 1,
                    "amount": 4.99,
                    "paymentDate": "2025-02-01T10:01:00Z",
                    "lastUpdate": "2025-02-01T10:01:00Z"
                  }
                }
                """;

        Integer rentalId = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/v1/rentals/checkout")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("rental.id", notNullValue())
                .body("payment.id", notNullValue())
                .body("rental.customerId", equalTo(3))
                .body("payment.customerId", equalTo(3))
                .body("payment.rentalId", notNullValue())
                .extract().path("rental.id");

        given()
                .when().get("/api/v1/rentals/" + rentalId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(rentalId));
    }

    @Test
    @DisplayName("400 when payment customerId does not match rental customerId")
    void checkout_withMismatchedCustomer_returns400() {
        String body = """
                {
                  "rental": {
                    "rentalDate": "2025-02-01T10:00:00Z",
                    "inventoryId": 11,
                    "customerId": 3,
                    "staffId": 1,
                    "lastUpdate": "2025-02-01T10:00:00Z"
                  },
                  "payment": {
                    "customerId": 99,
                    "staffId": 1,
                    "amount": 4.99,
                    "paymentDate": "2025-02-01T10:01:00Z",
                    "lastUpdate": "2025-02-01T10:01:00Z"
                  }
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/v1/rentals/checkout")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}



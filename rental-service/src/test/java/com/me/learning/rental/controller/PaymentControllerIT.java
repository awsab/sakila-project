package com.me.learning.rental.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.time.Instant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.me.learning.rental.entity.Payment;
import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.repository.PaymentRepository;
import com.me.learning.rental.repository.RentalRepository;

/**
 * REST-Assured integration tests for {@link PaymentController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PaymentController — REST-Assured Integration Tests")
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.UnitTestShouldIncludeAssert"})
class PaymentControllerIT {

    private static final String PAYMENTS_PATH = "/api/v1/payments";
    private static final String PAYMENT_DATE = "2025-01-01T10:05:00Z";
    private static final String LAST_UPDATE = "2025-01-01T10:05:00Z";
    private static final BigDecimal AMOUNT = new BigDecimal("4.99");
    private static final int CUSTOMER_ID = 1;
    private static final short STAFF_ID = 1;

    @LocalServerPort
    private int port;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private int seededRentalId;

    @BeforeAll
    void setUpReferenceData () {
        Rental rental = new Rental();
        rental.setRentalDate(Instant.parse("2025-01-01T10:00:00Z"));
        rental.setInventoryId(1);
        rental.setCustomerId(CUSTOMER_ID);
        rental.setStaffId(STAFF_ID);
        rental.setLastUpdate(Instant.parse("2025-01-01T10:00:00Z"));
        rental = rentalRepository.save(rental);
        seededRentalId = rental.getId();

        Payment payment = new Payment();
        payment.setCustomerId(CUSTOMER_ID);
        payment.setStaffId(STAFF_ID);
        payment.setRental(rental);
        payment.setAmount(AMOUNT);
        payment.setPaymentDate(Instant.parse(PAYMENT_DATE));
        payment.setLastUpdate(Instant.parse(LAST_UPDATE));
        paymentRepository.save(payment);
    }

    @BeforeEach
    void configureRestAssured () {
        RestAssured.port = port;
        RestAssured.basePath = "/rental/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private String buildCreateJson () {
        return """
                {
                  "customerId": %d,
                  "staffId": %d,
                  "rental": { "id": %d },
                  "amount": %s,
                  "paymentDate": "%s",
                  "lastUpdate": "%s"
                }
                """.formatted(CUSTOMER_ID, STAFF_ID, seededRentalId, AMOUNT.toPlainString(), PAYMENT_DATE, LAST_UPDATE);
    }

    private int createPaymentAndReturnId () {
        return given()
                .contentType(ContentType.JSON)
                .body(buildCreateJson())
                .when().post(PAYMENTS_PATH)
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
    }

    @Nested
    @DisplayName("POST /api/v1/payments")
    class CreateTests {

        @Test
        @DisplayName("201 creates payment linked to existing rental")
        void create_withRentalId_returnsCreatedPayment() {
            given()
                    .contentType(ContentType.JSON)
                    .body(buildCreateJson())
                    .when().post(PAYMENTS_PATH)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue())
                    .body("rentalId", equalTo(seededRentalId))
                    .body("amount", equalTo(AMOUNT.floatValue()));
        }

        @Test
        @DisplayName("404 when referenced rental ID does not exist")
        void create_nonExistentRental_returns404() {
            String body = buildCreateJson().replace("\"id\": " + seededRentalId, "\"id\": 999999");

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(PAYMENTS_PATH)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/payments")
    class ReadTests {

        @Test
        @DisplayName("200 returns payment by id")
        void findById_existingPayment_returns200() {
            int id = createPaymentAndReturnId();

            given()
                    .when().get(PAYMENTS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id))
                    .body("rentalId", equalTo(seededRentalId));
        }

        @Test
        @DisplayName("200 count returns non-negative integer")
        void count_returnsCount() {
            given()
                    .when().get(PAYMENTS_PATH + "/count")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", greaterThanOrEqualTo(1));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/payments/{id}")
    class DeleteTests {

        @Test
        @DisplayName("204 deletes payment")
        void delete_existingPayment_returns204() {
            int id = createPaymentAndReturnId();

            given()
                    .when().delete(PAYMENTS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}


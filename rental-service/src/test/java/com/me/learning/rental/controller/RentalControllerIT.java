package com.me.learning.rental.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.repository.RentalRepository;

/**
 * Full REST-Assured integration tests for {@link RentalController}.
 *
 * <p>The application starts with a real embedded server on a random port
 * ({@link SpringBootTest.WebEnvironment#RANDOM_PORT}) backed by the H2
 * in-memory database.  Reference rental data is seeded once in
 * {@link #setUpReferenceData()} before any test runs.
 *
 * <p>Each endpoint group is isolated in its own {@link Nested} class so that
 * the test report has a clear per-operation breakdown.  Helper methods
 * ({@link #buildCreateJson} / {@link #createRentalAndReturnId}) keep
 * individual test bodies concise.
 *
 * <p>Tests use only lenient numeric assertions (e.g. {@code ≥ 1}) when other
 * parallel tests may have left rows in the shared H2 database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("RentalController — REST-Assured Integration Tests")
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.UnitTestShouldIncludeAssert"})
class RentalControllerIT {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String RENTALS_PATH   = "/api/v1/rentals";
    private static final String RENTAL_DATE    = "2025-03-01T10:00:00Z";
    private static final int    INVENTORY_ID   = 101;
    private static final int    CUSTOMER_ID    = 21;
    private static final short  STAFF_ID       = 1;
    private static final String LAST_UPDATE    = "2025-03-01T10:00:00Z";

    @LocalServerPort
    private int port;

    @Autowired
    private RentalRepository rentalRepository;

    private final AtomicInteger rentalDateSequence = new AtomicInteger (0);

    // ── One-time reference data setup ─────────────────────────────────────────

    /**
     * Seeds a minimal Rental row once before any test runs.
     * Individual test-created rentals are created per test via the REST endpoint.
     */
    @BeforeAll
    void setUpReferenceData() {
        Rental rental = new Rental();
        rental.setRentalDate(Instant.parse(RENTAL_DATE).minusSeconds(60));
        rental.setInventoryId(999);
        rental.setCustomerId(999);
        rental.setStaffId(STAFF_ID);
        rental.setLastUpdate(Instant.parse(LAST_UPDATE));
        rentalRepository.save(rental);
    }

    // ── Per-test REST Assured configuration ───────────────────────────────────

    @BeforeEach
    void configureRestAssured() {
        RestAssured.port     = port;
        RestAssured.basePath = "/rental/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    /**
     * Builds a minimal valid rental creation JSON body.
     */
    private String buildCreateJson() {
        return """
                {
                  "rentalDate": "%s",
                  "inventoryId": %d,
                  "customerId": %d,
                  "staffId": %d,
                  "lastUpdate": "%s"
                }
                """.formatted(nextRentalDate(), INVENTORY_ID, CUSTOMER_ID, STAFF_ID, LAST_UPDATE);
    }

    private String nextRentalDate() {
        return Instant.parse(RENTAL_DATE)
                .plusSeconds(rentalDateSequence.incrementAndGet())
                .toString();
    }

    /**
     * Posts a create request and returns the generated rental ID.
     * Asserts 201 status so callers can trust the ID is valid.
     */
    private int createRentalAndReturnId() {
        return given()
                .contentType(ContentType.JSON)
                .body(buildCreateJson())
                .when().post(RENTALS_PATH)
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // POST /api/v1/rentals — create()
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/v1/rentals — create()")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateRentalTests {

        @Test
        @Order(1)
        @DisplayName("201 with full valid payload — body contains generated id and all sent fields")
        void create_withFullValidPayload_returns201AndBodyFields() {
            given()
                    .contentType(ContentType.JSON)
                    .body(buildCreateJson())
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .contentType(ContentType.JSON)
                    .body("id", notNullValue())
                    .body("inventoryId", equalTo(INVENTORY_ID))
                    .body("customerId", equalTo(CUSTOMER_ID))
                    .body("staffId", equalTo((int) STAFF_ID));
        }

        @Test
        @Order(2)
        @DisplayName("201 — id in the request body is ignored; id is always DB-generated")
        void create_withIdInPayload_dbGeneratesNewId() {
            String body = """
                    {
                      "id": 99999,
                      "rentalDate": "%s",
                      "inventoryId": %d,
                      "customerId": %d,
                      "staffId": %d,
                      "lastUpdate": "%s"
                    }
                    """.formatted(nextRentalDate(), INVENTORY_ID, CUSTOMER_ID, STAFF_ID, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", not(equalTo(99_999)));
        }

        @Test
        @Order(3)
        @DisplayName("201 — null optional returnDate is accepted")
        void create_withNullReturnDate_returns201() {
            given()
                    .contentType(ContentType.JSON)
                    .body(buildCreateJson())
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", notNullValue());
        }

        @Test
        @Order(4)
        @DisplayName("400 when rentalDate is missing (@NotNull violation)")
        void create_missingRentalDate_returns400() {
            String body = """
                    {
                      "inventoryId": %d,
                      "customerId": %d,
                      "staffId": %d,
                      "lastUpdate": "%s"
                    }
                    """.formatted(INVENTORY_ID, CUSTOMER_ID, STAFF_ID, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Order(5)
        @DisplayName("400 when inventoryId is missing (@NotNull violation)")
        void create_missingInventoryId_returns400() {
            String body = """
                    {
                      "rentalDate": "%s",
                      "customerId": %d,
                      "staffId": %d,
                      "lastUpdate": "%s"
                    }
                    """.formatted(nextRentalDate(), CUSTOMER_ID, STAFF_ID, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Order(6)
        @DisplayName("400 when customerId is missing (@NotNull violation)")
        void create_missingCustomerId_returns400() {
            String body = """
                    {
                      "rentalDate": "%s",
                      "inventoryId": %d,
                      "staffId": %d,
                      "lastUpdate": "%s"
                    }
                    """.formatted(nextRentalDate(), INVENTORY_ID, STAFF_ID, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Order(7)
        @DisplayName("400 when request body is empty JSON object")
        void create_emptyJsonBody_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{}")
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Order(8)
        @DisplayName("400 when Content-Type header is missing (no application/json)")
        void create_missingContentType_returns400OrUnsupportedMedia() {
            given()
                    .body(buildCreateJson())
                    .when().post(RENTALS_PATH)
                    .then()
                    .statusCode(anyOf(
                            equalTo(HttpStatus.BAD_REQUEST.value()),
                            equalTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
                            equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GET /api/v1/rentals/{id} — findById()
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/rentals/{id} — findById()")
    class FindByIdTests {

        @Test
        @DisplayName("200 with all fields when rental exists")
        void findById_existingId_returns200WithAllFields() {
            int id = createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id))
                    .body("inventoryId", equalTo(INVENTORY_ID))
                    .body("customerId", equalTo(CUSTOMER_ID))
                    .body("staffId", equalTo((int) STAFF_ID));
        }

        @Test
        @DisplayName("404 when rental ID does not exist")
        void findById_nonExistentId_returns404() {
            given()
                    .when().get(RENTALS_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("200 — rentalDate is persisted and returned as ISO-8601 timestamp")
        void findById_rentalDateIsPersistedAndReturned() {
            int id = createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("rentalDate", notNullValue());
        }

        @Test
        @DisplayName("400 or 404 when id path variable is not a valid integer")
        void findById_nonNumericId_returnsClientError() {
            given()
                    .when().get(RENTALS_PATH + "/not-a-number")
                    .then()
                    .statusCode(anyOf(
                            equalTo(HttpStatus.BAD_REQUEST.value()),
                            equalTo(HttpStatus.NOT_FOUND.value())));
        }

        @Test
        @DisplayName("404 after rental is deleted — get returns 404")
        void findById_afterDelete_returns404() {
            int id = createRentalAndReturnId();
            rentalRepository.deleteById(id);

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GET /api/v1/rentals — findAll(Pageable)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/rentals — findAll(Pageable)")
    class FindAllPagedTests {

        @Test
        @DisplayName("200 with default page size 20 when no pagination parameters sent")
        void findAll_noParams_defaultPageSize20() {
            createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", notNullValue())
                    .body("size", equalTo(20))
                    .body("number", equalTo(0));
        }

        @Test
        @DisplayName("200 with explicit page and size parameters respected")
        void findAll_withExplicitPageAndSize_pageMetadataCorrect() {
            createRentalAndReturnId();

            given()
                    .queryParam("page", 0)
                    .queryParam("size", 5)
                    .when().get(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size", equalTo(5))
                    .body("number", equalTo(0));
        }

        @Test
        @DisplayName("200 with empty content array when requesting a page beyond available data")
        void findAll_pageWayBeyondData_returnsEmptyContent() {
            given()
                    .queryParam("page", 9999)
                    .queryParam("size", 10)
                    .when().get(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", empty());
        }

        @Test
        @DisplayName("200 when sort parameter is supplied (rentalDate,asc)")
        void findAll_withSortParam_returns200() {
            createRentalAndReturnId();

            given()
                    .queryParam("page", 0)
                    .queryParam("size", 5)
                    .queryParam("sort", "rentalDate,asc")
                    .when().get(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", notNullValue());
        }

        @Test
        @DisplayName("200 response includes totalElements and totalPages in page metadata")
        void findAll_responsePaginationMetadata_present() {
            createRentalAndReturnId();

            given()
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when().get(RENTALS_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", greaterThanOrEqualTo(1))
                    .body("totalPages", greaterThanOrEqualTo(1));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GET /api/v1/rentals/all — getAllRentals() (unpaged list)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/rentals/all — getAllRentals()")
    class GetAllRentalsTests {

        @Test
        @DisplayName("200 returns a JSON array with at least one element")
        void getAllRentals_afterCreation_returnsNonEmptyArray() {
            createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/all")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", instanceOf(java.util.List.class))
                    .body("size()", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("200 — each item in the list contains id, inventoryId, customerId, staffId")
        void getAllRentals_eachItemContainsMandatoryFields() {
            createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/all")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].id", notNullValue())
                    .body("[0].inventoryId", notNullValue())
                    .body("[0].customerId", notNullValue())
                    .body("[0].staffId", notNullValue());
        }

        @Test
        @DisplayName("200 — newly created rental appears in the unpaged list")
        void getAllRentals_newlyCreatedRentalAppearsInList() {
            int id = createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GET /api/v1/rentals/count — count()
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/rentals/count — count()")
    class CountTests {

        @Test
        @DisplayName("200 returns a non-negative integer")
        void count_returns200WithNonNegativeInteger() {
            createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/count")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("count increments by exactly 1 after creating a single rental")
        void count_incrementsByOneAfterCreate() {
            long before = given()
                    .when().get(RENTALS_PATH + "/count")
                    .then().statusCode(200)
                    .extract().as(Long.class);

            createRentalAndReturnId();

            long after = given()
                    .when().get(RENTALS_PATH + "/count")
                    .then().statusCode(200)
                    .extract().as(Long.class);

            org.assertj.core.api.Assertions.assertThat(after).isEqualTo(before + 1);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GET /api/v1/rentals/exists/{id} — existsById()
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/rentals/exists/{id} — existsById()")
    class ExistsByIdTests {

        @Test
        @DisplayName("200 true when rental with given id exists")
        void existsById_existingRental_returnsTrue() {
            int id = createRentalAndReturnId();

            given()
                    .when().get(RENTALS_PATH + "/exists/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(true));
        }

        @Test
        @DisplayName("200 false when rental with given id does not exist")
        void existsById_nonExistentId_returnsFalse() {
            given()
                    .when().get(RENTALS_PATH + "/exists/999999")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(false));
        }

        @Test
        @DisplayName("200 false after the rental is deleted")
        void existsById_afterDelete_returnsFalse() {
            int id = createRentalAndReturnId();

            rentalRepository.deleteById(id);

            given()
                    .when().get(RENTALS_PATH + "/exists/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(false));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PUT /api/v1/rentals/{id} — update() (full replace)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PUT /api/v1/rentals/{id} — update()")
    class FullUpdateTests {

        @Test
        @DisplayName("200 — all mutable fields are replaced with request payload values")
        void update_withValidPayload_updatesAllFields() {
            int id = createRentalAndReturnId();

            String body = """
                    {
                      "rentalDate": "2025-06-01T12:00:00Z",
                      "inventoryId": 2,
                      "customerId": 2,
                      "staffId": 2,
                      "lastUpdate": "2025-06-01T12:00:00Z"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().put(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id))
                    .body("inventoryId", equalTo(2))
                    .body("customerId", equalTo(2))
                    .body("staffId", equalTo(2));
        }

        @Test
        @DisplayName("200 — subsequent GET reflects the updated values")
        void update_subsequentGet_showsNewValues() {
            int id = createRentalAndReturnId();

            String body = """
                    {
                      "rentalDate": "2025-07-01T08:00:00Z",
                      "inventoryId": 3,
                      "customerId": 3,
                      "staffId": 1,
                      "lastUpdate": "2025-07-01T08:00:00Z"
                    }
                    """;

            given().contentType(ContentType.JSON).body(body)
                    .when().put(RENTALS_PATH + "/" + id)
                    .then().statusCode(200);

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("inventoryId", equalTo(3));
        }

        @Test
        @DisplayName("404 when the rental ID to update does not exist")
        void update_nonExistentId_returns404() {
            String body = """
                    {
                      "rentalDate": "%s",
                      "inventoryId": %d,
                      "customerId": %d,
                      "staffId": %d,
                      "lastUpdate": "%s"
                    }
                    """.formatted(nextRentalDate(), INVENTORY_ID, CUSTOMER_ID, STAFF_ID, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().put(RENTALS_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("400 when required field rentalDate is absent in PUT payload")
        void update_missingRequiredRentalDate_returns400() {
            int id = createRentalAndReturnId();

            String body = """
                    {
                      "inventoryId": %d,
                      "customerId": %d,
                      "staffId": %d,
                      "lastUpdate": "%s"
                    }
                    """.formatted(INVENTORY_ID, CUSTOMER_ID, STAFF_ID, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().put(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PATCH /api/v1/rentals/{id} — partialUpdate()
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PATCH /api/v1/rentals/{id} — partialUpdate()")
    class PartialUpdateTests {

        @Test
        @DisplayName("200 — patching returnDate marks the rental as returned")
        void partialUpdate_returnDateOnly_setsReturnDate() {
            int id = createRentalAndReturnId();

            String patch = """
                    {
                      "returnDate": "2025-01-15T10:00:00Z"
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(patch)
                    .when().patch(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id))
                    .body("returnDate", notNullValue());
        }

        @Test
        @DisplayName("200 — PATCH with empty body does not corrupt existing rental data")
        void partialUpdate_emptyBody_doesNotCorruptExistingData() {
            int id = createRentalAndReturnId();

            given()
                    .contentType(ContentType.JSON)
                    .body("{}")
                    .when().patch(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id));
        }

        @Test
        @DisplayName("404 when rental ID to patch does not exist")
        void partialUpdate_nonExistentId_returns404() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"inventoryId\": 5}")
                    .when().patch(RENTALS_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DELETE /api/v1/rentals/{id} — delete()
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DELETE /api/v1/rentals/{id} — delete()")
    class DeleteRentalTests {

        @Test
        @DisplayName("204 No Content when rental exists and is successfully deleted")
        void delete_existingRental_returns204() {
            int id = createRentalAndReturnId();

            given()
                    .when().delete(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("404 on subsequent GET after successful delete (data actually removed)")
        void delete_subsequentGet_returns404() {
            int id = createRentalAndReturnId();

            given().when().delete(RENTALS_PATH + "/" + id).then().statusCode(204);

            given()
                    .when().get(RENTALS_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("404 when trying to delete a non-existent rental ID")
        void delete_nonExistentId_returns404() {
            given()
                    .when().delete(RENTALS_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("404 on second delete attempt — not idempotent (resource is gone)")
        void delete_secondAttemptOnSameId_returns404() {
            int id = createRentalAndReturnId();

            given().when().delete(RENTALS_PATH + "/" + id).then().statusCode(204);
            given().when().delete(RENTALS_PATH + "/" + id).then().statusCode(404);
        }

        @Test
        @DisplayName("existsById returns false after successful deletion")
        void delete_existsByIdReturnsFalseAfterDelete() {
            int id = createRentalAndReturnId();

            given().when().delete(RENTALS_PATH + "/" + id).then().statusCode(204);

            given()
                    .when().get(RENTALS_PATH + "/exists/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(false));
        }
    }
}


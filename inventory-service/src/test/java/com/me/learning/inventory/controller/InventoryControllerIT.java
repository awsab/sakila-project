package com.me.learning.inventory.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

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

import com.me.learning.inventory.entity.Store;
import com.me.learning.inventory.repository.StoreRepository;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:inventoryit;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("InventoryController - REST-Assured Integration Tests")
@SuppressWarnings({"PMD.MethodNamingConventions", "PMD.UnitTestShouldIncludeAssert"})
class InventoryControllerIT {

    private static final String INVENTORIES_PATH = "/api/v1/inventories";
    private static final String LAST_UPDATE = "2025-01-01T00:00:00Z";

    @LocalServerPort
    private int port;

    @Autowired
    private StoreRepository storeRepository;


    private short seededStoreId;

    @BeforeAll
    void seedStore() {
        Store store = new Store();
        store.setManagerStaffId((short) 1);
        store.setAddressId(10);
        store.setLastUpdate(Instant.parse("2024-01-01T00:00:00Z"));
        store = storeRepository.save(store);
        seededStoreId = store.getId();
    }

    @BeforeEach
    void configureRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private String buildCreateJson(int filmId) {
        return """
                {
                  "filmId": %d,
                  "store": { "id": %d },
                  "lastUpdate": "%s"
                }
                """.formatted(filmId, seededStoreId, LAST_UPDATE);
    }

    private int createInventoryAndReturnId(int filmId) {
        return given()
                .contentType(ContentType.JSON)
                .body(buildCreateJson(filmId))
                .when().post(INVENTORIES_PATH)
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().path("id");
    }

    @Nested
    @DisplayName("POST /api/v1/inventories - create()")
    class CreateTests {

        @Test
        @DisplayName("201 with valid payload")
        void create_withValidPayload_returns201() {
            given()
                    .contentType(ContentType.JSON)
                    .body(buildCreateJson(100))
                    .when().post(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .contentType(ContentType.JSON)
                    .body("id", notNullValue())
                    .body("filmId", equalTo(100))
                    .body("storeId", equalTo((int) seededStoreId));
        }

        @Test
        @DisplayName("201 ignores id in payload")
        void create_withIdInPayload_ignoresClientId() {
            String body = """
                    {
                      "id": 99999,
                      "filmId": 101,
                      "store": { "id": %d },
                      "lastUpdate": "%s"
                    }
                    """.formatted(seededStoreId, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", not(equalTo(99_999)));
        }

        @Test
        @DisplayName("400 when filmId is missing")
        void create_missingFilmId_returns400() {
            String body = """
                    {
                      "store": { "id": %d },
                      "lastUpdate": "%s"
                    }
                    """.formatted(seededStoreId, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("404 when referenced store does not exist")
        void create_nonExistentStore_returns404() {
            String body = """
                    {
                      "filmId": 105,
                      "store": { "id": 99 },
                      "lastUpdate": "%s"
                    }
                    """.formatted(LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().post(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("400 when Content-Type is missing")
        void create_missingContentType_returnsClientError() {
            given()
                    .body(buildCreateJson(106))
                    .when().post(INVENTORIES_PATH)
                    .then()
                    .statusCode(anyOf(
                            equalTo(HttpStatus.BAD_REQUEST.value()),
                            equalTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
                            equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventories/{id} - findById()")
    class FindByIdTests {

        @Test
        @DisplayName("200 when inventory exists")
        void findById_existingId_returns200() {
            int id = createInventoryAndReturnId(110);

            given()
                    .when().get(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id))
                    .body("filmId", equalTo(110))
                    .body("storeId", equalTo((int) seededStoreId));
        }

        @Test
        @DisplayName("404 when inventory does not exist")
        void findById_nonExistentId_returns404() {
            given()
                    .when().get(INVENTORIES_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventories - findAll(Pageable)")
    class FindAllPagedTests {

        @Test
        @DisplayName("200 with default page size 20")
        void findAll_defaultPageSize() {
            createInventoryAndReturnId(120);

            given()
                    .when().get(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", notNullValue())
                    .body("size", equalTo(20))
                    .body("number", equalTo(0));
        }

        @Test
        @DisplayName("200 with explicit page/size")
        void findAll_withPageAndSize_returns200() {
            createInventoryAndReturnId(121);

            given()
                    .queryParam("page", 0)
                    .queryParam("size", 5)
                    .when().get(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size", equalTo(5))
                    .body("number", equalTo(0));
        }

        @Test
        @DisplayName("200 with empty content for high page index")
        void findAll_pageBeyondData_returnsEmptyContent() {
            given()
                    .queryParam("page", 9999)
                    .queryParam("size", 10)
                    .when().get(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", empty());
        }

        @Test
        @DisplayName("200 includes pagination metadata")
        void findAll_includesPaginationMetadata() {
            createInventoryAndReturnId(122);

            given()
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when().get(INVENTORIES_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalElements", greaterThanOrEqualTo(1))
                    .body("totalPages", greaterThanOrEqualTo(1));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventories/all - getAllInventories()")
    class FindAllUnpagedTests {

        @Test
        @DisplayName("200 returns a JSON array")
        void getAll_returnsArray() {
            createInventoryAndReturnId(130);

            given()
                    .when().get(INVENTORIES_PATH + "/all")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", instanceOf(java.util.List.class))
                    .body("size()", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("200 each element contains id, filmId and storeId")
        void getAll_containsMandatoryFields() {
            createInventoryAndReturnId(131);

            given()
                    .when().get(INVENTORIES_PATH + "/all")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("[0].id", notNullValue())
                    .body("[0].filmId", notNullValue())
                    .body("[0].storeId", notNullValue());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventories/count - count()")
    class CountTests {

        @Test
        @DisplayName("200 returns non-negative integer")
        void count_returnsNonNegativeInteger() {
            createInventoryAndReturnId(140);

            given()
                    .when().get(INVENTORIES_PATH + "/count")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("count increments by 1 after create")
        void count_incrementsByOneAfterCreate() {
            long before = given()
                    .when().get(INVENTORIES_PATH + "/count")
                    .then().statusCode(200)
                    .extract().as(Long.class);

            createInventoryAndReturnId(141);

            long after = given()
                    .when().get(INVENTORIES_PATH + "/count")
                    .then().statusCode(200)
                    .extract().as(Long.class);

            org.assertj.core.api.Assertions.assertThat(after).isEqualTo(before + 1);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventories/exists/{id} - existsById()")
    class ExistsByIdTests {

        @Test
        @DisplayName("200 true when inventory exists")
        void exists_existing_returnsTrue() {
            int id = createInventoryAndReturnId(150);

            given()
                    .when().get(INVENTORIES_PATH + "/exists/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(true));
        }

        @Test
        @DisplayName("200 false when inventory does not exist")
        void exists_missing_returnsFalse() {
            given()
                    .when().get(INVENTORIES_PATH + "/exists/999999")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(false));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/inventories/{id} - update()")
    class UpdateTests {

        @Test
        @DisplayName("200 replaces mutable fields")
        void update_withValidPayload_returns200() {
            int id = createInventoryAndReturnId(160);

            String body = """
                    {
                      "filmId": 999,
                      "store": { "id": %d },
                      "lastUpdate": "%s"
                    }
                    """.formatted(seededStoreId, LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().put(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(id))
                    .body("filmId", equalTo(999))
                    .body("storeId", equalTo((int) seededStoreId));
        }

        @Test
        @DisplayName("404 when inventory id does not exist")
        void update_nonExistentId_returns404() {
            given()
                    .contentType(ContentType.JSON)
                    .body(buildCreateJson(161))
                    .when().put(INVENTORIES_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("404 when referenced store does not exist")
        void update_nonExistentStore_returns404() {
            int id = createInventoryAndReturnId(162);

            String body = """
                    {
                      "filmId": 162,
                      "store": { "id": 99 },
                      "lastUpdate": "%s"
                    }
                    """.formatted(LAST_UPDATE);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when().put(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/inventories/{id} - partialUpdate()")
    class PatchTests {

        @Test
        @DisplayName("200 patch updates filmId")
        void patch_updatesFilmId() {
            int id = createInventoryAndReturnId(170);

            String patch = """
                    {
                      "filmId": 171
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(patch)
                    .when().patch(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("filmId", equalTo(171));
        }

        @Test
        @DisplayName("404 patch with non-existent id")
        void patch_nonExistentId_returns404() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"filmId\": 172}")
                    .when().patch(INVENTORIES_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("404 patch with non-existent store id")
        void patch_nonExistentStore_returns404() {
            int id = createInventoryAndReturnId(173);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"store\": {\"id\": 99}}")
                    .when().patch(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/inventories/{id} - delete()")
    class DeleteTests {

        @Test
        @DisplayName("204 when inventory exists")
        void delete_existingInventory_returns204() {
            int id = createInventoryAndReturnId(180);

            given()
                    .when().delete(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("404 on get after delete")
        void delete_thenGet_returns404() {
            int id = createInventoryAndReturnId(181);

            given().when().delete(INVENTORIES_PATH + "/" + id).then().statusCode(204);

            given()
                    .when().get(INVENTORIES_PATH + "/" + id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("404 when deleting non-existent id")
        void delete_nonExistentId_returns404() {
            given()
                    .when().delete(INVENTORIES_PATH + "/999999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("exists endpoint returns false after delete")
        void delete_existsReturnsFalseAfterDelete() {
            int id = createInventoryAndReturnId(182);

            given().when().delete(INVENTORIES_PATH + "/" + id).then().statusCode(204);

            given()
                    .when().get(INVENTORIES_PATH + "/exists/" + id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", equalTo(false));
        }
    }
}


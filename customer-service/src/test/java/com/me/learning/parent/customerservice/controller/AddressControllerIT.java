package com.me.learning.parent.customerservice.controller;

import java.time.Instant;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.AddressRequest;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.AddressUpdateRequest;
import com.me.learning.parent.customerservice.service.AddressService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link AddressController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("AddressController Integration Tests")
class AddressControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private AddressService addressService;

    private static final String BASE_URL = "/api/v1/addresses";

    private AddressResponse sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = new AddressResponse(
                1, "47 MySakila Drive", null, "Alberta", 300, "Lethbridge",
                "", "403-555-1212", Instant.parse("2026-01-01T00:00:00Z"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/addresses
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/addresses — createAddress")
    class CreateAddress {

        @Test
        @DisplayName("should return 201 and the created address")
        void createAddress_returnsCreated() {
            when(addressService.createAddress(any(AddressRequest.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"47 MySakila Drive\",\"district\":\"Alberta\","
                            + "\"cityId\":300,\"phone\":\"403-555-1212\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("id",      equalTo(1))
                    .body("address", equalTo("47 MySakila Drive"));
        }

        @Test
        @DisplayName("should return 400 when address is blank")
        void createAddress_blankAddress_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"\",\"district\":\"Alberta\","
                            + "\"cityId\":300,\"phone\":\"403-555-1212\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when district is blank")
        void createAddress_blankDistrict_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"47 MySakila Drive\",\"district\":\"\","
                            + "\"cityId\":300,\"phone\":\"403-555-1212\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when phone is blank")
        void createAddress_blankPhone_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"47 MySakila Drive\",\"district\":\"Alberta\","
                            + "\"cityId\":300,\"phone\":\"\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 404 when city not found")
        void createAddress_cityNotFound_returns404() {
            when(addressService.createAddress(any(AddressRequest.class)))
                    .thenThrow(new ResourceNotFoundException("City not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"47 MySakila Drive\",\"district\":\"Alberta\","
                            + "\"cityId\":9999,\"phone\":\"403-555-1212\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/addresses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/addresses/{id} — updateAddress")
    class UpdateAddress {

        @Test
        @DisplayName("should return 200 and the updated address")
        void updateAddress_returnsOk() {
            AddressResponse updated = new AddressResponse(
                    1, "28 MySQL Boulevard", null, "QLD", 576, "Woodridge",
                    "4114", "14-22716666", null);
            when(addressService.updateAddress(eq(1), any(AddressUpdateRequest.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"28 MySQL Boulevard\",\"district\":\"QLD\","
                            + "\"cityId\":576,\"phone\":\"14-22716666\"}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("address", equalTo("28 MySQL Boulevard"));
        }

        @Test
        @DisplayName("should return 404 when address not found")
        void updateAddress_notFound_returns404() {
            when(addressService.updateAddress(eq(99), any(AddressUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Address not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"address\":\"28 MySQL Boulevard\",\"district\":\"QLD\","
                            + "\"cityId\":576,\"phone\":\"14-22716666\"}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/addresses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/addresses/{id} — patchAddress")
    class PatchAddress {

        @Test
        @DisplayName("should return 200 and the patched address")
        void patchAddress_returnsOk() {
            AddressResponse patched = new AddressResponse(
                    1, "47 MySakila Drive", null, "New District", 300, "Lethbridge",
                    "", "403-555-9999", null);
            when(addressService.patchAddress(eq(1), any(AddressUpdateRequest.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"district\":\"New District\",\"phone\":\"403-555-9999\"}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("district", equalTo("New District"));
        }

        @Test
        @DisplayName("should return 404 when address not found")
        void patchAddress_notFound_returns404() {
            when(addressService.patchAddress(eq(99), any(AddressUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Address not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"district\":\"New District\"}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/addresses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/addresses/{id} — getAddressById")
    class GetAddressById {

        @Test
        @DisplayName("should return 200 and the address")
        void getAddressById_found_returnsOk() {
            when(addressService.getAddressById(1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("id",      equalTo(1))
                    .body("address", equalTo("47 MySakila Drive"));
        }

        @Test
        @DisplayName("should return 404 when address not found")
        void getAddressById_notFound_returns404() {
            when(addressService.getAddressById(99))
                    .thenThrow(new ResourceNotFoundException("Address not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/addresses  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/addresses — getAllAddresses (paged)")
    class GetAllAddressesPaged {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllAddresses_returnsPage() {
            when(addressService.getAllAddresses(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content",            hasSize(1))
                    .body("content[0].address", equalTo("47 MySakila Drive"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllAddresses_empty_returnsEmptyPage() {
            when(addressService.getAllAddresses(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/addresses/by-city?cityId=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/addresses/by-city — getAddressesByCity")
    class GetAddressesByCity {

        @Test
        @DisplayName("should return 200 with matching addresses")
        void getAddressesByCity_returnsResults() {
            when(addressService.getAddressesByCityId(300)).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("cityId", 300)
            .when()
                    .get(BASE_URL + "/by-city")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].address", equalTo("47 MySakila Drive"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no addresses for city")
        void getAddressesByCity_noMatch_returnsEmpty() {
            when(addressService.getAddressesByCityId(9999)).thenReturn(List.of());

            given()
                    .queryParam("cityId", 9999)
            .when()
                    .get(BASE_URL + "/by-city")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/addresses/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/addresses/{id} — deleteAddress")
    class DeleteAddress {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteAddress_returnsNoContent() {
            doNothing().when(addressService).deleteAddress(1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when address not found")
        void deleteAddress_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Address not found"))
                    .when(addressService).deleteAddress(99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/addresses/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/addresses/count — countAddresses")
    class CountAddresses {

        @Test
        @DisplayName("should return 200 with the total count")
        void countAddresses_returnsCount() {
            when(addressService.countAddresses()).thenReturn(603L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("603"));
        }
    }
}


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
import com.me.learning.parent.customerservice.dto.CityRequest;
import com.me.learning.parent.customerservice.dto.CityResponse;
import com.me.learning.parent.customerservice.dto.CityUpdateRequest;
import com.me.learning.parent.customerservice.service.CityService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link CityController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("CityController Integration Tests")
class CityControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private CityService cityService;

    private static final String BASE_URL = "/api/v1/cities";

    private CityResponse sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = new CityResponse(1, "New York", 103, "United States",
                Instant.parse("2026-01-01T00:00:00Z"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/cities
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/cities — createCity")
    class CreateCity {

        @Test
        @DisplayName("should return 201 and the created city")
        void createCity_returnsCreated() {
            when(cityService.createCity(any(CityRequest.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"New York\",\"countryId\":103}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("id",   equalTo(1))
                    .body("city", equalTo("New York"));
        }

        @Test
        @DisplayName("should return 400 when city name is blank")
        void createCity_blankName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"\",\"countryId\":103}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when countryId is missing")
        void createCity_missingCountryId_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"New York\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when duplicate city is submitted")
        void createCity_duplicate_returns400() {
            when(cityService.createCity(any(CityRequest.class)))
                    .thenThrow(new IllegalArgumentException("City already exists"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"New York\",\"countryId\":103}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/cities/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/cities/{id} — updateCity")
    class UpdateCity {

        @Test
        @DisplayName("should return 200 and the updated city")
        void updateCity_returnsOk() {
            CityResponse updated = new CityResponse(1, "Los Angeles", 103, "United States", null);
            when(cityService.updateCity(eq(1), any(CityUpdateRequest.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"Los Angeles\",\"countryId\":103}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("city", equalTo("Los Angeles"));
        }

        @Test
        @DisplayName("should return 404 when city not found")
        void updateCity_notFound_returns404() {
            when(cityService.updateCity(eq(99), any(CityUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("City not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"Los Angeles\",\"countryId\":103}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/cities/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/cities/{id} — patchCity")
    class PatchCity {

        @Test
        @DisplayName("should return 200 and the patched city")
        void patchCity_returnsOk() {
            CityResponse patched = new CityResponse(1, "Chicago", 103, "United States", null);
            when(cityService.patchCity(eq(1), any(CityUpdateRequest.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"Chicago\",\"countryId\":103}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("city", equalTo("Chicago"));
        }

        @Test
        @DisplayName("should return 404 when city not found")
        void patchCity_notFound_returns404() {
            when(cityService.patchCity(eq(99), any(CityUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("City not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"city\":\"Chicago\",\"countryId\":103}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/cities/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/cities/{id} — getCityById")
    class GetCityById {

        @Test
        @DisplayName("should return 200 and the city")
        void getCityById_found_returnsOk() {
            when(cityService.getCityById(1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("id",   equalTo(1))
                    .body("city", equalTo("New York"));
        }

        @Test
        @DisplayName("should return 404 when city not found")
        void getCityById_notFound_returns404() {
            when(cityService.getCityById(99))
                    .thenThrow(new ResourceNotFoundException("City not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/cities  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/cities — getAllCities (paged)")
    class GetAllCitiesPaged {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllCities_returnsPage() {
            when(cityService.getAllCities(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content",         hasSize(1))
                    .body("content[0].city", equalTo("New York"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllCities_empty_returnsEmptyPage() {
            when(cityService.getAllCities(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/cities/by-country?countryId=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/cities/by-country — getCitiesByCountry")
    class GetCitiesByCountry {

        @Test
        @DisplayName("should return 200 with matching cities")
        void getCitiesByCountry_returnsResults() {
            when(cityService.getCitiesByCountryId(103)).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("countryId", 103)
            .when()
                    .get(BASE_URL + "/by-country")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].city", equalTo("New York"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no cities for country")
        void getCitiesByCountry_noMatch_returnsEmpty() {
            when(cityService.getCitiesByCountryId(999)).thenReturn(List.of());

            given()
                    .queryParam("countryId", 999)
            .when()
                    .get(BASE_URL + "/by-country")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/cities/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/cities/{id} — deleteCity")
    class DeleteCity {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteCity_returnsNoContent() {
            doNothing().when(cityService).deleteCity(1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when city not found")
        void deleteCity_notFound_returns404() {
            doThrow(new ResourceNotFoundException("City not found"))
                    .when(cityService).deleteCity(99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/cities/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/cities/count — countCities")
    class CountCities {

        @Test
        @DisplayName("should return 200 with the total count")
        void countCities_returnsCount() {
            when(cityService.countCities()).thenReturn(600L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("600"));
        }
    }
}


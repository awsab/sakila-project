package com.me.learning.parent.customerservice.controller;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.customerservice.dto.CountryRequest;
import com.me.learning.parent.customerservice.dto.CountryResponse;
import com.me.learning.parent.customerservice.dto.CountryUpdateRequest;
import com.me.learning.parent.customerservice.service.CountryService;
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
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

/**
 * Integration tests for {@link CountryController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("CountryController Integration Tests")
class CountryControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private CountryService countryService;

    private static final String BASE_URL = "/api/v1/countries";

    private CountryResponse sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = new CountryResponse(1, "United States", Instant.parse("2026-01-01T00:00:00Z"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/countries
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/countries — createCountry")
    class CreateCountry {

        @Test
        @DisplayName("should return 201 and the created country")
        void createCountry_returnsCreated() {
            when(countryService.createCountry(any(CountryRequest.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"United States\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("id",      equalTo(1))
                    .body("country", equalTo("United States"));
        }

        @Test
        @DisplayName("should return 400 when country name is blank")
        void createCountry_blankName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when duplicate country is submitted")
        void createCountry_duplicate_returns400() {
            when(countryService.createCountry(any(CountryRequest.class)))
                    .thenThrow(new IllegalArgumentException("Country already exists"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"United States\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/countries/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/countries/{id} — updateCountry")
    class UpdateCountry {

        @Test
        @DisplayName("should return 200 and the updated country")
        void updateCountry_returnsOk() {
            CountryResponse updated = new CountryResponse(1, "Canada", null);
            when(countryService.updateCountry(eq(1), any(CountryUpdateRequest.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"Canada\"}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("country", equalTo("Canada"));
        }

        @Test
        @DisplayName("should return 404 when country not found")
        void updateCountry_notFound_returns404() {
            when(countryService.updateCountry(eq(99), any(CountryUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Country not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"Canada\"}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/countries/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/countries/{id} — patchCountry")
    class PatchCountry {

        @Test
        @DisplayName("should return 200 and the patched country")
        void patchCountry_returnsOk() {
            CountryResponse patched = new CountryResponse(1, "Mexico", null);
            when(countryService.patchCountry(eq(1), any(CountryUpdateRequest.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"Mexico\"}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("country", equalTo("Mexico"));
        }

        @Test
        @DisplayName("should return 404 when country not found")
        void patchCountry_notFound_returns404() {
            when(countryService.patchCountry(eq(99), any(CountryUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Country not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"country\":\"Mexico\"}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/countries/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/countries/{id} — getCountryById")
    class GetCountryById {

        @Test
        @DisplayName("should return 200 and the country")
        void getCountryById_found_returnsOk() {
            when(countryService.getCountryById(1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("id",      equalTo(1))
                    .body("country", equalTo("United States"));
        }

        @Test
        @DisplayName("should return 404 when country not found")
        void getCountryById_notFound_returns404() {
            when(countryService.getCountryById(99))
                    .thenThrow(new ResourceNotFoundException("Country not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/countries  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/countries — getAllCountries (paged)")
    class GetAllCountriesPaged {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllCountries_returnsPage() {
            when(countryService.getAllCountries(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content",          hasSize(1))
                    .body("content[0].country", equalTo("United States"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllCountries_empty_returnsEmptyPage() {
            when(countryService.getAllCountries(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/countries/all
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/countries/all — getAllCountriesList")
    class GetAllCountriesList {

        @Test
        @DisplayName("should return 200 with list of all countries")
        void getAllCountriesList_returnsList() {
            CountryResponse second = new CountryResponse(2, "Canada", null);
            when(countryService.getAllCountries()).thenReturn(List.of(sampleResponse, second));

            given()
            .when()
                    .get(BASE_URL + "/all")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("[0].country", equalTo("United States"))
                    .body("[1].country", equalTo("Canada"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no countries")
        void getAllCountriesList_empty_returnsEmpty() {
            when(countryService.getAllCountries()).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/all")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/countries/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/countries/{id} — deleteCountry")
    class DeleteCountry {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteCountry_returnsNoContent() {
            doNothing().when(countryService).deleteCountry(1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when country not found")
        void deleteCountry_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Country not found"))
                    .when(countryService).deleteCountry(99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/countries/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/countries/count — countCountries")
    class CountCountries {

        @Test
        @DisplayName("should return 200 with the total count")
        void countCountries_returnsCount() {
            when(countryService.countCountries()).thenReturn(195L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("195"));
        }
    }
}


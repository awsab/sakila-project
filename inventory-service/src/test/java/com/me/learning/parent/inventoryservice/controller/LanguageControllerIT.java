package com.me.learning.parent.inventoryservice.controller;

import java.time.Instant;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.inventoryservice.dto.request.LanguageRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.LanguageResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.LanguageUpdateDTO;
import com.me.learning.parent.inventoryservice.service.LanguageService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link LanguageController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("LanguageController Integration Tests")
class LanguageControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private LanguageService languageService;

    private static final String BASE_URL = "/api/v1/languages";

    private LanguageResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = LanguageResponseDTO.builder()
                .id(1)
                .name("English")
                .lastUpdate(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/languages
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/languages — createLanguage")
    class CreateLanguage {

        @Test
        @DisplayName("should return 201 and the created language")
        void createLanguage_returnsCreated() {
            when(languageService.createLanguage(any(LanguageRequestDTO.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"English\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("languageId", equalTo(1))
                    .body("name",       equalTo("English"));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void createLanguage_blankName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when duplicate language is submitted")
        void createLanguage_duplicate_returns400() {
            when(languageService.createLanguage(any(LanguageRequestDTO.class)))
                    .thenThrow(new IllegalArgumentException("Language already exists"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"English\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/languages/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/languages/{id} — updateLanguage")
    class UpdateLanguage {

        @Test
        @DisplayName("should return 200 and the updated language")
        void updateLanguage_returnsOk() {
            LanguageResponseDTO updated = LanguageResponseDTO.builder().id(1).name("French").build();
            when(languageService.updateLanguage(eq((short) 1), any(LanguageUpdateDTO.class)))
                    .thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"languageId\":1,\"name\":\"French\"}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("name", equalTo("French"));
        }

        @Test
        @DisplayName("should return 404 when language not found")
        void updateLanguage_notFound_returns404() {
            when(languageService.updateLanguage(eq((short) 99), any(LanguageUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Language not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"languageId\":99,\"name\":\"French\"}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/languages/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/languages/{id} — patchLanguage")
    class PatchLanguage {

        @Test
        @DisplayName("should return 200 and the patched language")
        void patchLanguage_returnsOk() {
            LanguageResponseDTO patched = LanguageResponseDTO.builder().id(1).name("German").build();
            when(languageService.patchLanguage(eq((short) 1), any(LanguageUpdateDTO.class)))
                    .thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"languageId\":1,\"name\":\"German\"}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("name", equalTo("German"));
        }

        @Test
        @DisplayName("should return 404 when language not found")
        void patchLanguage_notFound_returns404() {
            when(languageService.patchLanguage(eq((short) 99), any(LanguageUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Language not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"languageId\":99,\"name\":\"German\"}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/{id} — getLanguageById")
    class GetLanguageById {

        @Test
        @DisplayName("should return 200 and the language")
        void getLanguageById_found_returnsOk() {
            when(languageService.getLanguageById((short) 1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("languageId", equalTo(1))
                    .body("name",       equalTo("English"));
        }

        @Test
        @DisplayName("should return 404 when language not found")
        void getLanguageById_notFound_returns404() {
            when(languageService.getLanguageById((short) 99))
                    .thenThrow(new ResourceNotFoundException("Language not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/name/{name}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/name/{name} — getLanguageByName")
    class GetLanguageByName {

        @Test
        @DisplayName("should return 200 and the language")
        void getLanguageByName_found_returnsOk() {
            when(languageService.getLanguageByName("English")).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/name/English")
            .then()
                    .statusCode(200)
                    .body("name", equalTo("English"));
        }

        @Test
        @DisplayName("should return 404 when language not found by name")
        void getLanguageByName_notFound_returns404() {
            when(languageService.getLanguageByName("Klingon"))
                    .thenThrow(new ResourceNotFoundException("Language not found"));

            given()
            .when()
                    .get(BASE_URL + "/name/Klingon")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages — getAllLanguages")
    class GetAllLanguages {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllLanguages_returnsPage() {
            when(languageService.getAllLanguages(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(1))
                    .body("content[0].name", equalTo("English"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllLanguages_empty_returnsEmptyPage() {
            when(languageService.getAllLanguages(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/search?searchTerm=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/search — searchLanguages")
    class SearchLanguages {

        @Test
        @DisplayName("should return 200 with matching languages")
        void searchLanguages_returnsResults() {
            when(languageService.searchLanguagesByName("Eng")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("searchTerm", "Eng")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].name", equalTo("English"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no match")
        void searchLanguages_noMatch_returnsEmpty() {
            when(languageService.searchLanguagesByName("ZZZ")).thenReturn(List.of());

            given()
                    .queryParam("searchTerm", "ZZZ")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/sorted
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/sorted — getAllLanguagesSorted")
    class GetAllLanguagesSorted {

        @Test
        @DisplayName("should return 200 with sorted languages")
        void getAllLanguagesSorted_returnsResults() {
            LanguageResponseDTO italian = LanguageResponseDTO.builder().id(2).name("Italian").build();
            when(languageService.getAllLanguagesSortedByName())
                    .thenReturn(List.of(sampleResponse, italian));

            given()
            .when()
                    .get(BASE_URL + "/sorted")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("[0].name", equalTo("English"))
                    .body("[1].name", equalTo("Italian"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no languages")
        void getAllLanguagesSorted_empty_returnsEmpty() {
            when(languageService.getAllLanguagesSortedByName()).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/sorted")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/languages/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/languages/{id} — deleteLanguage")
    class DeleteLanguage {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteLanguage_returnsNoContent() {
            doNothing().when(languageService).deleteLanguage((short) 1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when language not found")
        void deleteLanguage_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Language not found"))
                    .when(languageService).deleteLanguage((short) 99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/count — countLanguages")
    class CountLanguages {

        @Test
        @DisplayName("should return 200 with the total count")
        void countLanguages_returnsCount() {
            when(languageService.countLanguages()).thenReturn(6L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("6"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/exists/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/exists/{id} — existsById")
    class ExistsById {

        @Test
        @DisplayName("should return 200 true when language exists")
        void existsById_exists_returnsTrue() {
            when(languageService.existsById((short) 1)).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/1")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when language does not exist")
        void existsById_notExists_returnsFalse() {
            when(languageService.existsById((short) 99)).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/99")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/languages/exists/name/{name}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/languages/exists/name/{name} — existsByName")
    class ExistsByName {

        @Test
        @DisplayName("should return 200 true when language name exists")
        void existsByName_exists_returnsTrue() {
            when(languageService.existsByName("English")).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/name/English")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when language name does not exist")
        void existsByName_notExists_returnsFalse() {
            when(languageService.existsByName("Klingon")).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/name/Klingon")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }
}


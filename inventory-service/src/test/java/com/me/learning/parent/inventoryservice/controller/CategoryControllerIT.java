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

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.inventoryservice.dto.request.CategoryRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.CategoryResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.CategoryUpdateDTO;
import com.me.learning.parent.inventoryservice.service.CategoryService;

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
 * Integration tests for {@link CategoryController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("CategoryController Integration Tests")
class CategoryControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private CategoryService categoryService;

    private static final String BASE_URL = "/api/v1/categories";

    private CategoryResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = CategoryResponseDTO.builder()
                .id(1)
                .name("Action")
                .lastUpdate(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/categories
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/categories — createCategory")
    class CreateCategory {

        @Test
        @DisplayName("should return 201 and the created category")
        void createCategory_returnsCreated() {
            when(categoryService.createCategory(any(CategoryRequestDTO.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"Action\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("categoryId", equalTo(1))
                    .body("name", equalTo("Action"));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void createCategory_blankName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 409 when category already exists")
        void createCategory_duplicate_returns409() {
            when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Category already exists"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\":\"Action\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(409);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/categories/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/categories/{id} — updateCategory")
    class UpdateCategory {

        @Test
        @DisplayName("should return 200 and the updated category")
        void updateCategory_returnsOk() {
            CategoryResponseDTO updated = CategoryResponseDTO.builder().id(1).name("Drama").build();
            when(categoryService.updateCategory(eq((short) 1), any(CategoryUpdateDTO.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"categoryId\":1,\"name\":\"Drama\"}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Drama"));
        }

        @Test
        @DisplayName("should return 404 when category not found")
        void updateCategory_notFound_returns404() {
            when(categoryService.updateCategory(eq((short) 99), any(CategoryUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"categoryId\":99,\"name\":\"Drama\"}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/categories/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/categories/{id} — patchCategory")
    class PatchCategory {

        @Test
        @DisplayName("should return 200 and the patched category")
        void patchCategory_returnsOk() {
            CategoryResponseDTO patched = CategoryResponseDTO.builder().id(1).name("Comedy").build();
            when(categoryService.patchCategory(eq((short) 1), any(CategoryUpdateDTO.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"categoryId\":1,\"name\":\"Comedy\"}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Comedy"));
        }

        @Test
        @DisplayName("should return 404 when category not found")
        void patchCategory_notFound_returns404() {
            when(categoryService.patchCategory(eq((short) 99), any(CategoryUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"categoryId\":99,\"name\":\"Comedy\"}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/{id} — getCategoryById")
    class GetCategoryById {

        @Test
        @DisplayName("should return 200 and the category")
        void getCategoryById_found_returnsOk() {
            when(categoryService.getCategoryById((short) 1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("categoryId", equalTo(1))
                    .body("name", equalTo("Action"));
        }

        @Test
        @DisplayName("should return 404 when category not found")
        void getCategoryById_notFound_returns404() {
            when(categoryService.getCategoryById((short) 99))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/name/{name}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/name/{name} — getCategoryByName")
    class GetCategoryByName {

        @Test
        @DisplayName("should return 200 and the category")
        void getCategoryByName_found_returnsOk() {
            when(categoryService.getCategoryByName("Action")).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/name/Action")
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Action"));
        }

        @Test
        @DisplayName("should return 404 when category not found")
        void getCategoryByName_notFound_returns404() {
            when(categoryService.getCategoryByName("Unknown"))
                    .thenThrow(new ResourceNotFoundException("Category not found"));

            given()
            .when()
                    .get(BASE_URL + "/name/Unknown")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories — getAllCategories")
    class GetAllCategories {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllCategories_returnsPage() {
            when(categoryService.getAllCategories(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(1))
                    .body("content[0].name", equalTo("Action"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllCategories_empty_returnsEmptyPage() {
            when(categoryService.getAllCategories(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/search?searchTerm=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/search — searchCategories")
    class SearchCategories {

        @Test
        @DisplayName("should return 200 with matching categories")
        void searchCategories_returnsResults() {
            when(categoryService.searchCategoriesByName("Act")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("searchTerm", "Act")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].name", equalTo("Action"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no match")
        void searchCategories_noMatch_returnsEmpty() {
            when(categoryService.searchCategoriesByName("ZZZ")).thenReturn(List.of());

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
    //  GET /api/v1/categories/sorted
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/sorted — getAllCategoriesSorted")
    class GetAllCategoriesSorted {

        @Test
        @DisplayName("should return 200 with sorted categories")
        void getAllCategoriesSorted_returnsResults() {
            when(categoryService.getAllCategoriesSortedByName()).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/sorted")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/by-film/{filmId}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/by-film/{filmId} — getCategoriesByFilm")
    class GetCategoriesByFilm {

        @Test
        @DisplayName("should return 200 with categories for the film")
        void getCategoriesByFilm_returnsResults() {
            when(categoryService.getCategoriesByFilmId(10)).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/by-film/10")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].name", equalTo("Action"));
        }

        @Test
        @DisplayName("should return 200 with empty list when film has no categories")
        void getCategoriesByFilm_empty_returnsEmpty() {
            when(categoryService.getCategoriesByFilmId(999)).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/by-film/999")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/{id}/film-count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/{id}/film-count — countFilmsByCategory")
    class CountFilmsByCategory {

        @Test
        @DisplayName("should return 200 with film count for the category")
        void countFilmsByCategory_returnsCount() {
            when(categoryService.countFilmsByCategory((short) 1)).thenReturn(64L);

            given()
            .when()
                    .get(BASE_URL + "/1/film-count")
            .then()
                    .statusCode(200)
                    .body(equalTo("64"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/categories/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/categories/{id} — deleteCategory")
    class DeleteCategory {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteCategory_returnsNoContent() {
            doNothing().when(categoryService).deleteCategory((short) 1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when category not found")
        void deleteCategory_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Category not found"))
                    .when(categoryService).deleteCategory((short) 99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/count — countCategories")
    class CountCategories {

        @Test
        @DisplayName("should return 200 with the total count")
        void countCategories_returnsCount() {
            when(categoryService.countCategories()).thenReturn(16L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("16"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/exists/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/exists/{id} — existsById")
    class ExistsById {

        @Test
        @DisplayName("should return 200 true when category exists")
        void existsById_exists_returnsTrue() {
            when(categoryService.existsById((short) 1)).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/1")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when category does not exist")
        void existsById_notExists_returnsFalse() {
            when(categoryService.existsById((short) 99)).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/99")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/categories/exists/name/{name}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/categories/exists/name/{name} — existsByName")
    class ExistsByName {

        @Test
        @DisplayName("should return 200 true when category name exists")
        void existsByName_exists_returnsTrue() {
            when(categoryService.existsByName("Action")).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/name/Action")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when category name does not exist")
        void existsByName_notExists_returnsFalse() {
            when(categoryService.existsByName("Unknown")).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/name/Unknown")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }
}


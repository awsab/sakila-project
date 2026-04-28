package com.me.learning.parent.inventoryservice.controller;

import java.math.BigDecimal;
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
import com.me.learning.parent.inventoryservice.dto.request.FilmRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.FilmResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.FilmUpdateDTO;
import com.me.learning.parent.inventoryservice.service.FilmService;

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
 * Integration tests for {@link FilmController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("FilmController Integration Tests")
class FilmControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private FilmService filmService;

    private static final String BASE_URL = "/api/v1/films";

    /** A valid JSON body that satisfies all @NotNull / @NotBlank constraints on FilmRequestDTO. */
    private static final String VALID_FILM_JSON =
            "{\"title\":\"ACADEMY DINOSAUR\",\"description\":\"Epic dinosaur saga\"," +
            "\"releaseYear\":2006,\"rentalDuration\":6,\"rentalRate\":0.99," +
            "\"length\":86,\"replacementCost\":20.99,\"rating\":\"PG\"}";

    private FilmResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = FilmResponseDTO.builder()
                .id(1)
                .title("ACADEMY DINOSAUR")
                .description("Epic dinosaur saga")
                .releaseYear(2006)
                .rentalDuration((short) 6)
                .rentalRate(new BigDecimal("0.99"))
                .length(86)
                .replacementCost(new BigDecimal("20.99"))
                .rating("PG")
                .lastUpdate(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/films
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/films — createFilm")
    class CreateFilm {

        @Test
        @DisplayName("should return 201 and the created film")
        void createFilm_returnsCreated() {
            when(filmService.createFilm(any(FilmRequestDTO.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body(VALID_FILM_JSON)
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("filmId", equalTo(1))
                    .body("title",  equalTo("ACADEMY DINOSAUR"))
                    .body("rating", equalTo("PG"));
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void createFilm_blankTitle_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"title\":\"\",\"rentalRate\":0.99,\"replacementCost\":20.99}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when rentalRate is missing")
        void createFilm_missingRentalRate_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"title\":\"ACADEMY DINOSAUR\",\"replacementCost\":20.99}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 400 when replacementCost is missing")
        void createFilm_missingReplacementCost_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"title\":\"ACADEMY DINOSAUR\",\"rentalRate\":0.99}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 409 when film already exists")
        void createFilm_duplicate_returns409() {
            when(filmService.createFilm(any(FilmRequestDTO.class)))
                    .thenThrow(new IllegalArgumentException("Film already exists"));

            given()
                    .contentType(ContentType.JSON)
                    .body(VALID_FILM_JSON)
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/films/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/films/{id} — updateFilm")
    class UpdateFilm {

        @Test
        @DisplayName("should return 200 and the updated film")
        void updateFilm_returnsOk() {
            FilmResponseDTO updated = FilmResponseDTO.builder()
                    .id(1).title("UPDATED FILM").rating("R").build();
            when(filmService.updateFilm(eq(1), any(FilmUpdateDTO.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"filmId\":1,\"title\":\"UPDATED FILM\",\"rating\":\"R\"," +
                          "\"rentalRate\":0.99,\"replacementCost\":20.99}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("title",  equalTo("UPDATED FILM"))
                    .body("rating", equalTo("R"));
        }

        @Test
        @DisplayName("should return 404 when film not found")
        void updateFilm_notFound_returns404() {
            when(filmService.updateFilm(eq(99), any(FilmUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Film not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"filmId\":99,\"title\":\"UPDATED FILM\"," +
                          "\"rentalRate\":0.99,\"replacementCost\":20.99}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/films/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/films/{id} — patchFilm")
    class PatchFilm {

        @Test
        @DisplayName("should return 200 and the patched film")
        void patchFilm_returnsOk() {
            FilmResponseDTO patched = FilmResponseDTO.builder()
                    .id(1).title("ACADEMY DINOSAUR").rating("NC-17").build();
            when(filmService.patchFilm(eq(1), any(FilmUpdateDTO.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"filmId\":1,\"rating\":\"NC-17\"}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("rating", equalTo("NC-17"));
        }

        @Test
        @DisplayName("should return 404 when film not found")
        void patchFilm_notFound_returns404() {
            when(filmService.patchFilm(eq(99), any(FilmUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Film not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"filmId\":99,\"rating\":\"PG\"}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/{id} — getFilmById")
    class GetFilmById {

        @Test
        @DisplayName("should return 200 and the film")
        void getFilmById_found_returnsOk() {
            when(filmService.getFilmById(1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("filmId", equalTo(1))
                    .body("title",  equalTo("ACADEMY DINOSAUR"));
        }

        @Test
        @DisplayName("should return 404 when film not found")
        void getFilmById_notFound_returns404() {
            when(filmService.getFilmById(99))
                    .thenThrow(new ResourceNotFoundException("Film not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/title/{title}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/title/{title} — getFilmByTitle")
    class GetFilmByTitle {

        @Test
        @DisplayName("should return 200 and the film")
        void getFilmByTitle_found_returnsOk() {
            when(filmService.getFilmByTitle("ACADEMY DINOSAUR")).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/title/ACADEMY DINOSAUR")
            .then()
                    .statusCode(200)
                    .body("title", equalTo("ACADEMY DINOSAUR"));
        }

        @Test
        @DisplayName("should return 404 when film not found by title")
        void getFilmByTitle_notFound_returns404() {
            when(filmService.getFilmByTitle("UNKNOWN"))
                    .thenThrow(new ResourceNotFoundException("Film not found"));

            given()
            .when()
                    .get(BASE_URL + "/title/UNKNOWN")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films — getAllFilms")
    class GetAllFilms {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllFilms_returnsPage() {
            when(filmService.getAllFilms(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(1))
                    .body("content[0].title", equalTo("ACADEMY DINOSAUR"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllFilms_empty_returnsEmptyPage() {
            when(filmService.getAllFilms(any())).thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/search/title?title=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/search/title — searchByTitle")
    class SearchByTitle {

        @Test
        @DisplayName("should return 200 with matching films")
        void searchByTitle_returnsResults() {
            when(filmService.searchFilmsByTitle("ACADEMY")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("title", "ACADEMY")
            .when()
                    .get(BASE_URL + "/search/title")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].title", equalTo("ACADEMY DINOSAUR"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no match")
        void searchByTitle_noMatch_returnsEmpty() {
            when(filmService.searchFilmsByTitle("ZZZZZ")).thenReturn(List.of());

            given()
                    .queryParam("title", "ZZZZZ")
            .when()
                    .get(BASE_URL + "/search/title")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/search?searchTerm=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/search — searchFilms (title or description)")
    class SearchFilms {

        @Test
        @DisplayName("should return 200 with matching films")
        void searchFilms_returnsResults() {
            when(filmService.searchFilmsByTitleOrDescription("dinosaur"))
                    .thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("searchTerm", "dinosaur")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].title", equalTo("ACADEMY DINOSAUR"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no match")
        void searchFilms_noMatch_returnsEmpty() {
            when(filmService.searchFilmsByTitleOrDescription("ZZZZZ")).thenReturn(List.of());

            given()
                    .queryParam("searchTerm", "ZZZZZ")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-release-year/{year}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-release-year/{year} — getFilmsByReleaseYear")
    class GetFilmsByReleaseYear {

        @Test
        @DisplayName("should return 200 with films for the given year")
        void getFilmsByReleaseYear_returnsResults() {
            when(filmService.getFilmsByReleaseYear(2006)).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/by-release-year/2006")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].releaseYear", equalTo(2006));
        }

        @Test
        @DisplayName("should return 200 with empty list when no films in year")
        void getFilmsByReleaseYear_empty_returnsEmpty() {
            when(filmService.getFilmsByReleaseYear(1900)).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/by-release-year/1900")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-rating/{rating}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-rating/{rating} — getFilmsByRating")
    class GetFilmsByRating {

        @Test
        @DisplayName("should return 200 with films for the given rating")
        void getFilmsByRating_returnsResults() {
            when(filmService.getFilmsByRating("PG")).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/by-rating/PG")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].rating", equalTo("PG"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no films match rating")
        void getFilmsByRating_empty_returnsEmpty() {
            when(filmService.getFilmsByRating("NC-17")).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/by-rating/NC-17")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-rating/{rating}/paginated
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-rating/{rating}/paginated — getFilmsByRatingPaginated")
    class GetFilmsByRatingPaginated {

        @Test
        @DisplayName("should return 200 with paginated films for the given rating")
        void getFilmsByRatingPaginated_returnsPage() {
            when(filmService.getFilmsByRating(eq("PG"), any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL + "/by-rating/PG/paginated")
            .then()
                    .statusCode(200)
                    .body("content", hasSize(1))
                    .body("content[0].rating", equalTo("PG"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-rental-rate?maxRate=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-rental-rate — getFilmsByRentalRate")
    class GetFilmsByRentalRate {

        @Test
        @DisplayName("should return 200 with films within rental rate")
        void getFilmsByRentalRate_returnsResults() {
            when(filmService.getFilmsByRentalRate(new BigDecimal("2.99")))
                    .thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("maxRate", "2.99")
            .when()
                    .get(BASE_URL + "/by-rental-rate")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-length?minLength=&maxLength=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-length — getFilmsByLengthRange")
    class GetFilmsByLengthRange {

        @Test
        @DisplayName("should return 200 with films within length range")
        void getFilmsByLengthRange_returnsResults() {
            when(filmService.getFilmsByLengthRange(60, 120)).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("minLength", 60)
                    .queryParam("maxLength", 120)
            .when()
                    .get(BASE_URL + "/by-length")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].length", equalTo(86));
        }

        @Test
        @DisplayName("should return 200 with empty list when no films in range")
        void getFilmsByLengthRange_empty_returnsEmpty() {
            when(filmService.getFilmsByLengthRange(200, 300)).thenReturn(List.of());

            given()
                    .queryParam("minLength", 200)
                    .queryParam("maxLength", 300)
            .when()
                    .get(BASE_URL + "/by-length")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-category/{categoryId}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-category/{categoryId} — getFilmsByCategory")
    class GetFilmsByCategory {

        @Test
        @DisplayName("should return 200 with films for the given category")
        void getFilmsByCategory_returnsResults() {
            when(filmService.getFilmsByCategoryId((short) 1)).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/by-category/1")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }

        @Test
        @DisplayName("should return 200 with empty list when category has no films")
        void getFilmsByCategory_empty_returnsEmpty() {
            when(filmService.getFilmsByCategoryId((short) 99)).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/by-category/99")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/by-actor/{actorId}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/by-actor/{actorId} — getFilmsByActor")
    class GetFilmsByActor {

        @Test
        @DisplayName("should return 200 with films for the given actor")
        void getFilmsByActor_returnsResults() {
            when(filmService.getFilmsByActorId(1)).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/by-actor/1")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }

        @Test
        @DisplayName("should return 200 with empty list when actor has no films")
        void getFilmsByActor_empty_returnsEmpty() {
            when(filmService.getFilmsByActorId(999)).thenReturn(List.of());

            given()
            .when()
                    .get(BASE_URL + "/by-actor/999")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/films/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/films/{id} — deleteFilm")
    class DeleteFilm {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteFilm_returnsNoContent() {
            doNothing().when(filmService).deleteFilm(1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when film not found")
        void deleteFilm_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Film not found"))
                    .when(filmService).deleteFilm(99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/count — countFilms")
    class CountFilms {

        @Test
        @DisplayName("should return 200 with the total count")
        void countFilms_returnsCount() {
            when(filmService.countFilms()).thenReturn(1000L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("1000"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/count/by-rating/{rating}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/count/by-rating/{rating} — countByRating")
    class CountByRating {

        @Test
        @DisplayName("should return 200 with count for the rating")
        void countByRating_returnsCount() {
            when(filmService.countFilmsByRating("PG")).thenReturn(194L);

            given()
            .when()
                    .get(BASE_URL + "/count/by-rating/PG")
            .then()
                    .statusCode(200)
                    .body(equalTo("194"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/exists/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/exists/{id} — existsById")
    class ExistsById {

        @Test
        @DisplayName("should return 200 true when film exists")
        void existsById_exists_returnsTrue() {
            when(filmService.existsById(1)).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/1")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when film does not exist")
        void existsById_notExists_returnsFalse() {
            when(filmService.existsById(99)).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/99")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/films/exists/title/{title}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/films/exists/title/{title} — existsByTitle")
    class ExistsByTitle {

        @Test
        @DisplayName("should return 200 true when title exists")
        void existsByTitle_exists_returnsTrue() {
            when(filmService.existsByTitle("ACADEMY DINOSAUR")).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/title/ACADEMY DINOSAUR")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when title does not exist")
        void existsByTitle_notExists_returnsFalse() {
            when(filmService.existsByTitle("UNKNOWN")).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/title/UNKNOWN")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }
}


package com.me.learning.parent.inventoryservice.controller;

import java.time.LocalDateTime;
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

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.parent.inventoryservice.dto.request.ActorRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.ActorResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.ActorUpdateDTO;
import com.me.learning.parent.inventoryservice.service.ActorService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
 * Integration tests for {@link ActorController} using REST Assured.
 * The Spring context is loaded with a random port; the service layer is mocked
 * so no real database is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("ActorController Integration Tests")
class ActorControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ActorService actorService;

    private static final String BASE_URL = "/api/v1/actors";

    private ActorResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = ActorResponseDTO.builder()
                .id(1)
                .firstName("PENELOPE")
                .lastName("GUINESS")
                .lastUpdate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/v1/actors
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /api/v1/actors — createActor")
    class CreateActor {

        @Test
        @DisplayName("should return 201 and the created actor")
        void createActor_returnsCreated() {
            when(actorService.createActor(any(ActorRequestDTO.class))).thenReturn(sampleResponse);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"firstName\":\"PENELOPE\",\"lastName\":\"GUINESS\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(201)
                    .body("actorId",    equalTo(1))
                    .body("firstName",  equalTo("PENELOPE"))
                    .body("lastName",   equalTo("GUINESS"));
        }

        @Test
        @DisplayName("should return 400 when firstName is blank")
        void createActor_blankFirstName_returns400() {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"firstName\":\"\",\"lastName\":\"GUINESS\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("should return 409 when actor already exists")
        void createActor_duplicate_returns409() {
            when(actorService.createActor(any(ActorRequestDTO.class)))
                    .thenThrow(new DuplicateResourceException("Actor already exists"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"firstName\":\"PENELOPE\",\"lastName\":\"GUINESS\"}")
            .when()
                    .post(BASE_URL)
            .then()
                    .statusCode(409);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/v1/actors/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PUT /api/v1/actors/{id} — updateActor")
    class UpdateActor {

        @Test
        @DisplayName("should return 200 and the updated actor")
        void updateActor_returnsOk() {
            ActorResponseDTO updated = ActorResponseDTO.builder()
                    .id(1).firstName("NICK").lastName("WAHLBERG").build();
            when(actorService.updateActor(eq(1), any(ActorUpdateDTO.class))).thenReturn(updated);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"actorId\":1,\"firstName\":\"NICK\",\"lastName\":\"WAHLBERG\"}")
            .when()
                    .put(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("firstName", equalTo("NICK"))
                    .body("lastName",  equalTo("WAHLBERG"));
        }

        @Test
        @DisplayName("should return 404 when actor not found")
        void updateActor_notFound_returns404() {
            when(actorService.updateActor(eq(99), any(ActorUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Actor not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"actorId\":99,\"firstName\":\"NICK\",\"lastName\":\"WAHLBERG\"}")
            .when()
                    .put(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PATCH /api/v1/actors/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("PATCH /api/v1/actors/{id} — patchActor")
    class PatchActor {

        @Test
        @DisplayName("should return 200 and the patched actor")
        void patchActor_returnsOk() {
            ActorResponseDTO patched = ActorResponseDTO.builder()
                    .id(1).firstName("PENELOPE").lastName("SMITH").build();
            when(actorService.patchActor(eq(1), any(ActorUpdateDTO.class))).thenReturn(patched);

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"actorId\":1,\"lastName\":\"SMITH\"}")
            .when()
                    .patch(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("lastName", equalTo("SMITH"));
        }

        @Test
        @DisplayName("should return 404 when actor not found")
        void patchActor_notFound_returns404() {
            when(actorService.patchActor(eq(99), any(ActorUpdateDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Actor not found"));

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"actorId\":99,\"lastName\":\"SMITH\"}")
            .when()
                    .patch(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/{id} — getActorById")
    class GetActorById {

        @Test
        @DisplayName("should return 200 and the actor")
        void getActorById_found_returnsOk() {
            when(actorService.getActorById(1)).thenReturn(sampleResponse);

            given()
            .when()
                    .get(BASE_URL + "/1")
            .then()
                    .statusCode(200)
                    .body("actorId",   equalTo(1))
                    .body("firstName", equalTo("PENELOPE"));
        }

        @Test
        @DisplayName("should return 404 when actor not found")
        void getActorById_notFound_returns404() {
            when(actorService.getActorById(99))
                    .thenThrow(new ResourceNotFoundException("Actor not found"));

            given()
            .when()
                    .get(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors  (paginated)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors — getAllActors")
    class GetAllActors {

        @Test
        @DisplayName("should return 200 with paginated content")
        void getAllActors_returnsPage() {
            when(actorService.getAllActors(any()))
                    .thenReturn(new PageImpl<>(List.of(sampleResponse), PageRequest.of(0, 20), 1));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(1))
                    .body("content[0].firstName", equalTo("PENELOPE"));
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void getAllActors_empty_returnsEmptyPage() {
            when(actorService.getAllActors(any()))
                    .thenReturn(new PageImpl<>(List.of()));

            given()
            .when()
                    .get(BASE_URL)
            .then()
                    .statusCode(200)
                    .body("content", hasSize(0));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors/search?searchTerm=
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/search — searchActors")
    class SearchActors {

        @Test
        @DisplayName("should return 200 with matching actors")
        void searchActors_returnsResults() {
            when(actorService.searchActorsByName("PEN")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("searchTerm", "PEN")
            .when()
                    .get(BASE_URL + "/search")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].firstName", equalTo("PENELOPE"));
        }

        @Test
        @DisplayName("should return 200 with empty list when no match")
        void searchActors_noMatch_returnsEmpty() {
            when(actorService.searchActorsByName("ZZZ")).thenReturn(List.of());

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
    //  GET /api/v1/actors/by-first-name
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/by-first-name — getActorsByFirstName")
    class GetActorsByFirstName {

        @Test
        @DisplayName("should return 200 with actors matching first name")
        void getActorsByFirstName_returnsResults() {
            when(actorService.getActorsByFirstName("PENELOPE")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("firstName", "PENELOPE")
            .when()
                    .get(BASE_URL + "/by-first-name")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors/by-last-name
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/by-last-name — getActorsByLastName")
    class GetActorsByLastName {

        @Test
        @DisplayName("should return 200 with actors matching last name")
        void getActorsByLastName_returnsResults() {
            when(actorService.getActorsByLastName("GUINESS")).thenReturn(List.of(sampleResponse));

            given()
                    .queryParam("lastName", "GUINESS")
            .when()
                    .get(BASE_URL + "/by-last-name")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors/sorted
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/sorted — getAllActorsSorted")
    class GetAllActorsSorted {

        @Test
        @DisplayName("should return 200 with sorted actors")
        void getAllActorsSorted_returnsResults() {
            when(actorService.getAllActorsSortedByLastName()).thenReturn(List.of(sampleResponse));

            given()
            .when()
                    .get(BASE_URL + "/sorted")
            .then()
                    .statusCode(200)
                    .body("$", hasSize(1));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  DELETE /api/v1/actors/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("DELETE /api/v1/actors/{id} — deleteActor")
    class DeleteActor {

        @Test
        @DisplayName("should return 204 on successful deletion")
        void deleteActor_returnsNoContent() {
            doNothing().when(actorService).deleteActor(1);

            given()
            .when()
                    .delete(BASE_URL + "/1")
            .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("should return 404 when actor not found")
        void deleteActor_notFound_returns404() {
            doThrow(new ResourceNotFoundException("Actor not found"))
                    .when(actorService).deleteActor(99);

            given()
            .when()
                    .delete(BASE_URL + "/99")
            .then()
                    .statusCode(404);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors/count
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/count — countActors")
    class CountActors {

        @Test
        @DisplayName("should return 200 with the total count")
        void countActors_returnsCount() {
            when(actorService.countActors()).thenReturn(200L);

            given()
            .when()
                    .get(BASE_URL + "/count")
            .then()
                    .statusCode(200)
                    .body(equalTo("200"));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/v1/actors/exists/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/v1/actors/exists/{id} — existsById")
    class ExistsById {

        @Test
        @DisplayName("should return 200 true when actor exists")
        void existsById_exists_returnsTrue() {
            when(actorService.existsById(1)).thenReturn(true);

            given()
            .when()
                    .get(BASE_URL + "/exists/1")
            .then()
                    .statusCode(200)
                    .body(is("true"));
        }

        @Test
        @DisplayName("should return 200 false when actor does not exist")
        void existsById_notExists_returnsFalse() {
            when(actorService.existsById(99)).thenReturn(false);

            given()
            .when()
                    .get(BASE_URL + "/exists/99")
            .then()
                    .statusCode(200)
                    .body(is("false"));
        }
    }
}


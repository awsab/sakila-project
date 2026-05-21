package com.me.learning.catalog.service;

import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;
import com.me.learning.catalog.entity.Actor;
import com.me.learning.catalog.mapper.ActorMapper;
import com.me.learning.catalog.repository.ActorRepository;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Unit test for {@link ActorServiceImpl}.
 * <p> All collaborators ({@link ActorRepository}, {@link ActorMapper}) are mocked,
 * so these tests focus solely on the logic within {@link ActorServiceImpl}.
 * </p>
 *
 * <p>
 * Assertions use AssertJ for a fluent and readable style
 * </p>
 */

@ExtendWith (MockitoExtension.class)
@DisplayName ("ActorServiceImpl — Unit Tests")
@SuppressWarnings("PMD.MethodNamingConventions")
class ActorServiceImplTest {

    /* ── Constants ─────────────────────────────────────────────────────── */
    private static final Instant NOW = Instant.parse ("2024-06-01T12:00:00Z");
    private static final int ACTOR_ID = 1;
    private static final String FIRST_NAME = "Brad";
    private static final String LAST_NAME = "James";

    /* ── Mocks ──────────────────────────────────────────────────────────── */
    @Mock
    private ActorRepository actorRepository;

    @Mock
    private ActorMapper actorMapper;

    @InjectMocks
    private ActorServiceImpl actorServiceImpl;

    /* ── Test-data builders ─────────────────────────────────────────────── */
    private Actor actorBuilder () {
        Actor actor = new Actor ();
        actor.setId (ACTOR_ID);
        actor.setFirstName (FIRST_NAME);
        actor.setLastName (LAST_NAME);
        actor.setLastUpdate (NOW);
        return actor;
    }

    private ActorRequestDto buildRequest () {
        return new ActorRequestDto (1, "Brad", "James", NOW);
    }

    private ActorResponseDto buildResponse () {
        return new ActorResponseDto (1, "Brad", "James", NOW);
    }

    /* ── Test cases ───────────────────────────────────────────────────────── */

     /*══════════════════════════════════════════════════════════════════════
    create()
    ══════════════════════════════════════════════════════════════════════*/

    @Nested
    @DisplayName ("create() — Create a new actor")
    class CreateActor {

        @Test
        @DisplayName ("Should create a new actor successfully")
        void Create_withValidRequest_WithPopulatedActorResponse () {

            /* Given */
            ActorRequestDto request = new ActorRequestDto (null, FIRST_NAME, LAST_NAME, NOW);
            Actor actor = actorBuilder ();
            ActorResponseDto expectedResponse = buildResponse ();

            /* When */
            when (actorMapper.toEntity (request)).thenReturn (actor);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toResponseDto (actor)).thenReturn (expectedResponse);

            ActorResponseDto actualResponse = actorServiceImpl.create (request);

            /* Then */
            assertThat (actualResponse)
                    .usingRecursiveComparison ()
                    .isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void Create_withNullRequest_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = null;

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.create (request))
                    .isInstanceOf (IllegalArgumentException.class);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is provided in request")
        void Create_withIdInRequest_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = new ActorRequestDto (1, FIRST_NAME, LAST_NAME, NOW);

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.create (request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("New actor should not have an ID");
        }


    }

    /* ══════════════════════════════════════════════════════════════════════
       update()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("update() — Update an existing actor")
    class UpdateActor {

        @Test
        @DisplayName ("Should update an existing actor successfully")
        void Update_withValidRequest_WithPopulatedActorResponse () {
            /* Given */
            ActorRequestDto request = buildRequest ();
            Actor actor = actorBuilder ();
            ActorResponseDto expectedResponse = buildResponse ();

            /* When */
            when (actorMapper.toEntity (request)).thenReturn (actor);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toResponseDto (actor)).thenReturn (expectedResponse);

            ActorResponseDto actualResponse = actorServiceImpl.update (ACTOR_ID, request);

            /* Then */
            assertThat (actualResponse)
                    .usingRecursiveComparison ()
                    .isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void Update_withNullRequest_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = null;

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.update (1, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Request cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when first name is null")
        void Update_withNullFirstName_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = new ActorRequestDto (1, null, "James", NOW);

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.update (1, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("First name cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when last name is null")
        void Update_withNullLastName_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = new ActorRequestDto (1, "Brad", null, NOW);

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.update (1, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Last name cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when actor ID is null")
        void Update_withNullActorId_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = new ActorRequestDto (null, "Brad", "James", NOW);

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.update (1, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Actor ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when update ID is null")
        void Update_withNullId_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = buildRequest ();

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.update (null, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       partialUpdate()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("partialUpdate() — Partially update an existing actor")
    class PartialUpdateActor {

        @Test
        @DisplayName ("Should partially update an existing actor successfully")
        void PartialUpdate_withValidRequest_WithPopulatedActorResponse () {
            /* Given */
            ActorRequestDto request = buildRequest ();
            Actor actor = actorBuilder ();
            ActorResponseDto expectedResponse = buildResponse ();

            /* When */
            when (actorRepository.findById (ACTOR_ID)).thenReturn (Optional.of (actor));
            when (actorMapper.partialUpdate (request, actor)).thenReturn (actor);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toResponseDto (actor)).thenReturn (expectedResponse);

            ActorResponseDto actualResponse = actorServiceImpl.partialUpdate (1, request);

            /* Then */
            assertThat (actualResponse)
                    .usingRecursiveComparison ()
                    .isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void PartialUpdate_withNullRequest_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = null;

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.partialUpdate (1, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Request cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when actor ID is null")
        void PartialUpdate_withNullActorId_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = new ActorRequestDto (null, "Brad", "James", NOW);

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.partialUpdate (1, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Actor ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when partial update ID is null")
        void PartialUpdate_withNullId_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = buildRequest ();

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.partialUpdate (null, request))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when partial updating missing actor")
        void PartialUpdate_withNonExistentId_ShouldThrowException () {
            /* Given */
            ActorRequestDto request = buildRequest ();
            when (actorRepository.findById (ACTOR_ID)).thenReturn (Optional.empty ());

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.partialUpdate (ACTOR_ID, request))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       findById()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findById() — Find an actor by ID")
    class FindActorById {

        @Test
        @DisplayName ("Should find an existing actor by ID successfully")
        void FindById_withValidId_WithPopulatedActorResponse () {
            /* Given */
            Actor actor = actorBuilder ();
            ActorResponseDto expectedResponse = buildResponse ();

            /* When */
            when (actorRepository.findById (ACTOR_ID)).thenReturn (Optional.of (actor));
            when (actorMapper.toResponseDto (actor)).thenReturn (expectedResponse);

            ActorResponseDto actualResponse = actorServiceImpl.findById (ACTOR_ID);

            /* Then */
            assertThat (actualResponse)
                    .usingRecursiveComparison ()
                    .isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void FindById_withNullId_ShouldThrowException () {
            /* Given */
            Integer id = null;

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.findById (id))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when actor ID is not found")
        void FindById_withNonExistentId_ShouldThrowException () {
            /* Given */
            when (actorRepository.findById (ACTOR_ID)).thenReturn (Optional.empty ());

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.findById (ACTOR_ID))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

      /* ══════════════════════════════════════════════════════════════════════
       findAll() — unpaged
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findAll() — Find all actors (unpaged)")
    class FindAllActorsUnpaged {

        @Test
        @DisplayName ("Should find all actors successfully")
        void FindAll_withActorsInRepository_ShouldReturnListOfActorResponses () {
            /* Given */
            Actor actor1 = actorBuilder ();
            Actor actor2 = new Actor ();
            actor2.setId (2);
            actor2.setFirstName ("Brad");
            actor2.setLastName ("Pitt");
            actor2.setLastUpdate (NOW);

            ActorResponseDto response1 = buildResponse ();
            ActorResponseDto response2 = new ActorResponseDto (2, "Brad", "Pitt", NOW);

            /* When */
            when (actorRepository.findAll ()).thenReturn (List.of (actor1, actor2));
            when (actorMapper.toResponseDto (actor1)).thenReturn (response1);
            when (actorMapper.toResponseDto (actor2)).thenReturn (response2);

            List<ActorResponseDto> actualResponses = actorServiceImpl.findAll ();

            /* Then */
            assertThat (actualResponses)
                    .usingRecursiveComparison ()
                    .isEqualTo (List.of (response1, response2));
        }

        @Test
        @DisplayName ("Should return empty list when no actors in repository")
        void FindAll_withNoActorsInRepository_ShouldReturnEmptyList () {
            /* Given */

            /* When */
            when (actorRepository.findAll ()).thenReturn (Collections.emptyList ());

            List<ActorResponseDto> actualResponses = actorServiceImpl.findAll ();

            /* Then */
            assertThat (actualResponses.isEmpty ());
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       findAll(Pageable)
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findAll(Pageable) — Find all actors (paged)")
    class FindAllActorsPaged {

        @Test
        @DisplayName ("Should find all actors with pagination successfully")
        void FindAll_withValidPageable_ShouldReturnPageOfActorResponses () {
            /* Given */
            Actor actor1 = actorBuilder ();
            Actor actor2 = new Actor ();
            actor2.setId (2);
            actor2.setFirstName ("Brad");
            actor2.setLastName ("Pitt");
            actor2.setLastUpdate (NOW);

            ActorResponseDto response1 = buildResponse ();
            ActorResponseDto response2 = new ActorResponseDto (2, "Brad", "Pitt", NOW);

            Pageable pageable = PageRequest.of (0, 10);
            Page<Actor> actorPage =
                    new PageImpl<> (List.of (actor1, actor2), pageable, 2);

            /* When */
            when (actorRepository.findAll (pageable)).thenReturn (actorPage);
            when (actorMapper.toResponseDto (actor1)).thenReturn (response1);
            when (actorMapper.toResponseDto (actor2)).thenReturn (response2);

            Page<ActorResponseDto> actualResponsePage = actorServiceImpl.findAll (pageable);

            /* Then */
            assertThat (actualResponsePage.getContent ())
                    .usingRecursiveComparison ()
                    .isEqualTo (List.of (response1, response2));
        }

        @Test
        @DisplayName ("Should return empty page when no actors in repository")
        void FindAll_withNoActorsInRepository_ShouldReturnEmptyPage () {
            /* Given */
            Pageable pageable = PageRequest.of (0, 10);
            Page<Actor> emptyActorPage =
                    new PageImpl<> (Collections.emptyList (), pageable, 0);

            /* When */
            when (actorRepository.findAll (pageable)).thenReturn (emptyActorPage);

            Page<ActorResponseDto> actualResponsePage = actorServiceImpl.findAll (pageable);

            /* Then */
            assertThat (actualResponsePage.getContent ()
                    .isEmpty ());
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       delete()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("delete() — Delete an actor by ID")
    class DeleteActorById {

        @Test
        @DisplayName ("Should delete an existing actor by ID successfully")
        void Delete_withValidId_ShouldDeleteActor () {
            /* Given */
            Integer id = ACTOR_ID;

            /* When */
            when (actorRepository.existsById (id)).thenReturn (true);

            assertDoesNotThrow (() -> actorServiceImpl.delete (id));
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void Delete_withNullId_ShouldThrowException () {
            /* Given */
            Integer id = null;

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.delete (id))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when actor does not exist")
        void Delete_withNonExistentId_ShouldThrowException () {
            /* Given */
            Integer id = ACTOR_ID;

            /* When */
            when (actorRepository.existsById (id)).thenReturn (false);

            /* Then */
            assertThatThrownBy (() -> actorServiceImpl.delete (id))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Actor with ID " + id + " does not exist");
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       existsById()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("existsById() — Check if an actor exists by ID")
    class ExistsActorById {

        @Test
        @DisplayName ("Should return true when actor exists by ID")
        void ExistsById_withExistingId_ShouldReturnTrue () {
            /* Given */
            Integer id = ACTOR_ID;

            /* When */
            when (actorRepository.existsById (id)).thenReturn (true);

            boolean exists = actorServiceImpl.existsById (id);

            /* Then */
            assertThat (exists)
                    .isTrue ();
        }

        @Test
        @DisplayName ("Should return false when actor does not exist by ID")
        void ExistsById_withNonExistingId_ShouldReturnFalse () {
            /* Given */
            Integer id = ACTOR_ID;

            /* When */
            when (actorRepository.existsById (id)).thenReturn (false);

            boolean exists = actorServiceImpl.existsById (id);

            /* Then */
            assertThat (exists)
                    .isFalse ();
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void ExistsById_withNullId_ShouldThrowException () {
            /* Given */
            Integer id = null;

            /* When & Then */
            assertThatThrownBy (() -> actorServiceImpl.existsById (id))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       count()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("count() — Count total number of actors")
    class CountActors {

        @Test
        @DisplayName ("Should return the total number of actors")
        void Count_ShouldReturnTotalNumberOfActors () {
            /* Given */
            long expectedCount = 5L;

            /* When */
            when (actorRepository.count ()).thenReturn (expectedCount);

            long actualCount = actorServiceImpl.count ();

            /* Then */
            assertThat (actualCount)
                    .isEqualTo (expectedCount);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when count is negative")
        void Count_withNegativeCount_ShouldThrowException () {
            /* Given */
            long negativeCount = -1L;

            /* When */
            when (actorRepository.count ()).thenReturn (negativeCount);

            /* Then */
            assertThatThrownBy (actorServiceImpl::count)
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Count cannot be negative");
        }
    }
}
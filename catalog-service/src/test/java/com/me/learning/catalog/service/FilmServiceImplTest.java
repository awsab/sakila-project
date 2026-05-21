package com.me.learning.catalog.service;

import com.me.learning.catalog.dto.ActorSummaryDto;
import com.me.learning.catalog.dto.CategorySummaryDto;
import com.me.learning.catalog.dto.FilmRequestDto;
import com.me.learning.catalog.dto.FilmResponseDto;
import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;
import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;
import com.me.learning.catalog.entity.Film;
import com.me.learning.catalog.entity.Actor;
import com.me.learning.catalog.entity.Category;
import com.me.learning.catalog.entity.FilmActor;
import com.me.learning.catalog.entity.FilmActorId;
import com.me.learning.catalog.entity.FilmCategory;
import com.me.learning.catalog.entity.FilmCategoryId;
import com.me.learning.catalog.entity.Language;
import com.me.learning.catalog.mapper.ActorMapper;
import com.me.learning.catalog.mapper.CategoryMapper;
import com.me.learning.catalog.mapper.FilmMapper;
import com.me.learning.catalog.repository.ActorRepository;
import com.me.learning.catalog.repository.CategoryRepository;
import com.me.learning.catalog.repository.FilmActorRepository;
import com.me.learning.catalog.repository.FilmCategoryRepository;
import com.me.learning.catalog.repository.FilmRepository;
import com.me.learning.catalog.repository.LanguageRepository;
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
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Test Cases for {@link FilmServiceImpl}
 *
 * <p>
 * All Collaborators are ({@link FilmRepository} and {@link FilmRepository})
 * so these tests focus solely on the logic within {@link FilmServiceImpl}
 * </p>
 * <p>
 * Assertions use AssertJ for a fluent and readable style
 * </p>
 */

@ExtendWith (MockitoExtension.class)
@DisplayName ("Unit Tests for FilmServiceImpl")
@SuppressWarnings("PMD.MethodNamingConventions")
class FilmServiceImplTest {

    /* ── Constants ─────────────────────────────────────────────────────── */
    private static final Integer FILM_ID = 1;
    private static final Integer ACTOR_ID = 10;
    private static final Short CATEGORY_ID = 5;
    private static final Instant NOW = Instant.parse ("2024-06-01T12:00:00Z");

    /* ── Mocks ──────────────────────────────────────────────────────────── */
    @Mock
    private FilmRepository filmRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FilmActorRepository filmActorRepository;

    @Mock
    private FilmCategoryRepository filmCategoryRepository;

    @Mock
    private FilmMapper filmMapper;

    @Mock
    private ActorMapper actorMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ActorService actorService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private FilmServiceImpl filmService;

    /* ── Test-data builders ─────────────────────────────────────────────── */

    private Film filmBuilder () {
        Film film = new Film ();
        film.setId (FILM_ID);
        film.setTitle ("Test Movie");
        film.setDescription ("A test movie description.");
        film.setReleaseYear ((short) 2020);
        film.setLanguage (languageBuilder ());
        film.setOriginalLanguage (null);
        film.setRentalDuration ((short) 3);
        film.setRentalRate (BigDecimal.valueOf (4.99));
        film.setLength ((short) 120);
        film.setReplacementCost (BigDecimal.valueOf (19.99));
        film.setRating ("PG-13");
        film.setSpecialFeatures ("Trailers,Commentaries");

        Set<FilmActor> filmActors = new LinkedHashSet<> ();
        filmActors.add (filmActorBuilder (film));
        film.setFilmActors (filmActors);

        Set<FilmCategory> filmCategories = new LinkedHashSet<> ();
        filmCategories.add (filmCategoryBuilder (film));
        film.setFilmCategories (filmCategories);

        film.setLastUpdate (NOW);
        return film;
    }

    private Language languageBuilder () {
        Language language = new Language ();
        language.setId ((short) 1);
        language.setName ("English");
        language.setLastUpdate (NOW);
        return language;
    }

    private FilmActor filmActorBuilder (Film film) {
        FilmActorId id = new FilmActorId ();
        id.setFilmId (FILM_ID);
        id.setActorId (ACTOR_ID);

        FilmActor filmActor = new FilmActor ();
        filmActor.setId (id);
        filmActor.setFilm (film);
        filmActor.setActor (actorBuilder ());
        filmActor.setLastUpdate (NOW);
        return filmActor;
    }

    private FilmCategory filmCategoryBuilder (Film film) {
        FilmCategoryId id = new FilmCategoryId ();
        id.setFilmId (FILM_ID);
        id.setCategoryId (CATEGORY_ID);

        FilmCategory filmCategory = new FilmCategory ();
        filmCategory.setId (id);
        filmCategory.setFilm (film);
        filmCategory.setCategory (categoryBuilder ());
        filmCategory.setLastUpdate (NOW);
        return filmCategory;
    }

    private Actor actorBuilder () {
        Actor actor = new Actor ();
        actor.setId (ACTOR_ID);
        actor.setFirstName ("Test");
        actor.setLastName ("Actor");
        actor.setLastUpdate (NOW);
        return actor;
    }

    private Category categoryBuilder () {
        Category category = new Category ();
        category.setId (CATEGORY_ID);
        category.setName ("Action");
        category.setLastUpdate (NOW);
        return category;
    }

    private FilmResponseDto filmResponseDtoBuilder () {
        return new FilmResponseDto (
                FILM_ID,
                "Test Movie",
                "A test movie description.",
                (short) 2020,
                (short) 1,
                "English",
                null,
                null,
                (short) 3,
                BigDecimal.valueOf (4.99),
                (short) 120,
                BigDecimal.valueOf (19.99),
                "PG-13",
                "Trailers,Commentaries",
                NOW,
                List.of (new ActorSummaryDto (ACTOR_ID, "Test", "Actor")),
                List.of (new CategorySummaryDto (CATEGORY_ID, "Action"))
        );
    }

    private FilmRequestDto filmRequestDtoBuilder () {
        return new FilmRequestDto (
                null,
                "Test Movie",
                "A test movie description.",
                (short) 2020,
                (short) 1,
                null,
                (short) 3,
                BigDecimal.valueOf (4.99),
                (short) 120,
                BigDecimal.valueOf (19.99),
                "PG-13",
                "Trailers,Commentaries",
                NOW,
                List.of (ACTOR_ID),
                List.of (CATEGORY_ID),
                List.of (),
                List.of ()
        );
    }


    /* ── Test cases ───────────────────────────────────────────────────────── */

     /*══════════════════════════════════════════════════════════════════════
    create()
    ══════════════════════════════════════════════════════════════════════*/

    @Nested
    @DisplayName ("create() — Create a new Film")
    class CreateTests {

        @Test
        @DisplayName ("Should create a new Film successfully")
        void Create_withValidRequest_WithPopulatedFilmResponse () {

            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Film film = filmBuilder ();
            Film savedFilm = filmBuilder ();   // fresh instance whose id=FILM_ID is preserved after save
            FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

            /* When */
            when (filmMapper.toEntity (requestDto)).thenReturn (film);
            when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
            when (filmRepository.save (any ())).thenReturn (savedFilm);
            doNothing ().when (filmActorRepository).deleteByFilmId (any ());
            when (actorRepository.findById (ACTOR_ID)).thenReturn (java.util.Optional.of (actorBuilder ()));
            when (filmActorRepository.save (any ())).thenReturn (filmActorBuilder (savedFilm));
            doNothing ().when (filmCategoryRepository).deleteByFilmId (any ());
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (java.util.Optional.of (categoryBuilder ()));
            when (filmCategoryRepository.save (any ())).thenReturn (filmCategoryBuilder (savedFilm));
            when (filmRepository.findById (FILM_ID)).thenReturn (java.util.Optional.of (savedFilm));
            when (filmMapper.toFilmResponseDto (savedFilm)).thenReturn (expectedResponse);

            FilmResponseDto actualResponse = filmService.create (requestDto);

            /* Then */
            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void Create_withNullRequest_ShouldThrowIllegalArgumentException () {
            /* Given */
            FilmRequestDto requestDto = null;

            /* When / Then */
            assertThatThrownBy (() -> filmService.create (requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Request cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is provided in request")
        void Create_withIdInRequest_ShouldThrowIllegalArgumentException () {
            /* Given — build a request DTO that already carries an id (records have no setters) */
            FilmRequestDto requestDto = new FilmRequestDto (
                    FILM_ID,                          // id is present — should be rejected
                    "Test Movie",
                    "A test movie description.",
                    (short) 2020,
                    (short) 1,
                    null,
                    (short) 3,
                    BigDecimal.valueOf (4.99),
                    (short) 120,
                    BigDecimal.valueOf (19.99),
                    "PG-13",
                    "Trailers,Commentaries",
                    NOW,
                    List.of (ACTOR_ID),
                    List.of (CATEGORY_ID),
                    List.of (),
                    List.of ()
            );

            /* When / Then */
            assertThatThrownBy (() -> filmService.create (requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID must not be provided in request");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when create request has unknown language")
        void Create_withUnknownLanguage_ShouldThrowResourceNotFoundException () {
            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Film film = filmBuilder ();

            /* When */
            when (filmMapper.toEntity (requestDto)).thenReturn (film);
            when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.empty ());

            /* Then */
            assertThatThrownBy (() -> filmService.create (requestDto))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when create request has unknown original language")
        void Create_withUnknownOriginalLanguage_ShouldThrowResourceNotFoundException () {
            /* Given */
            Short unknownOriginalLanguageId = 99;
            FilmRequestDto requestDto = new FilmRequestDto (
                    null,
                    "Test Movie",
                    "A test movie description.",
                    (short) 2020,
                    (short) 1,
                    unknownOriginalLanguageId,
                    (short) 3,
                    BigDecimal.valueOf (4.99),
                    (short) 120,
                    BigDecimal.valueOf (19.99),
                    "PG-13",
                    "Trailers,Commentaries",
                    NOW,
                    List.of (ACTOR_ID),
                    List.of (CATEGORY_ID),
                    List.of (),
                    List.of ()
            );
            Film film = filmBuilder ();

            /* When */
            when (filmMapper.toEntity (requestDto)).thenReturn (film);
            when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
            when (languageRepository.findById (unknownOriginalLanguageId)).thenReturn (java.util.Optional.empty ());

            /* Then */
            assertThatThrownBy (() -> filmService.create (requestDto))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       update()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("update() — Update an existing Film")
    class UpdateTests {

        @Test
        @DisplayName ("Should partially update an existing Film successfully")
        void Update_withValidRequest_ShouldPartiallyUpdateFilm () {

            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Film existingFilm = filmBuilder ();
            Film updatedFilm = filmBuilder ();   // fresh instance whose id=FILM_ID is preserved after update
            FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

            /* When */
            when (filmRepository.findById (FILM_ID))
                    .thenReturn (java.util.Optional.of (existingFilm))  // 1st call — fetch for mutation
                    .thenReturn (java.util.Optional.of (updatedFilm));  // 2nd call — fetch for response
            when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
            when (filmRepository.save (any ())).thenReturn (updatedFilm);
            doNothing ().when (filmActorRepository).deleteByFilmId (any ());
            when (actorRepository.findById (ACTOR_ID)).thenReturn (java.util.Optional.of (actorBuilder ()));
            when (filmActorRepository.save (any ())).thenReturn (filmActorBuilder (updatedFilm));
            doNothing ().when (filmCategoryRepository).deleteByFilmId (any ());
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (java.util.Optional.of (categoryBuilder ()));
            when (filmCategoryRepository.save (any ())).thenReturn (filmCategoryBuilder (updatedFilm));
            when (filmMapper.toFilmResponseDto (updatedFilm)).thenReturn (expectedResponse);

            /* Then */
            FilmResponseDto actualResponse = filmService.update (FILM_ID, requestDto);
            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);

        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void Update_withNullRequest_ShouldThrowIllegalArgumentException () {

            /* Gievn */
            FilmRequestDto requestDto = null;

            /* When and Then */
            assertThatThrownBy (() -> filmService.update (FILM_ID, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Request cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when Film ID is null")
        void Update_withNullId_ShouldThrowIllegalArgumentException () {

            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();

            /* When and Then */
            assertThatThrownBy (() -> filmService.update (null, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Film ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when updating non-existent film")
        void Update_withNonExistentId_ShouldThrowResourceNotFoundException () {
            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Integer nonExistentFilmId = 999;

            /* When */
            when (filmRepository.findById (nonExistentFilmId)).thenReturn (java.util.Optional.empty ());

            /* Then */
            assertThatThrownBy (() -> filmService.update (nonExistentFilmId, requestDto))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when update request has unknown language")
        void Update_withUnknownLanguage_ShouldThrowResourceNotFoundException () {
            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Film existingFilm = filmBuilder ();

            /* When */
            when (filmRepository.findById (FILM_ID)).thenReturn (java.util.Optional.of (existingFilm));
            when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.empty ());

            /* Then */
            assertThatThrownBy (() -> filmService.update (FILM_ID, requestDto))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       partialUpdate()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("partialUpdate() — Partially update an existing Film")
    class PartialUpdateTests {

        @Test
        @DisplayName ("Should partially update an existing Film successfully")
        void PartialUpdate_withValidRequest_ShouldPartiallyUpdateFilm () {
            /* Given — request has only title + languageId + actorIds + categoryIds populated (partial patch) */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Film existingFilm = filmBuilder ();
            Film patchedFilm = filmBuilder ();   // represents the persisted state after patching
            FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

            /* When */
            // findById is called twice: once to load the entity, once at the end to build the response
            when (filmRepository.findById (FILM_ID))
                    .thenReturn (java.util.Optional.of (existingFilm))  // 1st call — load for mutation
                    .thenReturn (java.util.Optional.of (patchedFilm));  // 2nd call — load for response
            // filmMapper.partialUpdate(dto, existing) mutates existing in place; return value is ignored
            // so no stub is needed — the default mock (null) is fine
            when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
            when (filmRepository.save (any ())).thenReturn (patchedFilm);
            doNothing ().when (filmActorRepository).deleteByFilmId (any ());
            when (actorRepository.findById (ACTOR_ID)).thenReturn (java.util.Optional.of (actorBuilder ()));
            when (filmActorRepository.save (any ())).thenReturn (filmActorBuilder (patchedFilm));
            doNothing ().when (filmCategoryRepository).deleteByFilmId (any ());
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (java.util.Optional.of (categoryBuilder ()));
            when (filmCategoryRepository.save (any ())).thenReturn (filmCategoryBuilder (patchedFilm));
            when (filmMapper.toFilmResponseDto (patchedFilm)).thenReturn (expectedResponse);

            FilmResponseDto actualResponse = filmService.partialUpdate (FILM_ID, requestDto);

            /* Then */
            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void PartialUpdate_withNullRequest_ShouldThrowIllegalArgumentException () {
            /* Given */
            FilmRequestDto requestDto = null;

            /* When and Then */
            assertThatThrownBy (() -> filmService.partialUpdate (FILM_ID, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Request cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when Film ID is null")
        void PartialUpdate_withNullId_ShouldThrowIllegalArgumentException () {
            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();

            /* When and Then */
            assertThatThrownBy (() -> filmService.partialUpdate (null, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Film ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when partially updating non-existent film")
        void PartialUpdate_withNonExistentId_ShouldThrowResourceNotFoundException () {
            /* Given */
            FilmRequestDto requestDto = filmRequestDtoBuilder ();
            Integer nonExistentFilmId = 999;

            /* When */
            when (filmRepository.findById (nonExistentFilmId)).thenReturn (java.util.Optional.empty ());

            /* Then */
            assertThatThrownBy (() -> filmService.partialUpdate (nonExistentFilmId, requestDto))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("Should not replace actors/categories when partial request omits relationship fields")
        void PartialUpdate_withoutRelationshipFields_ShouldSkipReplaceActorsAndCategories () {
            /* Given */
            FilmRequestDto requestDto = new FilmRequestDto (
                    null,
                    "Only Title Patch",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    NOW,
                    null,
                    null,
                    null,
                    null
            );
            Film existingFilm = filmBuilder ();
            Film patchedFilm = filmBuilder ();
            FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

            /* When */
            when (filmRepository.findById (FILM_ID))
                    .thenReturn (java.util.Optional.of (existingFilm))
                    .thenReturn (java.util.Optional.of (patchedFilm));
            when (filmRepository.save (any ())).thenReturn (patchedFilm);
            when (filmMapper.toFilmResponseDto (patchedFilm)).thenReturn (expectedResponse);

            FilmResponseDto actualResponse = filmService.partialUpdate (FILM_ID, requestDto);

            /* Then */
            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);
            verify (filmActorRepository, never ()).deleteByFilmId (any ());
            verify (filmCategoryRepository, never ()).deleteByFilmId (any ());
        }
    }


    /* ══════════════════════════════════════════════════════════════════════
       findById()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findById() — Find a film by ID")
    class FindByIdTests {

        @Test
        @DisplayName ("Should find an existing Film by ID successfully")
        void FindById_withValidId_ShouldReturnFilm () {
            /* Given */
            Film existingFilm = filmBuilder ();
            FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

            /* When */
            when (filmRepository.findById (FILM_ID)).thenReturn (java.util.Optional.of (existingFilm));
            when (filmMapper.toFilmResponseDto (existingFilm)).thenReturn (expectedResponse);

            FilmResponseDto actualResponse = filmService.findById (FILM_ID);

            /* Then */
            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void FindById_withNullId_ShouldThrowIllegalArgumentException () {
            /* Given */
            Integer id = null;

            /* When and Then */
            assertThatThrownBy (() -> filmService.findById (id))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Film ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when film ID is not found")
        void FindById_withNonExistentId_ShouldThrowResourceNotFoundException () {
            /* Given */
            Integer nonExistentId = 999;

            /* When */
            when (filmRepository.findById (nonExistentId)).thenReturn (java.util.Optional.empty ());

            /* Then */
            assertThatThrownBy (() -> filmService.findById (nonExistentId))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findAll() — unpaged
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findAll() — Find all Film (unpaged)")
    class FindAllActorsUnpaged {

        @Test
        @DisplayName ("Should find all Film successfully")
        void FindAll_withValidRequest_ShouldReturnAllFilm () {
            /* Given */
            List<Film> existingFilms = List.of (filmBuilder (), filmBuilder ());
            List<FilmResponseDto> expectedResponses = List.of (filmResponseDtoBuilder (), filmResponseDtoBuilder ());

            /* When */
            when (filmRepository.findAll ()).thenReturn (existingFilms);
            when (filmMapper.toFilmResponseDto (existingFilms.get (0))).thenReturn (expectedResponses.get (0));
            when (filmMapper.toFilmResponseDto (existingFilms.get (1))).thenReturn (expectedResponses.get (1));

            List<FilmResponseDto> actualResponses = filmService.findAll ();

            /* Then */
            assertThat (actualResponses).usingRecursiveComparison ().isEqualTo (expectedResponses);
        }

        @Test
        @DisplayName ("Should return empty list when no films in repository")
        void FindAll_withNoFilms_ShouldReturnEmptyList () {
            /* Given */
            List<Film> existingFilms = List.of ();
            List<FilmResponseDto> expectedResponses = List.of ();

            /* When */
            when (filmRepository.findAll ()).thenReturn (existingFilms);

            List<FilmResponseDto> actualResponses = filmService.findAll ();

            /* Then */
            assertThat (actualResponses).usingRecursiveComparison ().isEqualTo (expectedResponses);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findAll(Pageable)
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findAll(Pageable) — Find all films (paged)")
    class FindAllFilmsPaged {

        @Test
        @DisplayName ("Should find all films successfully with pagination")
        void FindAll_withValidRequest_ShouldReturnPagedFilms () {
            /* Given */
            Pageable pageable = Pageable.ofSize (10);
            Page<Film> existingFilms = new PageImpl<> (List.of (filmBuilder (), filmBuilder ()));
            Page<FilmResponseDto> expectedResponses = new PageImpl<> (List.of (filmResponseDtoBuilder (), filmResponseDtoBuilder ()));

            /* When */
            when (filmRepository.findAll (pageable)).thenReturn (existingFilms);
            when (filmMapper.toFilmResponseDto (existingFilms.getContent ().get (0))).thenReturn (expectedResponses.getContent ().get (0));
            when (filmMapper.toFilmResponseDto (existingFilms.getContent ().get (1))).thenReturn (expectedResponses.getContent ().get (1));

            Page<FilmResponseDto> actualResponses = filmService.findAll (pageable);

            /* Then */
            assertThat (actualResponses).usingRecursiveComparison ().isEqualTo (expectedResponses);
        }

        @Test
        @DisplayName ("Should return empty page when no films in repository")
        void FindAll_withNoFilms_ShouldReturnEmptyPage () {
            /* Given */
            Pageable pageable = Pageable.ofSize (10);
            Page<Film> existingFilms = new PageImpl<> (List.of ());
            Page<FilmResponseDto> expectedResponses = new PageImpl<> (List.of ());

            /* When */
            when (filmRepository.findAll (pageable)).thenReturn (existingFilms);

            Page<FilmResponseDto> actualResponses = filmService.findAll (pageable);

            /* Then */
            assertThat (actualResponses).usingRecursiveComparison ().isEqualTo (expectedResponses);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       delete()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("delete() — Delete a film by ID")
    class DeleteFilmById {

        @Test
        @DisplayName ("Should delete an existing film by ID successfully")
        void Delete_withValidId_ShouldDeleteFilmById () {
            /* Given */
            Integer filmId = FILM_ID;

            /* When */
            when (filmRepository.existsById (filmId)).thenReturn (true);
            doNothing ().when (filmActorRepository).deleteByFilmId (filmId);
            doNothing ().when (filmCategoryRepository).deleteByFilmId (filmId);
            doNothing ().when (filmRepository).deleteById (filmId);

            filmService.delete (filmId);

            /* Then */
            verify (filmRepository).existsById (filmId);
            verify (filmActorRepository).deleteByFilmId (filmId);
            verify (filmCategoryRepository).deleteByFilmId (filmId);
            verify (filmRepository).deleteById (filmId);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void Delete_withNullId_ShouldThrowIllegalArgumentException () {
            /* Given */
            Integer nullId = null;

            /* When / Then */
            assertThatThrownBy (() -> filmService.delete (nullId))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Film ID must not be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when film does not exist")
        void Delete_withNonExistentId_ShouldThrowResourceNotFoundException () {
            /* Given */
            Integer nonExistentId = 999;

            /* When */
            when (filmRepository.existsById (nonExistentId)).thenReturn (false);

            /* Then */
            assertThatThrownBy (() -> filmService.delete (nonExistentId))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       existsById()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("existsById() — Check if a film exists by ID")
    class ExistsFilmById {

        @Test
        @DisplayName ("Should return true when a film exists by ID")
        void Exists_withValidId_ShouldReturnTrue () {
            /* Given */
            Integer filmId = FILM_ID;

            /* When */
            when (filmRepository.existsById (filmId)).thenReturn (true);

            boolean exists = filmService.existsById (filmId);

            /* Then */
            assertThat (exists).isTrue ();
        }

        @Test
        @DisplayName ("Should return false when a film does not exist by ID")
        void Exists_withNonExistentId_ShouldReturnFalse () {
            /* Given */
            Integer nonExistentId = 999;

            /* When */
            when (filmRepository.existsById (nonExistentId)).thenReturn (false);

            boolean exists = filmService.existsById (nonExistentId);

            /* Then */
            assertThat (exists).isFalse ();
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void Exists_withNullId_ShouldThrowIllegalArgumentException () {
            /* Given */
            Integer nullId = null;

            /* When / Then */
            assertThatThrownBy (() -> filmService.existsById (nullId))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Film ID must not be null");
        }
    }

     /* ══════════════════════════════════════════════════════════════════════
       count()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("count() — Count total number of films")
    class CountFilms {

        @Test
        @DisplayName ("Should return the total number of films")
        void Count_ShouldReturnTotalNumberOfFilms () {
            /* Given */
            long expectedCount = 10;

            /* When */
            when (filmRepository.count ()).thenReturn (expectedCount);

            long actualCount = filmService.count ();

            /* Then */
            assertThat (actualCount).isEqualTo (expectedCount);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when count is negative")
        void Count_withNegativeCount_ShouldThrowIllegalArgumentException () {
            /* Given */
            long negativeCount = -1;

            /* When */
            when (filmRepository.count ()).thenReturn (negativeCount);

            /* Then */
            assertThatThrownBy (filmService::count)
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Count must not be negative");
        }
    }

    @Test
    @DisplayName ("Tests for Merge Actors Ids Successfully")
    void mergeActors_Successfully () {
        /* Given — test the merged result by exercising it through partialUpdate which uses mergeActorIds */
        Integer actor1Id = 1;
        Integer actor2Id = 2;
        Integer actor3Id = 3;

        ActorRequestDto newActorDto1 = new ActorRequestDto (null, "New", "Actor1", NOW);
        ActorRequestDto newActorDto2 = new ActorRequestDto (null, "New", "Actor2", NOW);

        ActorResponseDto newActor1 = new ActorResponseDto (actor3Id, "New", "Actor1", NOW);
        ActorResponseDto newActor2 = new ActorResponseDto (actor3Id, "New", "Actor2", NOW);

        FilmRequestDto requestDto = new FilmRequestDto (
                null,
                "Test Movie",
                "A test movie description.",
                (short) 2020,
                (short) 1,
                null,
                (short) 3,
                BigDecimal.valueOf (4.99),
                (short) 120,
                BigDecimal.valueOf (19.99),
                "PG-13",
                "Trailers,Commentaries",
                NOW,
                List.of (actor1Id, actor2Id),  // existing actor IDs
                List.of (CATEGORY_ID),
                List.of (newActorDto1, newActorDto2),  // inline actors to create
                List.of ()
        );

        Film existingFilm = filmBuilder ();
        Film patchedFilm = filmBuilder ();
        FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

        /* When */
        when (filmRepository.findById (FILM_ID))
                .thenReturn (java.util.Optional.of (existingFilm))
                .thenReturn (java.util.Optional.of (patchedFilm));
        when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
        when (filmRepository.save (any ())).thenReturn (patchedFilm);
        // Stub inline actor creation (mergeActorIds calls actorService.create for inline actors)
        when (actorService.create (newActorDto1)).thenReturn (newActor1);
        when (actorService.create (newActorDto2)).thenReturn (newActor2);
        // Stub actor/film-actor interactions
        doNothing ().when (filmActorRepository).deleteByFilmId (any ());
        when (actorRepository.findById (actor1Id)).thenReturn (java.util.Optional.of (new Actor ()));
        when (actorRepository.findById (actor2Id)).thenReturn (java.util.Optional.of (new Actor ()));
        when (actorRepository.findById (actor3Id)).thenReturn (java.util.Optional.of (new Actor ()));
        when (filmActorRepository.save (any ())).thenReturn (new FilmActor ());
        // Stub categories
        doNothing ().when (filmCategoryRepository).deleteByFilmId (any ());
        when (categoryRepository.findById (CATEGORY_ID)).thenReturn (java.util.Optional.of (categoryBuilder ()));
        when (filmCategoryRepository.save (any ())).thenReturn (new FilmCategory ());
        when (filmMapper.toFilmResponseDto (patchedFilm)).thenReturn (expectedResponse);

        /* Then — execute partialUpdate indirectly tests mergeActorIds */
        FilmResponseDto actualResponse = filmService.partialUpdate (FILM_ID, requestDto);
        assertThat (actualResponse).isNotNull ();
    }

    @Test
    @DisplayName ("Merge Category Ids Successfully")
    void mergeCategoryIds_Successfully () {
        /* Given — test the merged result by exercising it through partialUpdate which uses mergeCategoryIds */
        Short category1Id = 1;
        Short category2Id = 2;
        Short category3Id = 3;

        CategoryRequestDto newCategoryDto1 = new CategoryRequestDto (null, "New Category 1", NOW);
        CategoryRequestDto newCategoryDto2 = new CategoryRequestDto (null, "New Category 2", NOW);

        CategoryResponseDto newCategory1 = new CategoryResponseDto (category3Id, "New Category 1", NOW);
        CategoryResponseDto newCategory2 = new CategoryResponseDto (category3Id, "New Category 2", NOW);

        FilmRequestDto requestDto = new FilmRequestDto (
                null,
                "Test Movie",
                "A test movie description.",
                (short) 2020,
                (short) 1,
                null,
                (short) 3,
                BigDecimal.valueOf (4.99),
                (short) 120,
                BigDecimal.valueOf (19.99),
                "PG-13",
                "Trailers,Commentaries",
                NOW,
                List.of (ACTOR_ID),
                List.of (category1Id, category2Id),  // existing category IDs
                List.of (),
                List.of (newCategoryDto1, newCategoryDto2)  // inline categories to create
        );

        Film existingFilm = filmBuilder ();
        Film patchedFilm = filmBuilder ();
        FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

        /* When */
        when (filmRepository.findById (FILM_ID))
                .thenReturn (java.util.Optional.of (existingFilm))
                .thenReturn (java.util.Optional.of (patchedFilm));
        when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
        when (filmRepository.save (any ())).thenReturn (patchedFilm);
        // Stub inline category creation (mergeCategoryIds calls categoryService.create for inline categories)
        when (categoryService.create (newCategoryDto1)).thenReturn (newCategory1);
        when (categoryService.create (newCategoryDto2)).thenReturn (newCategory2);
        // Stub actor/film-actor interactions
        doNothing ().when (filmActorRepository).deleteByFilmId (any ());
        when (actorRepository.findById (ACTOR_ID)).thenReturn (java.util.Optional.of (new Actor ()));
        when (filmActorRepository.save (any ())).thenReturn (new FilmActor ());
        // Stub category/film-category interactions
        doNothing ().when (filmCategoryRepository).deleteByFilmId (any ());
        when (categoryRepository.findById (category1Id)).thenReturn (java.util.Optional.of (new Category ()));
        when (categoryRepository.findById (category2Id)).thenReturn (java.util.Optional.of (new Category ()));
        when (categoryRepository.findById (category3Id)).thenReturn (java.util.Optional.of (new Category ()));
        when (filmCategoryRepository.save (any ())).thenReturn (new FilmCategory ());
        when (filmMapper.toFilmResponseDto (patchedFilm)).thenReturn (expectedResponse);

        /* Then — execute partialUpdate indirectly tests mergeCategoryIds */
        FilmResponseDto actualResponse = filmService.partialUpdate (FILM_ID, requestDto);
        assertThat (actualResponse).isNotNull ();
    }

    @Test
    @DisplayName ("Resolve Original Language")
    void resolve_Original_Language () {
        /* Given — originalLanguageId is not null; should resolve to a language */
        Short originalLanguageId = 2;
        Language originalLanguage = new Language ();
        originalLanguage.setId (originalLanguageId);
        originalLanguage.setName ("French");
        originalLanguage.setLastUpdate (NOW);

        //FilmRequestDto requestDto = filmRequestDtoBuilder ();
        Film existingFilm = filmBuilder ();
        Film patchedFilm = filmBuilder ();
        FilmResponseDto expectedResponse = filmResponseDtoBuilder ();

        /* When */
        when (filmRepository.findById (FILM_ID))
                .thenReturn (java.util.Optional.of (existingFilm))
                .thenReturn (java.util.Optional.of (patchedFilm));
        // Stub language resolution (resolveOriginalLanguage is called in partialUpdate)
        when (languageRepository.findById ((short) 1)).thenReturn (java.util.Optional.of (languageBuilder ()));
        when (languageRepository.findById (originalLanguageId)).thenReturn (java.util.Optional.of (originalLanguage));
        when (filmRepository.save (any ())).thenReturn (patchedFilm);
        doNothing ().when (filmActorRepository).deleteByFilmId (any ());
        when (actorRepository.findById (ACTOR_ID)).thenReturn (java.util.Optional.of (new Actor ()));
        when (filmActorRepository.save (any ())).thenReturn (new FilmActor ());
        doNothing ().when (filmCategoryRepository).deleteByFilmId (any ());
        when (categoryRepository.findById (CATEGORY_ID)).thenReturn (java.util.Optional.of (new Category ()));
        when (filmCategoryRepository.save (any ())).thenReturn (new FilmCategory ());
        when (filmMapper.toFilmResponseDto (patchedFilm)).thenReturn (expectedResponse);

        // Create a custom request with originalLanguageId set
        FilmRequestDto customRequest = new FilmRequestDto (
                null,
                "Test Movie",
                "A test movie description.",
                (short) 2020,
                (short) 1,
                originalLanguageId,  // originalLanguageId is provided
                (short) 3,
                BigDecimal.valueOf (4.99),
                (short) 120,
                BigDecimal.valueOf (19.99),
                "PG-13",
                "Trailers,Commentaries",
                NOW,
                List.of (ACTOR_ID),
                List.of (CATEGORY_ID),
                List.of (),
                List.of ()
        );

        /* Then — execute partialUpdate which calls resolveOriginalLanguage */
        FilmResponseDto actualResponse = filmService.partialUpdate (FILM_ID, customRequest);
        assertThat (actualResponse).isNotNull ();
    }
}
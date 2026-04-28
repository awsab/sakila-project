package com.me.learning.parent.inventoryservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.inventoryservice.dto.request.FilmRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.FilmResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.FilmUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.FilmMapper;
import com.me.learning.parent.inventoryservice.model.Film;
import com.me.learning.parent.inventoryservice.repository.FilmRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Unit tests for FilmServiceImpl
 * Since    : Version 1.0
 */
@DisplayName ("FilmServiceImpl Unit Tests")
@ExtendWith (MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmServiceImpl filmService;

    private Film           film;
    private FilmRequestDTO  requestDTO;
    private FilmResponseDTO responseDTO;
    private FilmUpdateDTO   updateDTO;

    @BeforeEach
    void setUp () {
        film = new Film ();
        film.setId (1);
        film.setTitle ("ACADEMY DINOSAUR");
        film.setDescription ("Epic Drama");
        film.setReleaseYear (2006);
        film.setRentalDuration ((short) 6);
        film.setRentalRate (new BigDecimal ("0.99"));
        film.setLength (86);
        film.setReplacementCost (new BigDecimal ("20.99"));
        film.setRating ("PG");

        requestDTO = FilmRequestDTO.builder ()
                .title ("ACADEMY DINOSAUR")
                .description ("Epic Drama")
                .releaseYear (2006)
                .rentalDuration ((short) 6)
                .rentalRate (new BigDecimal ("0.99"))
                .length (86)
                .replacementCost (new BigDecimal ("20.99"))
                .rating ("PG")
                .build ();

        responseDTO = FilmResponseDTO.builder ()
                .id (1)
                .title ("ACADEMY DINOSAUR")
                .description ("Epic Drama")
                .releaseYear (2006)
                .rentalDuration ((short) 6)
                .rentalRate (new BigDecimal ("0.99"))
                .length (86)
                .replacementCost (new BigDecimal ("20.99"))
                .rating ("PG")
                .lastUpdate (Instant.now ())
                .build ();

        updateDTO = FilmUpdateDTO.builder ()
                .id (1)
                .title ("ACE GOLDFINGER")
                .description ("Silly Comedy")
                .releaseYear (2006)
                .rentalDuration ((short) 3)
                .rentalRate (new BigDecimal ("4.99"))
                .length (48)
                .replacementCost (new BigDecimal ("12.99"))
                .rating ("G")
                .build ();
    }

    // ─────────────────────────────────── createFilm ────────────────────────────────────

    @Nested
    @DisplayName ("createFilm")
    /* default */ class CreateFilm {

        @Test
        @DisplayName ("should create film and return response when title does not already exist")
        void shouldCreateFilmSuccessfully () {
            when (filmRepository.existsByTitle ("ACADEMY DINOSAUR")).thenReturn (false);
            when (filmMapper.toEntity (requestDTO)).thenReturn (film);
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            FilmResponseDTO result = filmService.createFilm (requestDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getTitle ()).isEqualTo ("ACADEMY DINOSAUR");
            assertThat (result.getRating ()).isEqualTo ("PG");
            verify (filmRepository, times (1)).save (film);
        }

        @Test
        @DisplayName ("should invoke mapper toEntity and toDto during successful creation")
        void shouldInvokeMapperDuringCreation () {
            when (filmRepository.existsByTitle ("ACADEMY DINOSAUR")).thenReturn (false);
            when (filmMapper.toEntity (requestDTO)).thenReturn (film);
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            filmService.createFilm (requestDTO);

            verify (filmMapper).toEntity (requestDTO);
            verify (filmMapper).toDto (film);
        }

        @Test
        @DisplayName ("should throw IllegalArgumentException when film title already exists")
        void shouldThrowIllegalArgWhenTitleExists () {
            when (filmRepository.existsByTitle ("ACADEMY DINOSAUR")).thenReturn (true);

            assertThatThrownBy (() -> filmService.createFilm (requestDTO))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessageContaining ("ACADEMY DINOSAUR");

            verify (filmRepository, never ()).save (any ());
            verify (filmMapper, never ()).toEntity (any ());
        }

        @Test
        @DisplayName ("should not call repository save when duplicate title is detected")
        void shouldNotSaveWhenDuplicateTitleDetected () {
            when (filmRepository.existsByTitle (any ())).thenReturn (true);

            assertThatThrownBy (() -> filmService.createFilm (requestDTO))
                    .isInstanceOf (IllegalArgumentException.class);

            verify (filmRepository, never ()).save (any (Film.class));
        }
    }

    // ─────────────────────────────────── updateFilm ────────────────────────────────────

    @Nested
    @DisplayName ("updateFilm")
    /* default */ class UpdateFilm {

        @Test
        @DisplayName ("should update film and return updated response when no title conflict")
        void shouldUpdateFilmSuccessfully () {
            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmRepository.existsByTitle ("ACE GOLDFINGER")).thenReturn (false);
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (
                    FilmResponseDTO.builder ().id (1).title ("ACE GOLDFINGER").rating ("G").build ());

            FilmResponseDTO result = filmService.updateFilm (1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getTitle ()).isEqualTo ("ACE GOLDFINGER");
            assertThat (result.getRating ()).isEqualTo ("G");
        }

        @Test
        @DisplayName ("should apply all update fields to entity before saving")
        void shouldApplyAllFieldsBeforeSave () {
            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmRepository.existsByTitle ("ACE GOLDFINGER")).thenReturn (false);
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            filmService.updateFilm (1, updateDTO);

            assertThat (film.getTitle ()).isEqualTo ("ACE GOLDFINGER");
            assertThat (film.getDescription ()).isEqualTo ("Silly Comedy");
            assertThat (film.getRating ()).isEqualTo ("G");
            assertThat (film.getRentalRate ()).isEqualByComparingTo ("4.99");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when film ID does not exist")
        void shouldThrowNotFoundWhenFilmMissing () {
            when (filmRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> filmService.updateFilm (99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Film");

            verify (filmRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when new title conflicts with existing film")
        void shouldThrowDuplicateExceptionOnTitleConflict () {
            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmRepository.existsByTitle ("ACE GOLDFINGER")).thenReturn (true);

            assertThatThrownBy (() -> filmService.updateFilm (1, updateDTO))
                    .isInstanceOf (DuplicateResourceException.class)
                    .hasMessageContaining ("Film");

            verify (filmRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should skip duplicate check when film title remains unchanged")
        void shouldSkipDuplicateCheckWhenTitleUnchanged () {
            FilmUpdateDTO sameTitle = FilmUpdateDTO.builder ()
                    .id (1).title ("ACADEMY DINOSAUR").description ("updated desc").build ();

            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            FilmResponseDTO result = filmService.updateFilm (1, sameTitle);

            assertThat (result).isNotNull ();
            verify (filmRepository, never ()).existsByTitle (any ());
        }
    }

    // ─────────────────────────────────── patchFilm ─────────────────────────────────────

    @Nested
    @DisplayName ("patchFilm")
    /* default */ class PatchFilm {

        @Test
        @DisplayName ("should patch film successfully and return updated response")
        void shouldPatchFilmSuccessfully () {
            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            FilmResponseDTO result = filmService.patchFilm (1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should delegate field mapping to filmMapper.updateEntity during patch")
        void shouldDelegateToMapperUpdateEntity () {
            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmRepository.save (film)).thenReturn (film);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            filmService.patchFilm (1, updateDTO);

            verify (filmMapper).updateEntity (updateDTO, film);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when film not found during patch")
        void shouldThrowNotFoundWhenFilmMissingForPatch () {
            when (filmRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> filmService.patchFilm (99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Film");

            verify (filmRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────── getFilmById ───────────────────────────────────

    @Nested
    @DisplayName ("getFilmById")
    /* default */ class GetFilmById {

        @Test
        @DisplayName ("should return film response when film exists for given ID")
        void shouldReturnFilmById () {
            when (filmRepository.findById (1)).thenReturn (Optional.of (film));
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            FilmResponseDTO result = filmService.getFilmById (1);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getTitle ()).isEqualTo ("ACADEMY DINOSAUR");
            assertThat (result.getRating ()).isEqualTo ("PG");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no film found for given ID")
        void shouldThrowNotFoundWhenFilmMissingById () {
            when (filmRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> filmService.getFilmById (99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Film");
        }
    }

    // ─────────────────────────────────── getFilmByTitle ────────────────────────────────

    @Nested
    @DisplayName ("getFilmByTitle")
    /* default */ class GetFilmByTitle {

        @Test
        @DisplayName ("should return film response when film exists for given title")
        void shouldReturnFilmByTitle () {
            when (filmRepository.findByTitle ("ACADEMY DINOSAUR")).thenReturn (Optional.of (film));
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            FilmResponseDTO result = filmService.getFilmByTitle ("ACADEMY DINOSAUR");

            assertThat (result).isNotNull ();
            assertThat (result.getTitle ()).isEqualTo ("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no film found for given title")
        void shouldThrowNotFoundWhenFilmMissingByTitle () {
            when (filmRepository.findByTitle ("UNKNOWN")).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> filmService.getFilmByTitle ("UNKNOWN"))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Film");
        }
    }

    // ─────────────────────────────────── getAllFilms ────────────────────────────────────

    @Nested
    @DisplayName ("getAllFilms")
    /* default */ class GetAllFilms {

        @Test
        @DisplayName ("should return list with all films when films exist")
        void shouldReturnAllFilms () {
            when (filmRepository.findAll ()).thenReturn (List.of (film));
            when (filmMapper.toDtoList (List.of (film))).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getAllFilms ();

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getTitle ()).isEqualTo ("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName ("should return empty list when no films exist")
        void shouldReturnEmptyListWhenNoFilms () {
            when (filmRepository.findAll ()).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (Collections.emptyList ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getAllFilms ()).isNotNull ().isEmpty ();
        }

        @Test
        @DisplayName ("should return paginated films for given pageable")
        void shouldReturnPaginatedFilms () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Film> page = new PageImpl<> (List.of (film), pageable, 1);
            when (filmRepository.findAll (pageable)).thenReturn (page);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            Page<FilmResponseDTO> result = filmService.getAllFilms (pageable);

            assertThat (result).isNotNull ();
            assertThat (result.getTotalElements ()).isEqualTo (1);
            assertThat (result.getContent ()).hasSize (1);
            assertThat (result.getContent ().get (0).getTitle ()).isEqualTo ("ACADEMY DINOSAUR");
        }

        @Test
        @DisplayName ("should return empty page when no films match pageable criteria")
        void shouldReturnEmptyPage () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Film> emptyPage = new PageImpl<> (Collections.emptyList (), pageable, 0);
            when (filmRepository.findAll (pageable)).thenReturn (emptyPage);

            Page<FilmResponseDTO> result = filmService.getAllFilms (pageable);

            assertThat (result.getTotalElements ()).isZero ();
            assertThat (result.getContent ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── searchFilmsByTitle ────────────────────────────

    @Nested
    @DisplayName ("searchFilmsByTitle")
    /* default */ class SearchFilmsByTitle {

        @Test
        @DisplayName ("should return films whose title contains the search term (case-insensitive)")
        void shouldReturnMatchingFilms () {
            when (filmRepository.findByTitleContainingIgnoreCase ("acad")).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.searchFilmsByTitle ("acad");

            assertThat (result).isNotNull ().hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when no films match title search")
        void shouldReturnEmptyWhenNoTitleMatch () {
            when (filmRepository.findByTitleContainingIgnoreCase ("xyz")).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.searchFilmsByTitle ("xyz")).isEmpty ();
        }
    }

    // ─────────────────────────────────── searchFilmsByTitleOrDescription ───────────────

    @Nested
    @DisplayName ("searchFilmsByTitleOrDescription")
    /* default */ class SearchFilmsByTitleOrDescription {

        @Test
        @DisplayName ("should return films matching title or description for search term")
        void shouldReturnMatchingFilms () {
            when (filmRepository.searchByTitleOrDescription ("epic")).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            assertThat (filmService.searchFilmsByTitleOrDescription ("epic")).hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when no films match search term in title or description")
        void shouldReturnEmptyWhenNoMatch () {
            when (filmRepository.searchByTitleOrDescription ("zzz")).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.searchFilmsByTitleOrDescription ("zzz")).isEmpty ();
        }
    }

    // ─────────────────────────────────── getFilmsByReleaseYear ─────────────────────────

    @Nested
    @DisplayName ("getFilmsByReleaseYear")
    /* default */ class GetFilmsByReleaseYear {

        @Test
        @DisplayName ("should return films for the given release year")
        void shouldReturnFilmsForReleaseYear () {
            when (filmRepository.findByReleaseYear (2006)).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getFilmsByReleaseYear (2006);

            assertThat (result).hasSize (1);
            assertThat (result.get (0).getReleaseYear ()).isEqualTo (2006);
        }

        @Test
        @DisplayName ("should return empty list when no films exist for given release year")
        void shouldReturnEmptyWhenYearHasNoFilms () {
            when (filmRepository.findByReleaseYear (1900)).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getFilmsByReleaseYear (1900)).isEmpty ();
        }
    }

    // ─────────────────────────────────── getFilmsByRating ──────────────────────────────

    @Nested
    @DisplayName ("getFilmsByRating")
    /* default */ class GetFilmsByRating {

        @Test
        @DisplayName ("should return films with matching rating (list)")
        void shouldReturnFilmsByRating () {
            when (filmRepository.findByRating ("PG")).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getFilmsByRating ("PG");

            assertThat (result).hasSize (1);
            assertThat (result.get (0).getRating ()).isEqualTo ("PG");
        }

        @Test
        @DisplayName ("should return paginated films with matching rating")
        void shouldReturnPaginatedFilmsByRating () {
            Pageable pageable = PageRequest.of (0, 5);
            Page<Film> page = new PageImpl<> (List.of (film), pageable, 1);
            when (filmRepository.findByRating ("PG", pageable)).thenReturn (page);
            when (filmMapper.toDto (film)).thenReturn (responseDTO);

            Page<FilmResponseDTO> result = filmService.getFilmsByRating ("PG", pageable);

            assertThat (result.getTotalElements ()).isEqualTo (1);
            assertThat (result.getContent ().get (0).getRating ()).isEqualTo ("PG");
        }

        @Test
        @DisplayName ("should return empty list when no films match the given rating")
        void shouldReturnEmptyWhenRatingHasNoFilms () {
            when (filmRepository.findByRating ("NC-17")).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getFilmsByRating ("NC-17")).isEmpty ();
        }
    }

    // ─────────────────────────────────── getFilmsByRentalRate ──────────────────────────

    @Nested
    @DisplayName ("getFilmsByRentalRate")
    /* default */ class GetFilmsByRentalRate {

        @Test
        @DisplayName ("should return films with rental rate at or below the given max rate")
        void shouldReturnFilmsWithinRentalRate () {
            BigDecimal maxRate = new BigDecimal ("2.99");
            when (filmRepository.findByRentalRateLessThanEqual (maxRate)).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getFilmsByRentalRate (maxRate);

            assertThat (result).hasSize (1);
            assertThat (result.get (0).getRentalRate ()).isLessThanOrEqualTo (maxRate);
        }

        @Test
        @DisplayName ("should return empty list when no films fall within the given rental rate")
        void shouldReturnEmptyWhenNoFilmsWithinRate () {
            BigDecimal maxRate = new BigDecimal ("0.01");
            when (filmRepository.findByRentalRateLessThanEqual (maxRate)).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getFilmsByRentalRate (maxRate)).isEmpty ();
        }
    }

    // ─────────────────────────────────── getFilmsByLengthRange ─────────────────────────

    @Nested
    @DisplayName ("getFilmsByLengthRange")
    /* default */ class GetFilmsByLengthRange {

        @Test
        @DisplayName ("should return films within the given length range")
        void shouldReturnFilmsInLengthRange () {
            when (filmRepository.findByLengthBetween (60, 120)).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getFilmsByLengthRange (60, 120);

            assertThat (result).hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when no films fall within the given length range")
        void shouldReturnEmptyWhenNoFilmsInRange () {
            when (filmRepository.findByLengthBetween (200, 300)).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getFilmsByLengthRange (200, 300)).isEmpty ();
        }
    }

    // ─────────────────────────────────── getFilmsByCategoryId ──────────────────────────

    @Nested
    @DisplayName ("getFilmsByCategoryId")
    /* default */ class GetFilmsByCategoryId {

        @Test
        @DisplayName ("should return films associated with the given category ID")
        void shouldReturnFilmsForCategory () {
            when (filmRepository.findByCategoryId ((short) 1)).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getFilmsByCategoryId ((short) 1);

            assertThat (result).isNotNull ().hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when category has no films")
        void shouldReturnEmptyWhenCategoryHasNoFilms () {
            when (filmRepository.findByCategoryId ((short) 99)).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getFilmsByCategoryId ((short) 99)).isEmpty ();
        }
    }

    // ─────────────────────────────────── getFilmsByActorId ─────────────────────────────

    @Nested
    @DisplayName ("getFilmsByActorId")
    /* default */ class GetFilmsByActorId {

        @Test
        @DisplayName ("should return films associated with the given actor ID")
        void shouldReturnFilmsForActor () {
            when (filmRepository.findByActorId (1)).thenReturn (List.of (film));
            when (filmMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<FilmResponseDTO> result = filmService.getFilmsByActorId (1);

            assertThat (result).isNotNull ().hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when actor has no associated films")
        void shouldReturnEmptyWhenActorHasNoFilms () {
            when (filmRepository.findByActorId (99)).thenReturn (Collections.emptyList ());
            when (filmMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (filmService.getFilmsByActorId (99)).isEmpty ();
        }
    }

    // ─────────────────────────────────── countFilmsByRating ────────────────────────────

    @Nested
    @DisplayName ("countFilmsByRating")
    /* default */ class CountFilmsByRating {

        @Test
        @DisplayName ("should return count of films with the given rating")
        void shouldReturnFilmCountForRating () {
            when (filmRepository.countByRating ("PG")).thenReturn (194L);

            assertThat (filmService.countFilmsByRating ("PG")).isEqualTo (194L);
        }

        @Test
        @DisplayName ("should return zero when no films with the given rating exist")
        void shouldReturnZeroWhenRatingHasNoFilms () {
            when (filmRepository.countByRating ("X")).thenReturn (0L);

            assertThat (filmService.countFilmsByRating ("X")).isZero ();
        }
    }

    // ─────────────────────────────────── existsById ────────────────────────────────────

    @Nested
    @DisplayName ("existsById")
    /* default */ class ExistsById {

        @Test
        @DisplayName ("should return true when film exists with the given ID")
        void shouldReturnTrueWhenFilmExists () {
            when (filmRepository.existsById (1)).thenReturn (true);

            assertThat (filmService.existsById (1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when film does not exist with the given ID")
        void shouldReturnFalseWhenFilmNotExists () {
            when (filmRepository.existsById (99)).thenReturn (false);

            assertThat (filmService.existsById (99)).isFalse ();
        }
    }

    // ─────────────────────────────────── existsByTitle ─────────────────────────────────

    @Nested
    @DisplayName ("existsByTitle")
    /* default */ class ExistsByTitle {

        @Test
        @DisplayName ("should return true when film with given title exists")
        void shouldReturnTrueWhenTitleExists () {
            when (filmRepository.existsByTitle ("ACADEMY DINOSAUR")).thenReturn (true);

            assertThat (filmService.existsByTitle ("ACADEMY DINOSAUR")).isTrue ();
        }

        @Test
        @DisplayName ("should return false when no film found with given title")
        void shouldReturnFalseWhenTitleNotFound () {
            when (filmRepository.existsByTitle ("UNKNOWN")).thenReturn (false);

            assertThat (filmService.existsByTitle ("UNKNOWN")).isFalse ();
        }
    }

    // ─────────────────────────────────── deleteFilm ────────────────────────────────────

    @Nested
    @DisplayName ("deleteFilm")
    /* default */ class DeleteFilm {

        @Test
        @DisplayName ("should delete film without throwing exception when film exists")
        void shouldDeleteFilmSuccessfully () {
            when (filmRepository.existsById (1)).thenReturn (true);

            assertThatCode (() -> filmService.deleteFilm (1)).doesNotThrowAnyException ();
            verify (filmRepository, times (1)).deleteById (1);
        }

        @Test
        @DisplayName ("should invoke deleteById exactly once when film is found")
        void shouldCallDeleteByIdExactlyOnce () {
            when (filmRepository.existsById (1)).thenReturn (true);

            filmService.deleteFilm (1);

            verify (filmRepository, times (1)).deleteById (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when film not found for delete")
        void shouldThrowNotFoundWhenFilmNotExistsForDelete () {
            when (filmRepository.existsById (99)).thenReturn (false);

            assertThatThrownBy (() -> filmService.deleteFilm (99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Film");

            verify (filmRepository, never ()).deleteById (any ());
        }

        @Test
        @DisplayName ("should not call deleteById when film does not exist")
        void shouldNotCallDeleteByIdWhenFilmNotFound () {
            when (filmRepository.existsById (any ())).thenReturn (false);

            assertThatThrownBy (() -> filmService.deleteFilm (5))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (filmRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────── countFilms ────────────────────────────────────

    @Nested
    @DisplayName ("countFilms")
    /* default */ class CountFilms {

        @Test
        @DisplayName ("should return total film count from repository")
        void shouldReturnTotalFilmCount () {
            when (filmRepository.count ()).thenReturn (1000L);

            assertThat (filmService.countFilms ()).isEqualTo (1000L);
        }

        @Test
        @DisplayName ("should return zero when repository has no films")
        void shouldReturnZeroWhenNoFilms () {
            when (filmRepository.count ()).thenReturn (0L);

            assertThat (filmService.countFilms ()).isZero ();
        }

        @Test
        @DisplayName ("should delegate count call to repository exactly once")
        void shouldDelegateCountToRepository () {
            when (filmRepository.count ()).thenReturn (500L);

            filmService.countFilms ();

            verify (filmRepository, times (1)).count ();
            verifyNoMoreInteractions (filmMapper);
        }
    }
}

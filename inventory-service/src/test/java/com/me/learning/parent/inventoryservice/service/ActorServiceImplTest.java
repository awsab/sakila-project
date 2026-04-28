package com.me.learning.parent.inventoryservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import com.me.learning.parent.inventoryservice.dto.request.ActorRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.ActorResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.ActorUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.ActorMapper;
import com.me.learning.parent.inventoryservice.model.Actor;
import com.me.learning.parent.inventoryservice.repository.ActorRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Unit tests for ActorServiceImpl
 * Since    : Version 1.0
 */
@DisplayName ("ActorServiceImpl Unit Tests")
@ExtendWith (MockitoExtension.class)
class ActorServiceImplTest {

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private ActorMapper actorMapper;

    @InjectMocks
    private ActorServiceImpl actorService;

    private Actor           actor;
    private ActorRequestDTO  requestDTO;
    private ActorResponseDTO responseDTO;
    private ActorUpdateDTO   updateDTO;

    @BeforeEach
    void setUp () {
        actor = new Actor ();
        actor.setId (1);
        actor.setFirstName ("PENELOPE");
        actor.setLastName ("GUINESS");
        actor.setLastUpdate (LocalDateTime.now ());

        requestDTO = ActorRequestDTO.builder ()
                .firstName ("PENELOPE")
                .lastName ("GUINESS")
                .build ();

        responseDTO = ActorResponseDTO.builder ()
                .id (1)
                .firstName ("PENELOPE")
                .lastName ("GUINESS")
                .lastUpdate (LocalDateTime.now ())
                .build ();

        updateDTO = ActorUpdateDTO.builder ()
                .id (1)
                .firstName ("NICK")
                .lastName ("WAHLBERG")
                .build ();
    }

    // ─────────────────────────────────── createActor ───────────────────────────────────

    @Nested
    @DisplayName ("createActor")
    /* default */ class CreateActor {

        @Test
        @DisplayName ("should create actor and return response when name does not already exist")
        void shouldCreateActorSuccessfully () {
            when (actorRepository.existsByFirstNameAndLastName ("PENELOPE", "GUINESS")).thenReturn (false);
            when (actorMapper.toEntity (requestDTO)).thenReturn (actor);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            ActorResponseDTO result = actorService.createActor (requestDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getFirstName ()).isEqualTo ("PENELOPE");
            assertThat (result.getLastName ()).isEqualTo ("GUINESS");
            verify (actorRepository, times (1)).save (actor);
        }

        @Test
        @DisplayName ("should invoke mapper toEntity and toDto during successful creation")
        void shouldInvokeMapperBeforeSave () {
            when (actorRepository.existsByFirstNameAndLastName ("PENELOPE", "GUINESS")).thenReturn (false);
            when (actorMapper.toEntity (requestDTO)).thenReturn (actor);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            actorService.createActor (requestDTO);

            verify (actorMapper).toEntity (requestDTO);
            verify (actorMapper).toDto (actor);
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when actor name already exists")
        void shouldThrowDuplicateExceptionWhenNameExists () {
            when (actorRepository.existsByFirstNameAndLastName ("PENELOPE", "GUINESS")).thenReturn (true);

            assertThatThrownBy (() -> actorService.createActor (requestDTO))
                    .isInstanceOf (DuplicateResourceException.class)
                    .hasMessageContaining ("Actor");

            verify (actorRepository, never ()).save (any ());
            verify (actorMapper, never ()).toEntity (any ());
        }

        @Test
        @DisplayName ("should not call repository save when duplicate is detected")
        void shouldNotSaveWhenDuplicateDetected () {
            when (actorRepository.existsByFirstNameAndLastName (anyString (), anyString ())).thenReturn (true);

            assertThatThrownBy (() -> actorService.createActor (requestDTO))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (actorRepository, never ()).save (any (Actor.class));
        }
    }

    // ─────────────────────────────────── updateActor ───────────────────────────────────

    @Nested
    @DisplayName ("updateActor")
    /* default */ class UpdateActor {

        @Test
        @DisplayName ("should update actor and return updated response when no name conflict")
        void shouldUpdateActorSuccessfully () {
            when (actorRepository.findById (1)).thenReturn (Optional.of (actor));
            when (actorRepository.existsByFirstNameAndLastName ("NICK", "WAHLBERG")).thenReturn (false);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (
                    ActorResponseDTO.builder ().id (1).firstName ("NICK").lastName ("WAHLBERG").build ());

            ActorResponseDTO result = actorService.updateActor (1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getFirstName ()).isEqualTo ("NICK");
            assertThat (result.getLastName ()).isEqualTo ("WAHLBERG");
        }

        @Test
        @DisplayName ("should apply updated firstName and lastName to the entity before saving")
        void shouldApplyFieldsBeforeSave () {
            when (actorRepository.findById (1)).thenReturn (Optional.of (actor));
            when (actorRepository.existsByFirstNameAndLastName ("NICK", "WAHLBERG")).thenReturn (false);
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            actorService.updateActor (1, updateDTO);

            assertThat (actor.getFirstName ()).isEqualTo ("NICK");
            assertThat (actor.getLastName ()).isEqualTo ("WAHLBERG");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when actor ID does not exist")
        void shouldThrowNotFoundExceptionWhenActorMissing () {
            when (actorRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> actorService.updateActor (99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Actor");

            verify (actorRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when new name already belongs to another actor")
        void shouldThrowDuplicateExceptionOnNameConflict () {
            Actor existing = new Actor ();
            existing.setId (1);
            existing.setFirstName ("PENELOPE");
            existing.setLastName ("GUINESS");

            when (actorRepository.findById (1)).thenReturn (Optional.of (existing));
            when (actorRepository.existsByFirstNameAndLastName ("NICK", "WAHLBERG")).thenReturn (true);

            assertThatThrownBy (() -> actorService.updateActor (1, updateDTO))
                    .isInstanceOf (DuplicateResourceException.class)
                    .hasMessageContaining ("Actor");

            verify (actorRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should skip duplicate check when actor name remains unchanged")
        void shouldSkipDuplicateCheckWhenNameUnchanged () {
            ActorUpdateDTO sameNameUpdate = ActorUpdateDTO.builder ()
                    .id (1).firstName ("PENELOPE").lastName ("GUINESS").build ();

            when (actorRepository.findById (1)).thenReturn (Optional.of (actor));
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            ActorResponseDTO result = actorService.updateActor (1, sameNameUpdate);

            assertThat (result).isNotNull ();
            verify (actorRepository, never ()).existsByFirstNameAndLastName (anyString (), anyString ());
        }
    }

    // ─────────────────────────────────── patchActor ────────────────────────────────────

    @Nested
    @DisplayName ("patchActor")
    /* default */ class PatchActor {

        @Test
        @DisplayName ("should patch actor successfully when path ID and body ID match")
        void shouldPatchActorSuccessfully () {
            when (actorRepository.findById (1)).thenReturn (Optional.of (actor));
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            ActorResponseDTO result = actorService.patchActor (1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should delegate field mapping to actorMapper.updateEntity during patch")
        void shouldDelegateToMapperUpdateEntity () {
            when (actorRepository.findById (1)).thenReturn (Optional.of (actor));
            when (actorRepository.save (actor)).thenReturn (actor);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            actorService.patchActor (1, updateDTO);

            verify (actorMapper).updateEntity (updateDTO, actor);
        }

        @Test
        @DisplayName ("should throw IllegalArgumentException when path ID and body ID differ")
        void shouldThrowIllegalArgWhenIdsMismatch () {
            ActorUpdateDTO mismatchDTO = ActorUpdateDTO.builder ()
                    .id (99).firstName ("X").lastName ("Y").build ();

            assertThatThrownBy (() -> actorService.patchActor (1, mismatchDTO))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessageContaining ("1");

            verify (actorRepository, never ()).findById (anyInt ());
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when actor is not found during patch")
        void shouldThrowNotFoundWhenActorMissingForPatch () {
            when (actorRepository.findById (1)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> actorService.patchActor (1, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Actor");

            verify (actorRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────── getActorById ──────────────────────────────────

    @Nested
    @DisplayName ("getActorById")
    /* default */ class GetActorById {

        @Test
        @DisplayName ("should return actor response when actor exists for given ID")
        void shouldReturnActorById () {
            when (actorRepository.findById (1)).thenReturn (Optional.of (actor));
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            ActorResponseDTO result = actorService.getActorById (1);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getFirstName ()).isEqualTo ("PENELOPE");
            assertThat (result.getLastName ()).isEqualTo ("GUINESS");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no actor found for given ID")
        void shouldThrowNotFoundWhenActorMissing () {
            when (actorRepository.findById (99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> actorService.getActorById (99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Actor");
        }
    }

    // ─────────────────────────────────── getAllActors ───────────────────────────────────

    @Nested
    @DisplayName ("getAllActors")
    /* default */ class GetAllActors {

        @Test
        @DisplayName ("should return list with all actors when actors exist")
        void shouldReturnAllActors () {
            when (actorRepository.findAll ()).thenReturn (List.of (actor));
            when (actorMapper.toDtoList (List.of (actor))).thenReturn (List.of (responseDTO));

            List<ActorResponseDTO> result = actorService.getAllActors ();

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getFirstName ()).isEqualTo ("PENELOPE");
        }

        @Test
        @DisplayName ("should return empty list when no actors exist")
        void shouldReturnEmptyListWhenNoActors () {
            when (actorRepository.findAll ()).thenReturn (Collections.emptyList ());
            when (actorMapper.toDtoList (Collections.emptyList ())).thenReturn (Collections.emptyList ());

            assertThat (actorService.getAllActors ()).isNotNull ().isEmpty ();
        }

        @Test
        @DisplayName ("should return paginated actors for given pageable")
        void shouldReturnPaginatedActors () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Actor> page = new PageImpl<> (List.of (actor), pageable, 1);
            when (actorRepository.findAll (pageable)).thenReturn (page);
            when (actorMapper.toDto (actor)).thenReturn (responseDTO);

            Page<ActorResponseDTO> result = actorService.getAllActors (pageable);

            assertThat (result).isNotNull ();
            assertThat (result.getTotalElements ()).isEqualTo (1);
            assertThat (result.getContent ()).hasSize (1);
            assertThat (result.getContent ().get (0).getFirstName ()).isEqualTo ("PENELOPE");
        }

        @Test
        @DisplayName ("should return empty page when no actors match pageable criteria")
        void shouldReturnEmptyPage () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Actor> emptyPage = new PageImpl<> (Collections.emptyList (), pageable, 0);
            when (actorRepository.findAll (pageable)).thenReturn (emptyPage);

            Page<ActorResponseDTO> result = actorService.getAllActors (pageable);

            assertThat (result).isNotNull ();
            assertThat (result.getTotalElements ()).isZero ();
            assertThat (result.getContent ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── searchActorsByName ────────────────────────────

    @Nested
    @DisplayName ("searchActorsByName")
    /* default */ class SearchActorsByName {

        @Test
        @DisplayName ("should return actors whose name matches the search term")
        void shouldReturnMatchingActors () {
            when (actorRepository.searchByName ("PEN")).thenReturn (List.of (actor));
            when (actorMapper.toDtoList (List.of (actor))).thenReturn (List.of (responseDTO));

            List<ActorResponseDTO> result = actorService.searchActorsByName ("PEN");

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getFirstName ()).isEqualTo ("PENELOPE");
        }

        @Test
        @DisplayName ("should return empty list when no actor matches the search term")
        void shouldReturnEmptyListWhenNoMatch () {
            when (actorRepository.searchByName ("XYZ")).thenReturn (Collections.emptyList ());
            when (actorMapper.toDtoList (Collections.emptyList ())).thenReturn (Collections.emptyList ());

            assertThat (actorService.searchActorsByName ("XYZ")).isNotNull ().isEmpty ();
        }
    }

    // ─────────────────────────────────── getActorsByFirstName ──────────────────────────

    @Nested
    @DisplayName ("getActorsByFirstName")
    /* default */ class GetActorsByFirstName {

        @Test
        @DisplayName ("should return actors whose first name contains the given term (case-insensitive)")
        void shouldReturnActorsByFirstName () {
            when (actorRepository.findByFirstNameContainingIgnoreCase ("pene")).thenReturn (List.of (actor));
            when (actorMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<ActorResponseDTO> result = actorService.getActorsByFirstName ("pene");

            assertThat (result).isNotNull ().hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when no actors match the first name")
        void shouldReturnEmptyWhenNoFirstNameMatch () {
            when (actorRepository.findByFirstNameContainingIgnoreCase ("zzz")).thenReturn (Collections.emptyList ());
            when (actorMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (actorService.getActorsByFirstName ("zzz")).isEmpty ();
        }
    }

    // ─────────────────────────────────── getActorsByLastName ───────────────────────────

    @Nested
    @DisplayName ("getActorsByLastName")
    /* default */ class GetActorsByLastName {

        @Test
        @DisplayName ("should return actors whose last name contains the given term (case-insensitive)")
        void shouldReturnActorsByLastName () {
            when (actorRepository.findByLastNameContainingIgnoreCase ("guin")).thenReturn (List.of (actor));
            when (actorMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<ActorResponseDTO> result = actorService.getActorsByLastName ("guin");

            assertThat (result).isNotNull ().hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when no actors match the last name")
        void shouldReturnEmptyWhenNoLastNameMatch () {
            when (actorRepository.findByLastNameContainingIgnoreCase ("zzz")).thenReturn (Collections.emptyList ());
            when (actorMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (actorService.getActorsByLastName ("zzz")).isEmpty ();
        }
    }

    // ─────────────────────────────────── getAllActorsSortedByLastName ───────────────────

    @Nested
    @DisplayName ("getAllActorsSortedByLastName")
    /* default */ class GetAllActorsSortedByLastName {

        @Test
        @DisplayName ("should return actors sorted by last name in ascending order")
        void shouldReturnActorsSortedByLastName () {
            Actor second = new Actor ();
            second.setId (2);
            second.setFirstName ("NICK");
            second.setLastName ("WAHLBERG");
            ActorResponseDTO secondResponse = ActorResponseDTO.builder ()
                    .id (2).firstName ("NICK").lastName ("WAHLBERG").build ();

            when (actorRepository.findAllByOrderByLastNameAsc ()).thenReturn (List.of (actor, second));
            when (actorMapper.toDtoList (List.of (actor, second))).thenReturn (List.of (responseDTO, secondResponse));

            List<ActorResponseDTO> result = actorService.getAllActorsSortedByLastName ();

            assertThat (result).hasSize (2);
            assertThat (result).extracting (ActorResponseDTO::getLastName)
                    .containsExactly ("GUINESS", "WAHLBERG");
        }

        @Test
        @DisplayName ("should return empty list when no actors are present")
        void shouldReturnEmptyListWhenNoActors () {
            when (actorRepository.findAllByOrderByLastNameAsc ()).thenReturn (Collections.emptyList ());
            when (actorMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (actorService.getAllActorsSortedByLastName ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── existsById ────────────────────────────────────

    @Nested
    @DisplayName ("existsById")
    /* default */ class ExistsById {

        @Test
        @DisplayName ("should return true when actor exists with the given ID")
        void shouldReturnTrueWhenActorExists () {
            when (actorRepository.existsById (1)).thenReturn (true);

            assertThat (actorService.existsById (1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when actor does not exist with the given ID")
        void shouldReturnFalseWhenActorNotExists () {
            when (actorRepository.existsById (99)).thenReturn (false);

            assertThat (actorService.existsById (99)).isFalse ();
        }
    }

    // ─────────────────────────────────── existsByName ──────────────────────────────────

    @Nested
    @DisplayName ("existsByName")
    /* default */ class ExistsByName {

        @Test
        @DisplayName ("should return true when actor with given first and last name exists")
        void shouldReturnTrueWhenNameExists () {
            when (actorRepository.existsByFirstNameAndLastName ("PENELOPE", "GUINESS")).thenReturn (true);

            assertThat (actorService.existsByName ("PENELOPE", "GUINESS")).isTrue ();
        }

        @Test
        @DisplayName ("should return false when no actor found with given name combination")
        void shouldReturnFalseWhenNameNotFound () {
            when (actorRepository.existsByFirstNameAndLastName ("X", "Y")).thenReturn (false);

            assertThat (actorService.existsByName ("X", "Y")).isFalse ();
        }
    }

    // ─────────────────────────────────── deleteActor ───────────────────────────────────

    @Nested
    @DisplayName ("deleteActor")
    /* default */ class DeleteActor {

        @Test
        @DisplayName ("should delete actor without throwing exception when actor exists")
        void shouldDeleteActorSuccessfully () {
            when (actorRepository.existsById (1)).thenReturn (true);

            assertThatCode (() -> actorService.deleteActor (1)).doesNotThrowAnyException ();
            verify (actorRepository, times (1)).deleteById (1);
        }

        @Test
        @DisplayName ("should invoke deleteById exactly once when actor is found")
        void shouldCallDeleteByIdExactlyOnce () {
            when (actorRepository.existsById (1)).thenReturn (true);

            actorService.deleteActor (1);

            verify (actorRepository, times (1)).deleteById (1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when actor not found for delete")
        void shouldThrowNotFoundWhenActorNotExistsForDelete () {
            when (actorRepository.existsById (99)).thenReturn (false);

            assertThatThrownBy (() -> actorService.deleteActor (99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Actor");

            verify (actorRepository, never ()).deleteById (anyInt ());
        }

        @Test
        @DisplayName ("should not call deleteById when actor does not exist")
        void shouldNotCallDeleteByIdWhenActorNotFound () {
            when (actorRepository.existsById (anyInt ())).thenReturn (false);

            assertThatThrownBy (() -> actorService.deleteActor (5))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (actorRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────── countActors ───────────────────────────────────

    @Nested
    @DisplayName ("countActors")
    /* default */ class CountActors {

        @Test
        @DisplayName ("should return total actor count from repository")
        void shouldReturnTotalActorCount () {
            when (actorRepository.count ()).thenReturn (200L);

            assertThat (actorService.countActors ()).isEqualTo (200L);
        }

        @Test
        @DisplayName ("should return zero when repository has no actors")
        void shouldReturnZeroWhenNoActors () {
            when (actorRepository.count ()).thenReturn (0L);

            assertThat (actorService.countActors ()).isZero ();
        }

        @Test
        @DisplayName ("should delegate count call to repository exactly once")
        void shouldDelegateCountToRepository () {
            when (actorRepository.count ()).thenReturn (10L);

            actorService.countActors ();

            verify (actorRepository, times (1)).count ();
            verifyNoMoreInteractions (actorMapper);
        }
    }
}

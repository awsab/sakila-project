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
import com.me.learning.parent.inventoryservice.dto.request.LanguageRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.LanguageResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.LanguageUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.LanguageMapper;
import com.me.learning.parent.inventoryservice.model.Language;
import com.me.learning.parent.inventoryservice.repository.LanguageRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Unit tests for LanguageServiceImpl
 * Since    : Version 1.0
 */
@DisplayName ("LanguageServiceImpl Unit Tests")
@ExtendWith (MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private LanguageMapper languageMapper;

    @InjectMocks
    private LanguageServiceImpl languageService;

    private Language            language;
    private LanguageRequestDTO  requestDTO;
    private LanguageResponseDTO responseDTO;
    private LanguageUpdateDTO   updateDTO;

    @BeforeEach
    void setUp () {
        language = new Language ();
        language.setId ((short) 1);
        language.setName ("English");

        requestDTO = LanguageRequestDTO.builder ()
                .name ("English")
                .build ();

        responseDTO = LanguageResponseDTO.builder ()
                .id (1)
                .name ("English")
                .lastUpdate (Instant.now ())
                .build ();

        updateDTO = LanguageUpdateDTO.builder ()
                .id (1)
                .name ("French")
                .build ();
    }

    // ─────────────────────────────────── createLanguage ────────────────────────────────

    @Nested
    @DisplayName ("createLanguage")
    /* default */ class CreateLanguage {

        @Test
        @DisplayName ("should create language and return response when name does not already exist")
        void shouldCreateLanguageSuccessfully () {
            when (languageRepository.existsByName ("English")).thenReturn (false);
            when (languageMapper.toEntity (requestDTO)).thenReturn (language);
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            LanguageResponseDTO result = languageService.createLanguage (requestDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getName ()).isEqualTo ("English");
            verify (languageRepository, times (1)).save (language);
        }

        @Test
        @DisplayName ("should invoke mapper toEntity and toDto during successful creation")
        void shouldInvokeMapperDuringCreation () {
            when (languageRepository.existsByName ("English")).thenReturn (false);
            when (languageMapper.toEntity (requestDTO)).thenReturn (language);
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            languageService.createLanguage (requestDTO);

            verify (languageMapper).toEntity (requestDTO);
            verify (languageMapper).toDto (language);
        }

        @Test
        @DisplayName ("should throw IllegalArgumentException when language name already exists")
        void shouldThrowIllegalArgWhenNameExists () {
            when (languageRepository.existsByName ("English")).thenReturn (true);

            assertThatThrownBy (() -> languageService.createLanguage (requestDTO))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessageContaining ("English");

            verify (languageRepository, never ()).save (any ());
            verify (languageMapper, never ()).toEntity (any (LanguageRequestDTO.class));
        }

        @Test
        @DisplayName ("should not call repository save when duplicate name is detected")
        void shouldNotSaveWhenDuplicateNameDetected () {
            when (languageRepository.existsByName (any ())).thenReturn (true);

            assertThatThrownBy (() -> languageService.createLanguage (requestDTO))
                    .isInstanceOf (IllegalArgumentException.class);

            verify (languageRepository, never ()).save (any (Language.class));
        }
    }

    // ─────────────────────────────────── updateLanguage ────────────────────────────────

    @Nested
    @DisplayName ("updateLanguage")
    /* default */ class UpdateLanguage {

        @Test
        @DisplayName ("should update language name and return updated response when no conflict")
        void shouldUpdateLanguageSuccessfully () {
            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageRepository.existsByName ("French")).thenReturn (false);
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (
                    LanguageResponseDTO.builder ().id (1).name ("French").build ());

            LanguageResponseDTO result = languageService.updateLanguage ((short) 1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getName ()).isEqualTo ("French");
        }

        @Test
        @DisplayName ("should apply new name to entity before saving")
        void shouldApplyNewNameBeforeSave () {
            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageRepository.existsByName ("French")).thenReturn (false);
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            languageService.updateLanguage ((short) 1, updateDTO);

            assertThat (language.getName ()).isEqualTo ("French");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when language ID does not exist")
        void shouldThrowNotFoundWhenLanguageMissing () {
            when (languageRepository.findById ((short) 99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> languageService.updateLanguage ((short) 99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Language");

            verify (languageRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when new name conflicts with existing language")
        void shouldThrowDuplicateExceptionOnNameConflict () {
            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageRepository.existsByName ("French")).thenReturn (true);

            assertThatThrownBy (() -> languageService.updateLanguage ((short) 1, updateDTO))
                    .isInstanceOf (DuplicateResourceException.class)
                    .hasMessageContaining ("Language");

            verify (languageRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should skip duplicate check when language name remains unchanged")
        void shouldSkipDuplicateCheckWhenNameUnchanged () {
            LanguageUpdateDTO sameNameDTO = LanguageUpdateDTO.builder ().id (1).name ("English").build ();

            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            LanguageResponseDTO result = languageService.updateLanguage ((short) 1, sameNameDTO);

            assertThat (result).isNotNull ();
            verify (languageRepository, never ()).existsByName (any ());
        }
    }

    // ─────────────────────────────────── patchLanguage ─────────────────────────────────

    @Nested
    @DisplayName ("patchLanguage")
    /* default */ class PatchLanguage {

        @Test
        @DisplayName ("should patch language successfully and return updated response")
        void shouldPatchLanguageSuccessfully () {
            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            LanguageResponseDTO result = languageService.patchLanguage ((short) 1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should delegate field mapping to languageMapper.updateEntity during patch")
        void shouldDelegateToMapperUpdateEntity () {
            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageRepository.save (language)).thenReturn (language);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            languageService.patchLanguage ((short) 1, updateDTO);

            verify (languageMapper).updateEntity (updateDTO, language);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when language not found during patch")
        void shouldThrowNotFoundWhenLanguageMissingForPatch () {
            when (languageRepository.findById ((short) 99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> languageService.patchLanguage ((short) 99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Language");

            verify (languageRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────── getLanguageById ───────────────────────────────

    @Nested
    @DisplayName ("getLanguageById")
    /* default */ class GetLanguageById {

        @Test
        @DisplayName ("should return language response when language exists for given ID")
        void shouldReturnLanguageById () {
            when (languageRepository.findById ((short) 1)).thenReturn (Optional.of (language));
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            LanguageResponseDTO result = languageService.getLanguageById ((short) 1);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getName ()).isEqualTo ("English");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no language found for given ID")
        void shouldThrowNotFoundWhenLanguageMissingById () {
            when (languageRepository.findById ((short) 99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> languageService.getLanguageById ((short) 99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Language");
        }
    }

    // ─────────────────────────────────── getLanguageByName ─────────────────────────────

    @Nested
    @DisplayName ("getLanguageByName")
    /* default */ class GetLanguageByName {

        @Test
        @DisplayName ("should return language response when language exists for given name")
        void shouldReturnLanguageByName () {
            when (languageRepository.findByName ("English")).thenReturn (Optional.of (language));
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            LanguageResponseDTO result = languageService.getLanguageByName ("English");

            assertThat (result).isNotNull ();
            assertThat (result.getName ()).isEqualTo ("English");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no language found for given name")
        void shouldThrowNotFoundWhenLanguageMissingByName () {
            when (languageRepository.findByName ("Unknown")).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> languageService.getLanguageByName ("Unknown"))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Language");
        }
    }

    // ─────────────────────────────────── getAllLanguages ───────────────────────────────

    @Nested
    @DisplayName ("getAllLanguages")
    /* default */ class GetAllLanguages {

        @Test
        @DisplayName ("should return list with all languages when languages exist")
        void shouldReturnAllLanguages () {
            when (languageRepository.findAll ()).thenReturn (List.of (language));
            when (languageMapper.toDtoList (List.of (language))).thenReturn (List.of (responseDTO));

            List<LanguageResponseDTO> result = languageService.getAllLanguages ();

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getName ()).isEqualTo ("English");
        }

        @Test
        @DisplayName ("should return empty list when no languages exist")
        void shouldReturnEmptyListWhenNoLanguages () {
            when (languageRepository.findAll ()).thenReturn (Collections.emptyList ());
            when (languageMapper.toDtoList (Collections.emptyList ())).thenReturn (Collections.emptyList ());

            assertThat (languageService.getAllLanguages ()).isNotNull ().isEmpty ();
        }

        @Test
        @DisplayName ("should return paginated languages for given pageable")
        void shouldReturnPaginatedLanguages () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Language> page = new PageImpl<> (List.of (language), pageable, 1);
            when (languageRepository.findAll (pageable)).thenReturn (page);
            when (languageMapper.toDto (language)).thenReturn (responseDTO);

            Page<LanguageResponseDTO> result = languageService.getAllLanguages (pageable);

            assertThat (result).isNotNull ();
            assertThat (result.getTotalElements ()).isEqualTo (1);
            assertThat (result.getContent ()).hasSize (1);
            assertThat (result.getContent ().get (0).getName ()).isEqualTo ("English");
        }

        @Test
        @DisplayName ("should return empty page when no languages match pageable criteria")
        void shouldReturnEmptyPage () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Language> emptyPage = new PageImpl<> (Collections.emptyList (), pageable, 0);
            when (languageRepository.findAll (pageable)).thenReturn (emptyPage);

            Page<LanguageResponseDTO> result = languageService.getAllLanguages (pageable);

            assertThat (result.getTotalElements ()).isZero ();
            assertThat (result.getContent ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── searchLanguagesByName ─────────────────────────

    @Nested
    @DisplayName ("searchLanguagesByName")
    /* default */ class SearchLanguagesByName {

        @Test
        @DisplayName ("should return languages whose name contains the search term (case-insensitive)")
        void shouldReturnMatchingLanguages () {
            when (languageRepository.findByNameContainingIgnoreCase ("eng")).thenReturn (List.of (language));
            when (languageMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<LanguageResponseDTO> result = languageService.searchLanguagesByName ("eng");

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getName ()).isEqualTo ("English");
        }

        @Test
        @DisplayName ("should return empty list when no languages match the search term")
        void shouldReturnEmptyWhenNoMatch () {
            when (languageRepository.findByNameContainingIgnoreCase ("xyz")).thenReturn (Collections.emptyList ());
            when (languageMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (languageService.searchLanguagesByName ("xyz")).isEmpty ();
        }
    }

    // ─────────────────────────────────── getAllLanguagesSortedByName ────────────────────

    @Nested
    @DisplayName ("getAllLanguagesSortedByName")
    /* default */ class GetAllLanguagesSortedByName {

        @Test
        @DisplayName ("should return all languages sorted by name ascending")
        void shouldReturnLanguagesSortedByName () {
            Language second = new Language ();
            second.setId ((short) 2);
            second.setName ("Spanish");
            LanguageResponseDTO secondResponse = LanguageResponseDTO.builder ().id (2).name ("Spanish").build ();

            when (languageRepository.findAllByOrderByNameAsc ()).thenReturn (List.of (language, second));
            when (languageMapper.toDtoList (any ())).thenReturn (List.of (responseDTO, secondResponse));

            List<LanguageResponseDTO> result = languageService.getAllLanguagesSortedByName ();

            assertThat (result).hasSize (2);
            assertThat (result).extracting (LanguageResponseDTO::getName)
                    .containsExactly ("English", "Spanish");
        }

        @Test
        @DisplayName ("should return empty list when no languages are present")
        void shouldReturnEmptyListWhenNoLanguages () {
            when (languageRepository.findAllByOrderByNameAsc ()).thenReturn (Collections.emptyList ());
            when (languageMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (languageService.getAllLanguagesSortedByName ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── existsById ────────────────────────────────────

    @Nested
    @DisplayName ("existsById")
    /* default */ class ExistsById {

        @Test
        @DisplayName ("should return true when language exists with the given ID")
        void shouldReturnTrueWhenLanguageExists () {
            when (languageRepository.existsById ((short) 1)).thenReturn (true);

            assertThat (languageService.existsById ((short) 1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when language does not exist with the given ID")
        void shouldReturnFalseWhenLanguageNotExists () {
            when (languageRepository.existsById ((short) 99)).thenReturn (false);

            assertThat (languageService.existsById ((short) 99)).isFalse ();
        }
    }

    // ─────────────────────────────────── existsByName ──────────────────────────────────

    @Nested
    @DisplayName ("existsByName")
    /* default */ class ExistsByName {

        @Test
        @DisplayName ("should return true when language with given name exists")
        void shouldReturnTrueWhenNameExists () {
            when (languageRepository.existsByName ("English")).thenReturn (true);

            assertThat (languageService.existsByName ("English")).isTrue ();
        }

        @Test
        @DisplayName ("should return false when no language found with given name")
        void shouldReturnFalseWhenNameNotFound () {
            when (languageRepository.existsByName ("Unknown")).thenReturn (false);

            assertThat (languageService.existsByName ("Unknown")).isFalse ();
        }
    }

    // ─────────────────────────────────── deleteLanguage ────────────────────────────────

    @Nested
    @DisplayName ("deleteLanguage")
    /* default */ class DeleteLanguage {

        @Test
        @DisplayName ("should delete language without throwing exception when language exists")
        void shouldDeleteLanguageSuccessfully () {
            when (languageRepository.existsById ((short) 1)).thenReturn (true);

            assertThatCode (() -> languageService.deleteLanguage ((short) 1)).doesNotThrowAnyException ();
            verify (languageRepository, times (1)).deleteById ((short) 1);
        }

        @Test
        @DisplayName ("should invoke deleteById exactly once when language is found")
        void shouldCallDeleteByIdExactlyOnce () {
            when (languageRepository.existsById ((short) 1)).thenReturn (true);

            languageService.deleteLanguage ((short) 1);

            verify (languageRepository, times (1)).deleteById ((short) 1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when language not found for delete")
        void shouldThrowNotFoundWhenLanguageNotExistsForDelete () {
            when (languageRepository.existsById ((short) 99)).thenReturn (false);

            assertThatThrownBy (() -> languageService.deleteLanguage ((short) 99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Language");

            verify (languageRepository, never ()).deleteById (any ());
        }

        @Test
        @DisplayName ("should not call deleteById when language does not exist")
        void shouldNotCallDeleteByIdWhenLanguageNotFound () {
            when (languageRepository.existsById (any ())).thenReturn (false);

            assertThatThrownBy (() -> languageService.deleteLanguage ((short) 5))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (languageRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────── countLanguages ───────────────────────────────

    @Nested
    @DisplayName ("countLanguages")
    /* default */ class CountLanguages {

        @Test
        @DisplayName ("should return total language count from repository")
        void shouldReturnTotalLanguageCount () {
            when (languageRepository.count ()).thenReturn (6L);

            assertThat (languageService.countLanguages ()).isEqualTo (6L);
        }

        @Test
        @DisplayName ("should return zero when repository has no languages")
        void shouldReturnZeroWhenNoLanguages () {
            when (languageRepository.count ()).thenReturn (0L);

            assertThat (languageService.countLanguages ()).isZero ();
        }

        @Test
        @DisplayName ("should delegate count call to repository exactly once")
        void shouldDelegateCountToRepository () {
            when (languageRepository.count ()).thenReturn (6L);

            languageService.countLanguages ();

            verify (languageRepository, times (1)).count ();
            verifyNoMoreInteractions (languageMapper);
        }
    }
}

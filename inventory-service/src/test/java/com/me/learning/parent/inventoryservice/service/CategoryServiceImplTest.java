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
import com.me.learning.parent.inventoryservice.dto.request.CategoryRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.CategoryResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.CategoryUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.CategoryMapper;
import com.me.learning.parent.inventoryservice.model.Category;
import com.me.learning.parent.inventoryservice.repository.CategoryRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Unit tests for CategoryServiceImpl
 * Since    : Version 1.0
 */
@DisplayName ("CategoryServiceImpl Unit Tests")
@ExtendWith (MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category            category;
    private CategoryRequestDTO  requestDTO;
    private CategoryResponseDTO responseDTO;
    private CategoryUpdateDTO   updateDTO;

    @BeforeEach
    void setUp () {
        category = new Category ();
        category.setId ((short) 1);
        category.setName ("Action");

        requestDTO = CategoryRequestDTO.builder ()
                .name ("Action")
                .build ();

        responseDTO = CategoryResponseDTO.builder ()
                .id (1)
                .name ("Action")
                .lastUpdate (Instant.now ())
                .build ();

        updateDTO = CategoryUpdateDTO.builder ()
                .id (1)
                .name ("Comedy")
                .build ();
    }

    // ─────────────────────────────────── createCategory ────────────────────────────────

    @Nested
    @DisplayName ("createCategory")
    /* default */ class CreateCategory {

        @Test
        @DisplayName ("should create category and return response when name does not already exist")
        void shouldCreateCategorySuccessfully () {
            when (categoryRepository.existsByName ("Action")).thenReturn (false);
            when (categoryMapper.toEntity (requestDTO)).thenReturn (category);
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            CategoryResponseDTO result = categoryService.createCategory (requestDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getName ()).isEqualTo ("Action");
            verify (categoryRepository, times (1)).save (category);
        }

        @Test
        @DisplayName ("should invoke mapper toEntity and toDto during successful creation")
        void shouldInvokeMapperDuringCreation () {
            when (categoryRepository.existsByName ("Action")).thenReturn (false);
            when (categoryMapper.toEntity (requestDTO)).thenReturn (category);
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            categoryService.createCategory (requestDTO);

            verify (categoryMapper).toEntity (requestDTO);
            verify (categoryMapper).toDto (category);
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when category name already exists")
        void shouldThrowDuplicateExceptionWhenNameExists () {
            when (categoryRepository.existsByName ("Action")).thenReturn (true);

            assertThatThrownBy (() -> categoryService.createCategory (requestDTO))
                    .isInstanceOf (DuplicateResourceException.class)
                    .hasMessageContaining ("Category");

            verify (categoryRepository, never ()).save (any ());
            verify (categoryMapper, never ()).toEntity (any (CategoryRequestDTO.class));
        }

        @Test
        @DisplayName ("should not call repository save when duplicate name detected")
        void shouldNotSaveWhenDuplicateDetected () {
            when (categoryRepository.existsByName (any ())).thenReturn (true);

            assertThatThrownBy (() -> categoryService.createCategory (requestDTO))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (categoryRepository, never ()).save (any (Category.class));
        }
    }

    // ─────────────────────────────────── updateCategory ────────────────────────────────

    @Nested
    @DisplayName ("updateCategory")
    /* default */ class UpdateCategory {

        @Test
        @DisplayName ("should update category name and return updated response when no conflict")
        void shouldUpdateCategorySuccessfully () {
            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryRepository.existsByName ("Comedy")).thenReturn (false);
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (
                    CategoryResponseDTO.builder ().id (1).name ("Comedy").build ());

            CategoryResponseDTO result = categoryService.updateCategory ((short) 1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getName ()).isEqualTo ("Comedy");
        }

        @Test
        @DisplayName ("should apply new name to entity before saving")
        void shouldApplyNewNameBeforeSave () {
            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryRepository.existsByName ("Comedy")).thenReturn (false);
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            categoryService.updateCategory ((short) 1, updateDTO);

            assertThat (category.getName ()).isEqualTo ("Comedy");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when category ID does not exist")
        void shouldThrowNotFoundWhenCategoryMissing () {
            when (categoryRepository.findById ((short) 99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> categoryService.updateCategory ((short) 99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Category");

            verify (categoryRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should throw DuplicateResourceException when new name conflicts with existing category")
        void shouldThrowDuplicateExceptionOnNameConflict () {
            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryRepository.existsByName ("Comedy")).thenReturn (true);

            assertThatThrownBy (() -> categoryService.updateCategory ((short) 1, updateDTO))
                    .isInstanceOf (DuplicateResourceException.class)
                    .hasMessageContaining ("Category");

            verify (categoryRepository, never ()).save (any ());
        }

        @Test
        @DisplayName ("should skip duplicate check when category name remains unchanged")
        void shouldSkipDuplicateCheckWhenNameUnchanged () {
            CategoryUpdateDTO sameNameDTO = CategoryUpdateDTO.builder ().id (1).name ("Action").build ();

            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            CategoryResponseDTO result = categoryService.updateCategory ((short) 1, sameNameDTO);

            assertThat (result).isNotNull ();
            verify (categoryRepository, never ()).existsByName (any ());
        }
    }

    // ─────────────────────────────────── patchCategory ─────────────────────────────────

    @Nested
    @DisplayName ("patchCategory")
    /* default */ class PatchCategory {

        @Test
        @DisplayName ("should patch category successfully and return updated response")
        void shouldPatchCategorySuccessfully () {
            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            CategoryResponseDTO result = categoryService.patchCategory ((short) 1, updateDTO);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
        }

        @Test
        @DisplayName ("should delegate field mapping to categoryMapper.updateEntity during patch")
        void shouldDelegateToMapperUpdateEntity () {
            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryRepository.save (category)).thenReturn (category);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            categoryService.patchCategory ((short) 1, updateDTO);

            verify (categoryMapper).updateEntity (updateDTO, category);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when category not found during patch")
        void shouldThrowNotFoundWhenCategoryMissingForPatch () {
            when (categoryRepository.findById ((short) 99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> categoryService.patchCategory ((short) 99, updateDTO))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Category");

            verify (categoryRepository, never ()).save (any ());
        }
    }

    // ─────────────────────────────────── getCategoryById ───────────────────────────────

    @Nested
    @DisplayName ("getCategoryById")
    /* default */ class GetCategoryById {

        @Test
        @DisplayName ("should return category response when category exists for given ID")
        void shouldReturnCategoryById () {
            when (categoryRepository.findById ((short) 1)).thenReturn (Optional.of (category));
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            CategoryResponseDTO result = categoryService.getCategoryById ((short) 1);

            assertThat (result).isNotNull ();
            assertThat (result.getId ()).isEqualTo (1);
            assertThat (result.getName ()).isEqualTo ("Action");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no category found for given ID")
        void shouldThrowNotFoundWhenCategoryMissingById () {
            when (categoryRepository.findById ((short) 99)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> categoryService.getCategoryById ((short) 99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Category");
        }
    }

    // ─────────────────────────────────── getCategoryByName ─────────────────────────────

    @Nested
    @DisplayName ("getCategoryByName")
    /* default */ class GetCategoryByName {

        @Test
        @DisplayName ("should return category response when category exists for given name")
        void shouldReturnCategoryByName () {
            when (categoryRepository.findByName ("Action")).thenReturn (Optional.of (category));
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            CategoryResponseDTO result = categoryService.getCategoryByName ("Action");

            assertThat (result).isNotNull ();
            assertThat (result.getName ()).isEqualTo ("Action");
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when no category found for given name")
        void shouldThrowNotFoundWhenCategoryMissingByName () {
            when (categoryRepository.findByName ("Unknown")).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> categoryService.getCategoryByName ("Unknown"))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Category");
        }
    }

    // ─────────────────────────────────── getAllCategories ──────────────────────────────

    @Nested
    @DisplayName ("getAllCategories")
    /* default */ class GetAllCategories {

        @Test
        @DisplayName ("should return list with all categories when categories exist")
        void shouldReturnAllCategories () {
            when (categoryRepository.findAll ()).thenReturn (List.of (category));
            when (categoryMapper.toDtoList (List.of (category))).thenReturn (List.of (responseDTO));

            List<CategoryResponseDTO> result = categoryService.getAllCategories ();

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getName ()).isEqualTo ("Action");
        }

        @Test
        @DisplayName ("should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategories () {
            when (categoryRepository.findAll ()).thenReturn (Collections.emptyList ());
            when (categoryMapper.toDtoList (Collections.emptyList ())).thenReturn (Collections.emptyList ());

            assertThat (categoryService.getAllCategories ()).isNotNull ().isEmpty ();
        }

        @Test
        @DisplayName ("should return paginated categories for given pageable")
        void shouldReturnPaginatedCategories () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Category> page = new PageImpl<> (List.of (category), pageable, 1);
            when (categoryRepository.findAll (pageable)).thenReturn (page);
            when (categoryMapper.toDto (category)).thenReturn (responseDTO);

            Page<CategoryResponseDTO> result = categoryService.getAllCategories (pageable);

            assertThat (result).isNotNull ();
            assertThat (result.getTotalElements ()).isEqualTo (1);
            assertThat (result.getContent ()).hasSize (1);
            assertThat (result.getContent ().get (0).getName ()).isEqualTo ("Action");
        }

        @Test
        @DisplayName ("should return empty page when no categories match pageable criteria")
        void shouldReturnEmptyPage () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<Category> emptyPage = new PageImpl<> (Collections.emptyList (), pageable, 0);
            when (categoryRepository.findAll (pageable)).thenReturn (emptyPage);

            Page<CategoryResponseDTO> result = categoryService.getAllCategories (pageable);

            assertThat (result.getTotalElements ()).isZero ();
            assertThat (result.getContent ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── searchCategoriesByName ───────────────────────

    @Nested
    @DisplayName ("searchCategoriesByName")
    /* default */ class SearchCategoriesByName {

        @Test
        @DisplayName ("should return categories whose name contains the search term (case-insensitive)")
        void shouldReturnMatchingCategories () {
            when (categoryRepository.findByNameContainingIgnoreCase ("act")).thenReturn (List.of (category));
            when (categoryMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<CategoryResponseDTO> result = categoryService.searchCategoriesByName ("act");

            assertThat (result).isNotNull ().hasSize (1);
        }

        @Test
        @DisplayName ("should return empty list when no categories match search term")
        void shouldReturnEmptyListWhenNoMatch () {
            when (categoryRepository.findByNameContainingIgnoreCase ("xyz")).thenReturn (Collections.emptyList ());
            when (categoryMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (categoryService.searchCategoriesByName ("xyz")).isEmpty ();
        }
    }

    // ─────────────────────────────────── getAllCategoriesSortedByName ──────────────────

    @Nested
    @DisplayName ("getAllCategoriesSortedByName")
    /* default */ class GetAllCategoriesSortedByName {

        @Test
        @DisplayName ("should return all categories sorted by name ascending")
        void shouldReturnCategoriesSortedByName () {
            Category second = new Category ();
            second.setId ((short) 2);
            second.setName ("Drama");
            CategoryResponseDTO secondResponse = CategoryResponseDTO.builder ().id (2).name ("Drama").build ();

            when (categoryRepository.findAllByOrderByNameAsc ()).thenReturn (List.of (category, second));
            when (categoryMapper.toDtoList (any ())).thenReturn (List.of (responseDTO, secondResponse));

            List<CategoryResponseDTO> result = categoryService.getAllCategoriesSortedByName ();

            assertThat (result).hasSize (2);
            assertThat (result).extracting (CategoryResponseDTO::getName)
                    .containsExactly ("Action", "Drama");
        }

        @Test
        @DisplayName ("should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategories () {
            when (categoryRepository.findAllByOrderByNameAsc ()).thenReturn (Collections.emptyList ());
            when (categoryMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (categoryService.getAllCategoriesSortedByName ()).isEmpty ();
        }
    }

    // ─────────────────────────────────── getCategoriesByFilmId ────────────────────────

    @Nested
    @DisplayName ("getCategoriesByFilmId")
    /* default */ class GetCategoriesByFilmId {

        @Test
        @DisplayName ("should return categories associated with the given film ID")
        void shouldReturnCategoriesForFilm () {
            when (categoryRepository.findByFilmId (1)).thenReturn (List.of (category));
            when (categoryMapper.toDtoList (any ())).thenReturn (List.of (responseDTO));

            List<CategoryResponseDTO> result = categoryService.getCategoriesByFilmId (1);

            assertThat (result).isNotNull ().hasSize (1);
            assertThat (result.get (0).getName ()).isEqualTo ("Action");
        }

        @Test
        @DisplayName ("should return empty list when film has no associated categories")
        void shouldReturnEmptyWhenFilmHasNoCategories () {
            when (categoryRepository.findByFilmId (99)).thenReturn (Collections.emptyList ());
            when (categoryMapper.toDtoList (any ())).thenReturn (Collections.emptyList ());

            assertThat (categoryService.getCategoriesByFilmId (99)).isEmpty ();
        }
    }

    // ─────────────────────────────────── countFilmsByCategory ─────────────────────────

    @Nested
    @DisplayName ("countFilmsByCategory")
    /* default */ class CountFilmsByCategory {

        @Test
        @DisplayName ("should return film count for the given category ID")
        void shouldReturnFilmCountForCategory () {
            when (categoryRepository.countFilmsByCategoryId ((short) 1)).thenReturn (50L);

            assertThat (categoryService.countFilmsByCategory ((short) 1)).isEqualTo (50L);
        }

        @Test
        @DisplayName ("should return zero when category has no films")
        void shouldReturnZeroWhenCategoryHasNoFilms () {
            when (categoryRepository.countFilmsByCategoryId ((short) 5)).thenReturn (0L);

            assertThat (categoryService.countFilmsByCategory ((short) 5)).isZero ();
        }
    }

    // ─────────────────────────────────── existsById ────────────────────────────────────

    @Nested
    @DisplayName ("existsById")
    /* default */ class ExistsById {

        @Test
        @DisplayName ("should return true when category exists with the given ID")
        void shouldReturnTrueWhenCategoryExists () {
            when (categoryRepository.existsById ((short) 1)).thenReturn (true);

            assertThat (categoryService.existsById ((short) 1)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when category does not exist with the given ID")
        void shouldReturnFalseWhenCategoryNotExists () {
            when (categoryRepository.existsById ((short) 99)).thenReturn (false);

            assertThat (categoryService.existsById ((short) 99)).isFalse ();
        }
    }

    // ─────────────────────────────────── existsByName ──────────────────────────────────

    @Nested
    @DisplayName ("existsByName")
    /* default */ class ExistsByName {

        @Test
        @DisplayName ("should return true when category with given name exists")
        void shouldReturnTrueWhenNameExists () {
            when (categoryRepository.existsByName ("Action")).thenReturn (true);

            assertThat (categoryService.existsByName ("Action")).isTrue ();
        }

        @Test
        @DisplayName ("should return false when no category found with given name")
        void shouldReturnFalseWhenNameNotFound () {
            when (categoryRepository.existsByName ("Unknown")).thenReturn (false);

            assertThat (categoryService.existsByName ("Unknown")).isFalse ();
        }
    }

    // ─────────────────────────────────── deleteCategory ────────────────────────────────

    @Nested
    @DisplayName ("deleteCategory")
    /* default */ class DeleteCategory {

        @Test
        @DisplayName ("should delete category without throwing exception when category exists")
        void shouldDeleteCategorySuccessfully () {
            when (categoryRepository.existsById ((short) 1)).thenReturn (true);

            assertThatCode (() -> categoryService.deleteCategory ((short) 1)).doesNotThrowAnyException ();
            verify (categoryRepository, times (1)).deleteById ((short) 1);
        }

        @Test
        @DisplayName ("should invoke deleteById exactly once when category is found")
        void shouldCallDeleteByIdExactlyOnce () {
            when (categoryRepository.existsById ((short) 1)).thenReturn (true);

            categoryService.deleteCategory ((short) 1);

            verify (categoryRepository, times (1)).deleteById ((short) 1);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when category not found for delete")
        void shouldThrowNotFoundWhenCategoryNotExistsForDelete () {
            when (categoryRepository.existsById ((short) 99)).thenReturn (false);

            assertThatThrownBy (() -> categoryService.deleteCategory ((short) 99))
                    .isInstanceOf (ResourceNotFoundException.class)
                    .hasMessageContaining ("Category");

            verify (categoryRepository, never ()).deleteById (any ());
        }

        @Test
        @DisplayName ("should not call deleteById when category does not exist")
        void shouldNotCallDeleteByIdWhenCategoryNotFound () {
            when (categoryRepository.existsById (any ())).thenReturn (false);

            assertThatThrownBy (() -> categoryService.deleteCategory ((short) 5))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (categoryRepository, never ()).deleteById (any ());
        }
    }

    // ─────────────────────────────────── countCategories ──────────────────────────────

    @Nested
    @DisplayName ("countCategories")
    /* default */ class CountCategories {

        @Test
        @DisplayName ("should return total category count from repository")
        void shouldReturnTotalCategoryCount () {
            when (categoryRepository.count ()).thenReturn (16L);

            assertThat (categoryService.countCategories ()).isEqualTo (16L);
        }

        @Test
        @DisplayName ("should return zero when repository has no categories")
        void shouldReturnZeroWhenNoCategories () {
            when (categoryRepository.count ()).thenReturn (0L);

            assertThat (categoryService.countCategories ()).isZero ();
        }

        @Test
        @DisplayName ("should delegate count call to repository exactly once")
        void shouldDelegateCountToRepository () {
            when (categoryRepository.count ()).thenReturn (16L);

            categoryService.countCategories ();

            verify (categoryRepository, times (1)).count ();
            verifyNoMoreInteractions (categoryMapper);
        }
    }
}

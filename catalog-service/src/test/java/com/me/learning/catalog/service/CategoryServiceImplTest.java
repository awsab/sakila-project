package com.me.learning.catalog.service;

import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;
import com.me.learning.catalog.entity.Category;
import com.me.learning.catalog.mapper.CategoryMapper;
import com.me.learning.catalog.repository.CategoryRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


/**
 * Unit Test for {@link CategoryServiceImpl}
 * <p> All collaborators ({@link CategoryRepository}, {@link CategoryMapper}) are mocked,
 * so these tests focus solely on the logic within {@link CategoryServiceImpl}.
 * </p>
 *
 * <p>
 * Assertions use AssertJ for a fluent and readable style
 * </p>
 */

@ExtendWith (MockitoExtension.class)
@DisplayName ("Unit Tests for CategoryServiceImpl")
@SuppressWarnings ("PMD.MethodNamingConventions")
class CategoryServiceImplTest {

    /* ── Constants ─────────────────────────────────────────────────────── */
    private static final Instant NOW = Instant.parse ("2024-01-01T00:00:00Z");
    private static final Short CATEGORY_ID = 1;
    private static final String NAME = "Test Category";

    /* ── Mocks and Test Subject ───────────────────────────────────────── */
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    /* ── Test-data builders ─────────────────────────────────────────────── */
    private Category buildCategory () {
        Category category = new Category ();
        category.setId (CATEGORY_ID);
        category.setName (NAME);
        category.setLastUpdate (NOW);
        return category;
    }

    private CategoryRequestDto buildCategoryRequestDto () {
        return new CategoryRequestDto (null, NAME, NOW);
    }

    private CategoryResponseDto buildCategoryResponseDto () {
        return new CategoryResponseDto (CATEGORY_ID, NAME, NOW);
    }

    /*══════════════════════════════════════════════════════════════════════
    create()
    ══════════════════════════════════════════════════════════════════════*/

    @Nested
    @DisplayName ("Tests for create method")
    class CreateCategory {

        @Test
        @DisplayName ("Should create a new category successfully")
        void create_WithValidRequest_WithSuccess_CategoryResponse () {
            // Given
            CategoryRequestDto requestDto = buildCategoryRequestDto ();
            Category categoryToSave = buildCategory ();
            categoryToSave.setId (null); // ID should be null for new entity

            Category savedCategory = buildCategory ();
            savedCategory.setId (CATEGORY_ID); // Simulate DB-generated ID

            CategoryResponseDto expectedResponse = buildCategoryResponseDto ();

            // Mocking behavior
            when (categoryMapper.toEntity (requestDto)).thenReturn (categoryToSave);
            when (categoryRepository.save (categoryToSave)).thenReturn (savedCategory);
            when (categoryMapper.toResponseDto (savedCategory)).thenReturn (expectedResponse);

            // When
            CategoryResponseDto actualResponse = categoryService.create (requestDto);

            // Then
            assertNotNull (actualResponse, "Created category response should not be null");
            assertEquals (expectedResponse.id (), actualResponse.id (), "Category ID should match saved entity ID");
            assertEquals (expectedResponse.name (), actualResponse.name (), "Category name should match expected value");
            assertEquals (
                    expectedResponse.lastUpdate (),
                    actualResponse.lastUpdate (),
                    "Category lastUpdate should match expected timestamp"
            );

            // Verify interactions with mocks
            verify (categoryMapper).toEntity (requestDto);
            verify (categoryRepository).save (categoryToSave);
            verify (categoryMapper).toResponseDto (savedCategory);

        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void Create_withNullRequest_ShouldThrowException () {
            // Given
            CategoryRequestDto requestDto = null;

            // When & Then
            /* When & Then */
            assertThatThrownBy (() -> categoryService.create (requestDto))
                    .isInstanceOf (IllegalArgumentException.class);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is provided in request")
        void Create_withIdInRequest_ShouldThrowException () {
            // Given
            CategoryRequestDto requestDto = new CategoryRequestDto (CATEGORY_ID, NAME, NOW);

            // When & Then
            assertThatThrownBy (() -> categoryService.create (requestDto))
                    .isInstanceOf (IllegalArgumentException.class);
        }

        @Test
        @DisplayName ("Should create category and set current lastUpdate when request lastUpdate is null")
        void create_WithNullLastUpdate_ShouldSucceed () {
            /* Given */
            CategoryRequestDto requestDto = new CategoryRequestDto (null, NAME, null);
            Category categoryToSave = buildCategory ();
            categoryToSave.setId (null);
            Category savedCategory = buildCategory ();
            CategoryResponseDto expectedResponse = buildCategoryResponseDto ();

            /* When */
            when (categoryMapper.toEntity (requestDto)).thenReturn (categoryToSave);
            when (categoryRepository.save (categoryToSave)).thenReturn (savedCategory);
            when (categoryMapper.toResponseDto (savedCategory)).thenReturn (expectedResponse);

            /* Then */
            assertThat (categoryService.create (requestDto)).isNotNull ();
        }

    }

    /*══════════════════════════════════════════════════════════════════════
    update()
    ══════════════════════════════════════════════════════════════════════*/

    @Nested
    @DisplayName ("Tests for update method")
    class UpdateCategory {

        @Test
        @DisplayName ("Should update an existing category successfully")
        void update_WithValidRequest_WithSuccess_CategoryResponse () {

            // Given
            Short categoryId = CATEGORY_ID;
            CategoryRequestDto requestDto = buildCategoryRequestDto ();
            Category existingCategory = buildCategory ();
            existingCategory.setId (categoryId);
            CategoryResponseDto expectedResponse = buildCategoryResponseDto ();

            /* When */
            when (categoryMapper.toEntity (requestDto)).thenReturn (existingCategory);
            when (categoryRepository.save (existingCategory)).thenReturn (existingCategory);
            when (categoryMapper.toResponseDto (existingCategory)).thenReturn (expectedResponse);

            CategoryResponseDto actualResponse = categoryService.update (categoryId, requestDto);
            // Then
            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);

            // Verify interactions with mocks
            verify (categoryMapper).toEntity (requestDto);
            verify (categoryRepository).save (existingCategory);
            verify (categoryMapper).toResponseDto (existingCategory);

        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when request is null")
        void update_withNullRequest_ShouldThrowException () {
            // Given
            Short categoryId = CATEGORY_ID;
            CategoryRequestDto requestDto = null;

            // When & Then
            assertThatThrownBy (() -> categoryService.update (categoryId, requestDto))
                    .isInstanceOf (IllegalArgumentException.class);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void update_withNullId_ShouldThrowException () {
            // Given
            Short categoryId = null;
            CategoryRequestDto requestDto = buildCategoryRequestDto ();

            // When & Then
            assertThatThrownBy (() -> categoryService.update (categoryId, requestDto))
                    .isInstanceOf (IllegalArgumentException.class);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException, when CategoryRequestDto.Name in request is null")
        void update_withNullName_ShouldThrowException () {
            // Given
            CategoryRequestDto requestDto = new CategoryRequestDto (CATEGORY_ID, null, NOW);

            // When & Then
            assertThatThrownBy (() -> categoryService.update (CATEGORY_ID, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Category name cannot be null");

        }

        @Test
        @DisplayName ("Should update category and set current lastUpdate when request lastUpdate is null")
        void update_withNullLastUpdate_ShouldSucceed () {
            /* Given */
            CategoryRequestDto requestDto = new CategoryRequestDto (null, NAME, null);
            Category existingCategory = buildCategory ();
            CategoryResponseDto expectedResponse = buildCategoryResponseDto ();

            /* When */
            when (categoryMapper.toEntity (requestDto)).thenReturn (existingCategory);
            when (categoryRepository.save (existingCategory)).thenReturn (existingCategory);
            when (categoryMapper.toResponseDto (existingCategory)).thenReturn (expectedResponse);

            /* Then */
            assertThat (categoryService.update (CATEGORY_ID, requestDto)).usingRecursiveComparison ().isEqualTo (expectedResponse);
        }
    }

    /*══════════════════════════════════════════════════════════════════════
   partialUpdate()
   ══════════════════════════════════════════════════════════════════════*/
    @Nested
    @DisplayName ("partialUpdate() — Partially update an existing Category")
    class PartialUpdateCategory {

        @Test
        @DisplayName ("Should partially update an existing category successfully")
        void partialUpdate_WithValidRequest_WithSuccess_CategoryResponse () {
            /* Given */
            CategoryRequestDto requestDto = new CategoryRequestDto (CATEGORY_ID, "Updated Name", NOW);
            Category existingCategory = buildCategory ();
            CategoryResponseDto expectedResponse = buildCategoryResponseDto ();

            /* When */
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (Optional.of (existingCategory));
            when (categoryRepository.save (existingCategory)).thenReturn (existingCategory);
            when (categoryMapper.toResponseDto (existingCategory)).thenReturn (expectedResponse);

            CategoryResponseDto actualResponse = categoryService.partialUpdate (CATEGORY_ID, requestDto);

            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);

            // Verify interactions with mocks
            verify (categoryRepository).findById (CATEGORY_ID);
            verify (categoryMapper).partialUpdate (requestDto, existingCategory);
            verify (categoryRepository).save (existingCategory);
            verify (categoryMapper).toResponseDto (existingCategory);

        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when categoty name is null")
        void partialUpdate_withNullName_ShouldThrowException () {

            /* Given */
            CategoryRequestDto requestDto = new CategoryRequestDto (CATEGORY_ID, null, NOW);

            /* When & Then */
            assertThatThrownBy (() -> categoryService.partialUpdate (CATEGORY_ID, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Category name cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when partial update ID is null")
        void partialUpdate_withNullId_ShouldThrowException () {
            /* Given */
            CategoryRequestDto requestDto = new CategoryRequestDto (CATEGORY_ID, NAME, NOW);

            /* When & Then */
            assertThatThrownBy (() -> categoryService.partialUpdate (null, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when partial update request is null")
        void partialUpdate_withNullRequest_ShouldThrowException () {
            /* Given */
            CategoryRequestDto requestDto = null;

            /* When & Then */
            assertThatThrownBy (() -> categoryService.partialUpdate (CATEGORY_ID, requestDto))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("Request cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when partial updating missing category")
        void partialUpdate_withNonExistentId_ShouldThrowException () {
            /* Given */
            CategoryRequestDto requestDto = new CategoryRequestDto (CATEGORY_ID, NAME, NOW);
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (Optional.empty ());

            /* When & Then */
            assertThatThrownBy (() -> categoryService.partialUpdate (CATEGORY_ID, requestDto))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findById()
    ══════════════════════════════════════════════════════════════════════ */
    @Nested
    @DisplayName ("findById() — Find an actor by ID")
    class FindByIdCategory {

        @Test
        @DisplayName ("Should find an existing category by ID successfully")
        void findById_withValidId_ShouldReturnCategoryResponse () {
            /* Given */
            Category existingCategory = buildCategory ();
            CategoryResponseDto expectedResponse = buildCategoryResponseDto ();

            /* When */
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (Optional.of (existingCategory));
            when (categoryMapper.toResponseDto (existingCategory)).thenReturn (expectedResponse);

            CategoryResponseDto actualResponse = categoryService.findById (CATEGORY_ID);

            assertThat (actualResponse).usingRecursiveComparison ().isEqualTo (expectedResponse);

            // Verify interactions with mocks
            verify (categoryRepository).findById (CATEGORY_ID);
            verify (categoryMapper).toResponseDto (existingCategory);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void findById_withNullId_ShouldThrowException () {
            /* Given */
            Short nullId = null;

            /* When & Then */
            assertThatThrownBy (() -> categoryService.findById (nullId))
                    .isInstanceOf (IllegalArgumentException.class)
                    .hasMessage ("ID cannot be null");
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when category ID is not found")
        void findById_withNonExistentId_ShouldThrowException () {
            /* Given */
            when (categoryRepository.findById (CATEGORY_ID)).thenReturn (Optional.empty ());

            /* When & Then */
            assertThatThrownBy (() -> categoryService.findById (CATEGORY_ID))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findAll() — unpaged
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findAll() — Find all Categories (unpaged)")
    class FindAllCategoriesUnpaged {

        @Test
        @DisplayName ("Should find all Categories successfully")
        void findAll_withValidRequest_ShouldReturnAllCategories () {
            /* Given */
            List<Category> existingCategories = List.of (buildCategory (), buildCategory ());
            List<CategoryResponseDto> expectedResponses = List.of (buildCategoryResponseDto (), buildCategoryResponseDto ());

            /* When */
            when (categoryRepository.findAll ()).thenReturn (existingCategories);
            when (categoryMapper.toResponseDto (existingCategories.get (0))).thenReturn (expectedResponses.get (0));
            when (categoryMapper.toResponseDto (existingCategories.get (1))).thenReturn (expectedResponses.get (1));

            List<CategoryResponseDto> actualResponses = categoryService.findAll ();

            /* Then */
            assertThat (actualResponses).usingRecursiveComparison ().isEqualTo (expectedResponses);

            // Verify interactions with mocks
            verify (categoryRepository).findAll ();
            verify (categoryMapper).toResponseDto (existingCategories.get (0));
            verify (categoryMapper).toResponseDto (existingCategories.get (1));
        }

        @Test
        @DisplayName ("Should return empty list when no categories in repository")
        void findAll_withNoCategories_ShouldReturnEmptyList () {
            /* Given */
            List<Category> emptyCategories = List.of ();

            /* When */
            when (categoryRepository.findAll ()).thenReturn (emptyCategories);

            List<CategoryResponseDto> actualResponses = categoryService.findAll ();

            /* Then */
            assertThat (actualResponses).isEmpty ();

            // Verify interactions with mocks
            verify (categoryRepository).findAll ();
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       findAll(Pageable)
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("findAll(Pageable) — Find all categories (paged)")
    class FindAllCategoriesPaged {

        @Test
        @DisplayName ("Should find all Categories with pagination successfully")
        void findAll_withPagination_ShouldReturnPagedCategories () {
            /* Given */
            Pageable pageable = PageRequest.of (0, 2);
            List<Category> existingCategories = List.of (buildCategory (), buildCategory ());
            Page<Category> pagedCategories = new PageImpl<> (existingCategories, pageable, existingCategories.size ());
            List<CategoryResponseDto> expectedResponses = List.of (buildCategoryResponseDto (), buildCategoryResponseDto ());

            /* When */
            when (categoryRepository.findAll (pageable)).thenReturn (pagedCategories);
            when (categoryMapper.toResponseDto (existingCategories.get (0))).thenReturn (expectedResponses.get (0));
            when (categoryMapper.toResponseDto (existingCategories.get (1))).thenReturn (expectedResponses.get (1));

            Page<CategoryResponseDto> actualPage = categoryService.findAll (pageable);

            /* Then */
            assertThat (actualPage.getContent ()).usingRecursiveComparison ().isEqualTo (expectedResponses);
            assertThat (actualPage.getTotalElements ()).isEqualTo (existingCategories.size ());

            // Verify interactions with mocks
            verify (categoryRepository).findAll (pageable);
            verify (categoryMapper).toResponseDto (existingCategories.get (0));
            verify (categoryMapper).toResponseDto (existingCategories.get (1));
        }

        @Test
        @DisplayName ("Should return empty page when no categories in repository")
        void findAll_withPaginationAndNoCategories_ShouldReturnEmptyPage () {
            /* Given */
            Pageable pageable = PageRequest.of (0, 2);
            List<Category> emptyCategories = List.of ();
            Page<Category> emptyPagedCategories = new PageImpl<> (emptyCategories, pageable, 0);

            /* When */
            when (categoryRepository.findAll (pageable)).thenReturn (emptyPagedCategories);

            Page<CategoryResponseDto> actualPage = categoryService.findAll (pageable);

            /* Then */
            assertThat (actualPage.getContent ()).isEmpty ();
            assertThat (actualPage.getTotalElements ()).isZero ();

            // Verify interactions with mocks
            verify (categoryRepository).findAll (pageable);
        }
    }


    /* ══════════════════════════════════════════════════════════════════════
       delete()
    ══════════════════════════════════════════════════════════════════════ */

    @Nested
    @DisplayName ("delete() — Delete an Category by ID")
    class DeleteCategoryById {

        @Test
        @DisplayName ("Should delete an existing category by ID successfully")
        void delete_withValidId_ShouldDeleteCategory () {

            /* Given */
            Short categoryId = CATEGORY_ID;

            /* When */
            when (categoryRepository.existsById (categoryId)).thenReturn (true);

            categoryService.delete (categoryId);

            /* Then */
            verify (categoryRepository).existsById (categoryId);
            verify (categoryRepository).deleteById (categoryId);
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when ID is null")
        void delete_withNullId_ShouldThrowResourceNotFoundException () {
            /* Given */
            Short categoryId = null;

            /* When / Then */
            assertThatThrownBy (() -> categoryService.delete (categoryId))
                    .isInstanceOf (ResourceNotFoundException.class);
        }

        @Test
        @DisplayName ("Should throw ResourceNotFoundException when category does not exist")
        void delete_withNonExistentId_ShouldThrowResourceNotFoundException () {
            /* Given */
            Short categoryId = CATEGORY_ID;

            /* When / Then */
            when (categoryRepository.existsById (categoryId)).thenReturn (false);

            assertThatThrownBy (() -> categoryService.delete (categoryId))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       existsById()
    ══════════════════════════════════════════════════════════════════════ */
    @Nested
    @DisplayName ("existsById() — Check if an category exists by ID")
    class ExistsCategoryById {

        @Test
        @DisplayName ("Should return true when category exists by ID")
        void existsById_withExistingId_ShouldReturnTrue () {
            /* Given */
            Short categoryId = CATEGORY_ID;

            /* When */
            when (categoryRepository.existsById (categoryId)).thenReturn (true);

            boolean exists = categoryService.existsById (categoryId);

            /* Then */
            assertThat (exists).isTrue ();
            verify (categoryRepository).existsById (categoryId);
        }

        @Test
        @DisplayName ("Should return false when category does not exist by ID")
        void existsById_withNonExistentId_ShouldReturnFalse () {
            /* Given */
            Short categoryId = CATEGORY_ID;

            /* When */
            when (categoryRepository.existsById (categoryId)).thenReturn (false);

            boolean exists = categoryService.existsById (categoryId);

            /* Then */
            assertThat (exists).isFalse ();
            verify (categoryRepository).existsById (categoryId);
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when ID is null")
        void existsById_withNullId_ShouldThrowIllegalArgumentException () {
            /* Given */
            Short categoryId = null;

            /* When / Then */
            assertThatThrownBy (() -> categoryService.existsById (categoryId))
                    .isInstanceOf (IllegalArgumentException.class);
        }
    }

    /* ══════════════════════════════════════════════════════════════════════
       count()
    ══════════════════════════════════════════════════════════════════════ */
    @Nested
    @DisplayName ("count() — Count total number of categories")
    class CountCategories {

        @Test
        @DisplayName ("Should return the total number of categories")
        void count_ShouldReturnTotalNumberOfCategories () {
            /* Given */
            long expectedCount = 5L;

            /* When */
            when (categoryRepository.count ()).thenReturn (expectedCount);

            long actualCount = categoryService.count ();

            /* Then */
            assertThat (actualCount).isEqualTo (expectedCount);
            verify (categoryRepository).count ();
        }

        @Test
        @DisplayName ("Should throw IllegalArgumentException when count is negative")
        void count_withNegativeCount_ShouldThrowIllegalArgumentException () {
            /* Given */
            long negativeCount = -1L;
            when (categoryRepository.count ()).thenReturn (negativeCount);

            /* When / Then */
            assertThatThrownBy (categoryService::count)
                    .isInstanceOf (IllegalArgumentException.class);
        }
    }
}

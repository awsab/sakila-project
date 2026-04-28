package com.me.learning.parent.inventoryservice.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.inventoryservice.dto.request.CategoryRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.CategoryResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.CategoryUpdateDTO;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service interface for Category entity operations
 * Since    : Version 1.0
 */
public interface CategoryService {

    /**
     * Create a new category
     *
     * @param requestDTO Category creation request
     *
     * @return Created category response
     */
    CategoryResponseDTO createCategory (CategoryRequestDTO requestDTO);

    /**
     * Update an existing category
     *
     * @param id        Category ID
     * @param updateDTO Category update request
     *
     * @return Updated category response
     */
    CategoryResponseDTO updateCategory (Short id, CategoryUpdateDTO updateDTO);

    /**
     * Partially update an existing category
     *
     * @param id        Category ID
     * @param updateDTO Category update request
     *
     * @return Updated category response
     */
    CategoryResponseDTO patchCategory (Short id, CategoryUpdateDTO updateDTO);

    /**
     * Get category by ID
     *
     * @param id Category ID
     *
     * @return Category response
     */
    CategoryResponseDTO getCategoryById (Short id);

    /**
     * Get category by name
     *
     * @param name Category name
     *
     * @return Category response
     */
    CategoryResponseDTO getCategoryByName (String name);

    /**
     * Get all categories
     *
     * @return List of all categories
     */
    List<CategoryResponseDTO> getAllCategories ();

    /**
     * Get all categories with pagination
     *
     * @param pageable Pagination information
     *
     * @return Page of categories
     */
    Page<CategoryResponseDTO> getAllCategories (Pageable pageable);

    /**
     * Search categories by name
     *
     * @param name Name to search
     *
     * @return List of matching categories
     */
    List<CategoryResponseDTO> searchCategoriesByName (String name);

    /**
     * Get all categories sorted by name
     *
     * @return List of categories sorted by name
     */
    List<CategoryResponseDTO> getAllCategoriesSortedByName ();

    /**
     * Get categories by film ID
     *
     * @param filmId Film ID
     *
     * @return List of categories
     */
    List<CategoryResponseDTO> getCategoriesByFilmId (Integer filmId);

    /**
     * Count films in a category
     *
     * @param categoryId Category ID
     *
     * @return Count of films
     */
    long countFilmsByCategory (Short categoryId);

    /**
     * Check if category exists by ID
     *
     * @param id Category ID
     *
     * @return true if exists, false otherwise
     */
    boolean existsById (Short id);

    /**
     * Check if category exists by name
     *
     * @param name Category name
     *
     * @return true if exists, false otherwise
     */
    boolean existsByName (String name);

    /**
     * Delete category by ID
     *
     * @param id Category ID
     */
    void deleteCategory (Short id);

    /**
     * Count total categories
     *
     * @return Total count of categories
     */
    long countCategories ();
}


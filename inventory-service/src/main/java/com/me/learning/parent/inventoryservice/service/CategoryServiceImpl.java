package com.me.learning.parent.inventoryservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 * Date     : 11/03/2026
 * Usage    : Service implementation for Category entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_RESOURCE = "Category";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory (CategoryRequestDTO requestDTO) {
        log.debug ("Creating new category: {}", requestDTO.getName ());

        if ( categoryRepository.existsByName (requestDTO.getName ()) ) {
            throw new DuplicateResourceException (CATEGORY_RESOURCE, FIELD_NAME, requestDTO.getName ());
        }

        Category category = categoryMapper.toEntity (requestDTO);
        Category savedCategory = categoryRepository.save (category);

        log.info ("Created category with ID: {}", savedCategory.getId ());
        return categoryMapper.toDto (savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory (Short id, CategoryUpdateDTO updateDTO) {
        log.debug ("Updating category with ID: {}", id);

        Category existingCategory = categoryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CATEGORY_RESOURCE, FIELD_ID, id));

        // Check if name change conflicts with existing category
        if ( !existingCategory.getName ().equals (updateDTO.getName ()) &&
                categoryRepository.existsByName (updateDTO.getName ()) ) {
            throw new DuplicateResourceException (CATEGORY_RESOURCE, FIELD_NAME, updateDTO.getName ());
        }

        existingCategory.setName (updateDTO.getName ());

        Category updatedCategory = categoryRepository.save (existingCategory);

        log.info ("Updated category with ID: {}", id);
        return categoryMapper.toDto (updatedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDTO patchCategory (Short id, CategoryUpdateDTO updateDTO) {
        log.debug ("Partially updating category with ID: {}", id);

        Category existingCategory = categoryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CATEGORY_RESOURCE, FIELD_ID, id));

        categoryMapper.updateEntity (updateDTO, existingCategory);

        Category updatedCategory = categoryRepository.save (existingCategory);

        log.info ("Patched category with ID: {}", id);
        return categoryMapper.toDto (updatedCategory);
    }

    @Override
    public CategoryResponseDTO getCategoryById (Short id) {
        log.debug ("Fetching category with ID: {}", id);

        Category category = categoryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CATEGORY_RESOURCE, FIELD_ID, id));

        return categoryMapper.toDto (category);
    }

    @Override
    public CategoryResponseDTO getCategoryByName (String name) {
        log.debug ("Fetching category with name: {}", name);

        Category category = categoryRepository.findByName (name)
                .orElseThrow (() -> new ResourceNotFoundException (CATEGORY_RESOURCE, FIELD_NAME, name));

        return categoryMapper.toDto (category);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories () {
        log.debug ("Fetching all categories");

        List<Category> categories = categoryRepository.findAll ();
        return categoryMapper.toDtoList (categories);
    }

    @Override
    public Page<CategoryResponseDTO> getAllCategories (Pageable pageable) {
        log.debug ("Fetching categories with pagination: page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());

        Page<Category> categoryPage = categoryRepository.findAll (pageable);
        return categoryPage.map (categoryMapper::toDto);
    }

    @Override
    public List<CategoryResponseDTO> searchCategoriesByName (String name) {
        log.debug ("Searching categories by name: {}", name);

        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase (name);
        return categoryMapper.toDtoList (categories);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategoriesSortedByName () {
        log.debug ("Fetching all categories sorted by name");

        List<Category> categories = categoryRepository.findAllByOrderByNameAsc ();
        return categoryMapper.toDtoList (categories);
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByFilmId (Integer filmId) {
        log.debug ("Fetching categories by film ID: {}", filmId);

        List<Category> categories = categoryRepository.findByFilmId (filmId);
        return categoryMapper.toDtoList (categories);
    }

    @Override
    public long countFilmsByCategory (Short categoryId) {
        log.debug ("Counting films in category ID: {}", categoryId);
        return categoryRepository.countFilmsByCategoryId (categoryId);
    }

    @Override
    public boolean existsById (Short id) {
        log.debug ("Checking if category exists with ID: {}", id);
        return categoryRepository.existsById (id);
    }

    @Override
    public boolean existsByName (String name) {
        log.debug ("Checking if category exists with name: {}", name);
        return categoryRepository.existsByName (name);
    }

    @Override
    @Transactional
    public void deleteCategory (Short id) {
        log.debug ("Deleting category with ID: {}", id);

        if ( !categoryRepository.existsById (id) ) {
            throw new ResourceNotFoundException (CATEGORY_RESOURCE, FIELD_ID, id);
        }

        categoryRepository.deleteById (id);
        log.info ("Deleted category with ID: {}", id);
    }

    @Override
    public long countCategories () {
        log.debug ("Counting total categories");
        return categoryRepository.count ();
    }
}

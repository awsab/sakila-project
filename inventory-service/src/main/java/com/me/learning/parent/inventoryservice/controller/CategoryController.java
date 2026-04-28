package com.me.learning.parent.inventoryservice.controller;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.parent.inventoryservice.dto.request.CategoryRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.CategoryResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.CategoryUpdateDTO;
import com.me.learning.parent.inventoryservice.service.CategoryService;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : REST Controller for Category operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/categories")
@RequiredArgsConstructor
@Tag (name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation (summary = "Create a new category", description = "Creates a new category in the system")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "201", description = "Category created successfully",
                    content = @Content (schema = @Schema (implementation = CategoryResponseDTO.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Category already exists")
    })
    public ResponseEntity<CategoryResponseDTO> createCategory (
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        log.info ("REST request to create Category: {}", requestDTO.getName ());
        CategoryResponseDTO response = categoryService.createCategory (requestDTO);
        return ResponseEntity.status (HttpStatus.CREATED).body (response);
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Update a category", description = "Updates an existing category by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Category updated successfully"),
            @ApiResponse (responseCode = "404", description = "Category not found"),
            @ApiResponse (responseCode = "409", description = "Duplicate category name")
    })
    public ResponseEntity<CategoryResponseDTO> updateCategory (
            @Parameter (description = "Category ID") @PathVariable Short id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        log.info ("REST request to update Category with ID: {}", id);
        CategoryResponseDTO response = categoryService.updateCategory (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a category", description = "Partially updates a category by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Category updated successfully"),
            @ApiResponse (responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponseDTO> patchCategory (
            @Parameter (description = "Category ID") @PathVariable Short id,
            @RequestBody CategoryUpdateDTO updateDTO) {
        log.info ("REST request to patch Category with ID: {}", id);
        CategoryResponseDTO response = categoryService.patchCategory (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get category by ID", description = "Retrieves a category by its ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Category found"),
            @ApiResponse (responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponseDTO> getCategoryById (
            @Parameter (description = "Category ID") @PathVariable Short id) {
        log.info ("REST request to get Category with ID: {}", id);
        CategoryResponseDTO response = categoryService.getCategoryById (id);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/name/{name}")
    @Operation (summary = "Get category by name", description = "Retrieves a category by its name")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Category found"),
            @ApiResponse (responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponseDTO> getCategoryByName (
            @Parameter (description = "Category name") @PathVariable String name) {
        log.info ("REST request to get Category with name: {}", name);
        CategoryResponseDTO response = categoryService.getCategoryByName (name);
        return ResponseEntity.ok (response);
    }

    @GetMapping
    @Operation (summary = "Get all categories", description = "Retrieves all categories with optional pagination")
    @ApiResponse (responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Categories with pagination");
        Page<CategoryResponseDTO> response = categoryService.getAllCategories (pageable);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/search")
    @Operation (summary = "Search categories by name", description = "Searches categories by name")
    @ApiResponse (responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategories (
            @Parameter (description = "Search term") @RequestParam String searchTerm) {
        log.info ("REST request to search Categories: {}", searchTerm);
        List<CategoryResponseDTO> response = categoryService.searchCategoriesByName (searchTerm);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/sorted")
    @Operation (summary = "Get categories sorted by name", description = "Retrieves all categories sorted by name")
    @ApiResponse (responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategoriesSorted () {
        log.info ("REST request to get all Categories sorted by name");
        List<CategoryResponseDTO> response = categoryService.getAllCategoriesSortedByName ();
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-film/{filmId}")
    @Operation (summary = "Get categories by film", description = "Retrieves categories for a specific film")
    @ApiResponse (responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByFilm (
            @Parameter (description = "Film ID") @PathVariable Integer filmId) {
        log.info ("REST request to get Categories by film ID: {}", filmId);
        List<CategoryResponseDTO> response = categoryService.getCategoriesByFilmId (filmId);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/{id}/film-count")
    @Operation (summary = "Count films in category", description = "Returns count of films in a specific category")
    @ApiResponse (responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countFilmsByCategory (
            @Parameter (description = "Category ID") @PathVariable Short id) {
        log.info ("REST request to count films in Category ID: {}", id);
        long count = categoryService.countFilmsByCategory (id);
        return ResponseEntity.ok (count);
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a category", description = "Deletes a category by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse (responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory (
            @Parameter (description = "Category ID") @PathVariable Short id) {
        log.info ("REST request to delete Category with ID: {}", id);
        categoryService.deleteCategory (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total categories", description = "Returns the total count of categories")
    @ApiResponse (responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countCategories () {
        log.info ("REST request to count all Categories");
        long count = categoryService.countCategories ();
        return ResponseEntity.ok (count);
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if category exists", description = "Checks if a category exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Category ID") @PathVariable Short id) {
        log.info ("REST request to check if Category exists with ID: {}", id);
        boolean exists = categoryService.existsById (id);
        return ResponseEntity.ok (exists);
    }

    @GetMapping ("/exists/name/{name}")
    @Operation (summary = "Check if category exists by name", description = "Checks if a category exists by name")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsByName (
            @Parameter (description = "Category name") @PathVariable String name) {
        log.info ("REST request to check if Category exists with name: {}", name);
        boolean exists = categoryService.existsByName (name);
        return ResponseEntity.ok (exists);
    }
}


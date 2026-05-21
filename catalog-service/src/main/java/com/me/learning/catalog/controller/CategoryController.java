package com.me.learning.catalog.controller;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;
import com.me.learning.catalog.service.CategoryService;

@Slf4j
@RestController
@RequestMapping ("/api/v1/categories")
@RequiredArgsConstructor
@Tag (name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation (summary = "Create a new category")
    @ApiResponse (
            responseCode = "201", description = "Category created successfully",
            content = @Content (schema = @Schema (implementation = CategoryResponseDto.class)))
    @ApiResponse (responseCode = "400", description = "Validation error")
    public ResponseEntity<CategoryResponseDto> create (
            @Valid @RequestBody CategoryRequestDto dto) {
        log.info ("REST POST /api/v1/categories - creating category: {}", dto.name ());
        return ResponseEntity.status (HttpStatus.CREATED).body (categoryService.create (dto));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a category")
    @ApiResponse (
            responseCode = "200", description = "Category updated successfully",
            content = @Content (schema = @Schema (implementation = CategoryResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryResponseDto> update (
            @Parameter (description = "Category primary key") @PathVariable Short id,
            @Valid @RequestBody CategoryRequestDto dto) {
        log.info ("REST PUT /api/v1/categories/{} - full update", id);
        return ResponseEntity.ok (categoryService.update (id, dto));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a category")
    @ApiResponse (
            responseCode = "200", description = "Category patched successfully",
            content = @Content (schema = @Schema (implementation = CategoryResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryResponseDto> partialUpdate (
            @Parameter (description = "Category primary key") @PathVariable Short id,
            @RequestBody CategoryRequestDto dto) {
        log.info ("REST PATCH /api/v1/categories/{} - partial update", id);
        return ResponseEntity.ok (categoryService.partialUpdate (id, dto));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a category by ID")
    @ApiResponse (
            responseCode = "200", description = "Category found",
            content = @Content (schema = @Schema (implementation = CategoryResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryResponseDto> findById (
            @Parameter (description = "Category primary key") @PathVariable Short id) {
        log.info ("REST GET /api/v1/categories/{}", id);
        return ResponseEntity.ok (categoryService.findById (id));
    }

    @GetMapping
    @Operation (summary = "List all categories (paginated)")
    @ApiResponse (responseCode = "200", description = "Categories retrieved")
    public ResponseEntity<Page<CategoryResponseDto>> findAll (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST GET /api/v1/categories - page {}, size {}", pageable.getPageNumber (), pageable.getPageSize ());
        return ResponseEntity.ok (categoryService.findAll (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "List all categories (unpaged)")
    @ApiResponse (responseCode = "200", description = "All categories retrieved")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories () {
        log.info ("REST GET /api/v1/categories/all");
        return ResponseEntity.ok (categoryService.findAll ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a category by ID")
    @ApiResponse (responseCode = "204", description = "Category deleted successfully")
    @ApiResponse (responseCode = "404", description = "Category not found")
    public ResponseEntity<Void> delete (
            @Parameter (description = "Category primary key") @PathVariable Short id) {
        log.info ("REST DELETE /api/v1/categories/{}", id);
        categoryService.delete (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total number of categories")
    @ApiResponse (responseCode = "200", description = "Count returned")
    public ResponseEntity<Long> count () {
        return ResponseEntity.ok (categoryService.count ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check whether a category exists by ID")
    @ApiResponse (responseCode = "200", description = "Existence flag returned")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Category primary key") @PathVariable Short id) {
        return ResponseEntity.ok (categoryService.existsById (id));
    }
}


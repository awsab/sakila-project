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

import com.me.learning.parent.inventoryservice.dto.request.LanguageRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.LanguageResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.LanguageUpdateDTO;
import com.me.learning.parent.inventoryservice.service.LanguageService;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : REST Controller for Language operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/languages")
@RequiredArgsConstructor
@Tag (name = "Language", description = "Language management APIs")
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping
    @Operation (summary = "Create a new language", description = "Creates a new language in the system")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "201", description = "Language created successfully",
                    content = @Content (schema = @Schema (implementation = LanguageResponseDTO.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Language already exists")
    })
    public ResponseEntity<LanguageResponseDTO> createLanguage (
            @Valid @RequestBody LanguageRequestDTO requestDTO) {
        log.info ("REST request to create Language: {}", requestDTO.getName ());
        LanguageResponseDTO response = languageService.createLanguage (requestDTO);
        return ResponseEntity.status (HttpStatus.CREATED).body (response);
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Update a language", description = "Updates an existing language by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Language updated successfully"),
            @ApiResponse (responseCode = "404", description = "Language not found"),
            @ApiResponse (responseCode = "409", description = "Duplicate language name")
    })
    public ResponseEntity<LanguageResponseDTO> updateLanguage (
            @Parameter (description = "Language ID") @PathVariable Short id,
            @Valid @RequestBody LanguageUpdateDTO updateDTO) {
        log.info ("REST request to update Language with ID: {}", id);
        LanguageResponseDTO response = languageService.updateLanguage (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a language", description = "Partially updates a language by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Language updated successfully"),
            @ApiResponse (responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponseDTO> patchLanguage (
            @Parameter (description = "Language ID") @PathVariable Short id,
            @RequestBody LanguageUpdateDTO updateDTO) {
        log.info ("REST request to patch Language with ID: {}", id);
        LanguageResponseDTO response = languageService.patchLanguage (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get language by ID", description = "Retrieves a language by its ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Language found"),
            @ApiResponse (responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponseDTO> getLanguageById (
            @Parameter (description = "Language ID") @PathVariable Short id) {
        log.info ("REST request to get Language with ID: {}", id);
        LanguageResponseDTO response = languageService.getLanguageById (id);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/name/{name}")
    @Operation (summary = "Get language by name", description = "Retrieves a language by its name")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Language found"),
            @ApiResponse (responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponseDTO> getLanguageByName (
            @Parameter (description = "Language name") @PathVariable String name) {
        log.info ("REST request to get Language with name: {}", name);
        LanguageResponseDTO response = languageService.getLanguageByName (name);
        return ResponseEntity.ok (response);
    }

    @GetMapping
    @Operation (summary = "Get all languages", description = "Retrieves all languages with optional pagination")
    @ApiResponse (responseCode = "200", description = "Languages retrieved successfully")
    public ResponseEntity<Page<LanguageResponseDTO>> getAllLanguages (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Languages with pagination");
        Page<LanguageResponseDTO> response = languageService.getAllLanguages (pageable);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/search")
    @Operation (summary = "Search languages by name", description = "Searches languages by name")
    @ApiResponse (responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<LanguageResponseDTO>> searchLanguages (
            @Parameter (description = "Search term") @RequestParam String searchTerm) {
        log.info ("REST request to search Languages: {}", searchTerm);
        List<LanguageResponseDTO> response = languageService.searchLanguagesByName (searchTerm);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/sorted")
    @Operation (summary = "Get languages sorted by name", description = "Retrieves all languages sorted by name")
    @ApiResponse (responseCode = "200", description = "Languages retrieved successfully")
    public ResponseEntity<List<LanguageResponseDTO>> getAllLanguagesSorted () {
        log.info ("REST request to get all Languages sorted by name");
        List<LanguageResponseDTO> response = languageService.getAllLanguagesSortedByName ();
        return ResponseEntity.ok (response);
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a language", description = "Deletes a language by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "204", description = "Language deleted successfully"),
            @ApiResponse (responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<Void> deleteLanguage (
            @Parameter (description = "Language ID") @PathVariable Short id) {
        log.info ("REST request to delete Language with ID: {}", id);
        languageService.deleteLanguage (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total languages", description = "Returns the total count of languages")
    @ApiResponse (responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countLanguages () {
        log.info ("REST request to count all Languages");
        long count = languageService.countLanguages ();
        return ResponseEntity.ok (count);
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if language exists", description = "Checks if a language exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Language ID") @PathVariable Short id) {
        log.info ("REST request to check if Language exists with ID: {}", id);
        boolean exists = languageService.existsById (id);
        return ResponseEntity.ok (exists);
    }

    @GetMapping ("/exists/name/{name}")
    @Operation (summary = "Check if language exists by name", description = "Checks if a language exists by name")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsByName (
            @Parameter (description = "Language name") @PathVariable String name) {
        log.info ("REST request to check if Language exists with name: {}", name);
        boolean exists = languageService.existsByName (name);
        return ResponseEntity.ok (exists);
    }
}


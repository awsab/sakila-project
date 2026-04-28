package com.me.learning.parent.inventoryservice.controller;


import java.math.BigDecimal;
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

import com.me.learning.parent.inventoryservice.dto.request.FilmRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.FilmResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.FilmUpdateDTO;
import com.me.learning.parent.inventoryservice.service.FilmService;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : REST Controller for Film operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/films")
@RequiredArgsConstructor
@Tag (name = "Film", description = "Film management APIs")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    @Operation (summary = "Create a new film", description = "Creates a new film in the system")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "201", description = "Film created successfully",
                    content = @Content (schema = @Schema (implementation = FilmResponseDTO.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Film already exists")
    })
    public ResponseEntity<FilmResponseDTO> createFilm (
            @Valid @RequestBody FilmRequestDTO requestDTO) {
        log.info ("REST request to create Film: {}", requestDTO.getTitle ());
        FilmResponseDTO response = filmService.createFilm (requestDTO);
        return ResponseEntity.status (HttpStatus.CREATED).body (response);
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Update a film", description = "Updates an existing film by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Film updated successfully"),
            @ApiResponse (responseCode = "404", description = "Film not found"),
            @ApiResponse (responseCode = "409", description = "Duplicate film title")
    })
    public ResponseEntity<FilmResponseDTO> updateFilm (
            @Parameter (description = "Film ID") @PathVariable Integer id,
            @Valid @RequestBody FilmUpdateDTO updateDTO) {
        log.info ("REST request to update Film with ID: {}", id);
        FilmResponseDTO response = filmService.updateFilm (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a film", description = "Partially updates a film by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Film updated successfully"),
            @ApiResponse (responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<FilmResponseDTO> patchFilm (
            @Parameter (description = "Film ID") @PathVariable Integer id,
            @RequestBody FilmUpdateDTO updateDTO) {
        log.info ("REST request to patch Film with ID: {}", id);
        FilmResponseDTO response = filmService.patchFilm (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get film by ID", description = "Retrieves a film by its ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Film found"),
            @ApiResponse (responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<FilmResponseDTO> getFilmById (
            @Parameter (description = "Film ID") @PathVariable Integer id) {
        log.info ("REST request to get Film with ID: {}", id);
        FilmResponseDTO response = filmService.getFilmById (id);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/title/{title}")
    @Operation (summary = "Get film by title", description = "Retrieves a film by its title")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Film found"),
            @ApiResponse (responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<FilmResponseDTO> getFilmByTitle (
            @Parameter (description = "Film title") @PathVariable String title) {
        log.info ("REST request to get Film with title: {}", title);
        FilmResponseDTO response = filmService.getFilmByTitle (title);
        return ResponseEntity.ok (response);
    }

    @GetMapping
    @Operation (summary = "Get all films", description = "Retrieves all films with optional pagination")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<Page<FilmResponseDTO>> getAllFilms (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Films with pagination");
        Page<FilmResponseDTO> response = filmService.getAllFilms (pageable);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/search/title")
    @Operation (summary = "Search films by title", description = "Searches films by title")
    @ApiResponse (responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<FilmResponseDTO>> searchByTitle (
            @Parameter (description = "Title search term") @RequestParam String title) {
        log.info ("REST request to search Films by title: {}", title);
        List<FilmResponseDTO> response = filmService.searchFilmsByTitle (title);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/search")
    @Operation (summary = "Search films by title or description", description = "Searches films by title or description")
    @ApiResponse (responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<FilmResponseDTO>> searchFilms (
            @Parameter (description = "Search term") @RequestParam String searchTerm) {
        log.info ("REST request to search Films: {}", searchTerm);
        List<FilmResponseDTO> response = filmService.searchFilmsByTitleOrDescription (searchTerm);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-release-year/{year}")
    @Operation (summary = "Get films by release year", description = "Retrieves films by release year")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<List<FilmResponseDTO>> getFilmsByReleaseYear (
            @Parameter (description = "Release year") @PathVariable int year) {
        log.info ("REST request to get Films by release year: {}", year);
        List<FilmResponseDTO> response = filmService.getFilmsByReleaseYear (year);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-rating/{rating}")
    @Operation (summary = "Get films by rating", description = "Retrieves films by rating (G, PG, PG-13, R, NC-17)")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<List<FilmResponseDTO>> getFilmsByRating (
            @Parameter (description = "Film rating") @PathVariable String rating) {
        log.info ("REST request to get Films by rating: {}", rating);
        List<FilmResponseDTO> response = filmService.getFilmsByRating (rating);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-rating/{rating}/paginated")
    @Operation (summary = "Get films by rating with pagination", description = "Retrieves films by rating with pagination")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<Page<FilmResponseDTO>> getFilmsByRatingPaginated (
            @Parameter (description = "Film rating") @PathVariable String rating,
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get Films by rating with pagination: {}", rating);
        Page<FilmResponseDTO> response = filmService.getFilmsByRating (rating, pageable);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-rental-rate")
    @Operation (summary = "Get films by max rental rate", description = "Retrieves films with rental rate less than or equal to max rate")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<List<FilmResponseDTO>> getFilmsByRentalRate (
            @Parameter (description = "Maximum rental rate") @RequestParam BigDecimal maxRate) {
        log.info ("REST request to get Films by max rental rate: {}", maxRate);
        List<FilmResponseDTO> response = filmService.getFilmsByRentalRate (maxRate);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-length")
    @Operation (summary = "Get films by length range", description = "Retrieves films within specified length range")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<List<FilmResponseDTO>> getFilmsByLengthRange (
            @Parameter (description = "Minimum length") @RequestParam Integer minLength,
            @Parameter (description = "Maximum length") @RequestParam Integer maxLength) {
        log.info ("REST request to get Films by length range: {}-{}", minLength, maxLength);
        List<FilmResponseDTO> response = filmService.getFilmsByLengthRange (minLength, maxLength);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-category/{categoryId}")
    @Operation (summary = "Get films by category", description = "Retrieves films in a specific category")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<List<FilmResponseDTO>> getFilmsByCategory (
            @Parameter (description = "Category ID") @PathVariable Short categoryId) {
        log.info ("REST request to get Films by category ID: {}", categoryId);
        List<FilmResponseDTO> response = filmService.getFilmsByCategoryId (categoryId);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-actor/{actorId}")
    @Operation (summary = "Get films by actor", description = "Retrieves films featuring a specific actor")
    @ApiResponse (responseCode = "200", description = "Films retrieved successfully")
    public ResponseEntity<List<FilmResponseDTO>> getFilmsByActor (
            @Parameter (description = "Actor ID") @PathVariable Integer actorId) {
        log.info ("REST request to get Films by actor ID: {}", actorId);
        List<FilmResponseDTO> response = filmService.getFilmsByActorId (actorId);
        return ResponseEntity.ok (response);
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a film", description = "Deletes a film by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "204", description = "Film deleted successfully"),
            @ApiResponse (responseCode = "404", description = "Film not found")
    })
    public ResponseEntity<Void> deleteFilm (
            @Parameter (description = "Film ID") @PathVariable Integer id) {
        log.info ("REST request to delete Film with ID: {}", id);
        filmService.deleteFilm (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total films", description = "Returns the total count of films")
    @ApiResponse (responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countFilms () {
        log.info ("REST request to count all Films");
        long count = filmService.countFilms ();
        return ResponseEntity.ok (count);
    }

    @GetMapping ("/count/by-rating/{rating}")
    @Operation (summary = "Count films by rating", description = "Returns count of films with specific rating")
    @ApiResponse (responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countByRating (
            @Parameter (description = "Film rating") @PathVariable String rating) {
        log.info ("REST request to count Films by rating: {}", rating);
        long count = filmService.countFilmsByRating (rating);
        return ResponseEntity.ok (count);
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if film exists", description = "Checks if a film exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Film ID") @PathVariable Integer id) {
        log.info ("REST request to check if Film exists with ID: {}", id);
        boolean exists = filmService.existsById (id);
        return ResponseEntity.ok (exists);
    }

    @GetMapping ("/exists/title/{title}")
    @Operation (summary = "Check if film exists by title", description = "Checks if a film exists by title")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsByTitle (
            @Parameter (description = "Film title") @PathVariable String title) {
        log.info ("REST request to check if Film exists with title: {}", title);
        boolean exists = filmService.existsByTitle (title);
        return ResponseEntity.ok (exists);
    }
}


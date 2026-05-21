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

import com.me.learning.catalog.dto.FilmRequestDto;
import com.me.learning.catalog.dto.FilmResponseDto;
import com.me.learning.catalog.service.FilmService;

@Slf4j
@RestController
@RequestMapping ("/api/v1/films")
@RequiredArgsConstructor
@Tag (name = "Film", description = "Film catalog management APIs")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    @Operation (summary = "Create a new film")
    @ApiResponse (
            responseCode = "201", description = "Film created successfully",
            content = @Content (schema = @Schema (implementation = FilmResponseDto.class)))
    @ApiResponse (responseCode = "400", description = "Validation error")
    @ApiResponse (responseCode = "404", description = "Referenced actor/category/language not found")
    public ResponseEntity<FilmResponseDto> create (
            @Valid @RequestBody FilmRequestDto dto) {
        log.info ("REST POST /api/v1/films - creating film: {}", dto.title ());
        return ResponseEntity.status (HttpStatus.CREATED).body (filmService.create (dto));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a film")
    @ApiResponse (
            responseCode = "200", description = "Film updated successfully",
            content = @Content (schema = @Schema (implementation = FilmResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Film or referenced actor/category/language not found")
    public ResponseEntity<FilmResponseDto> update (
            @Parameter (description = "Film primary key") @PathVariable Integer id,
            @Valid @RequestBody FilmRequestDto dto) {
        log.info ("REST PUT /api/v1/films/{} - full update", id);
        return ResponseEntity.ok (filmService.update (id, dto));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a film")
    @ApiResponse (
            responseCode = "200", description = "Film patched successfully",
            content = @Content (schema = @Schema (implementation = FilmResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Film or referenced actor/category/language not found")
    public ResponseEntity<FilmResponseDto> partialUpdate (
            @Parameter (description = "Film primary key") @PathVariable Integer id,
            @RequestBody FilmRequestDto dto) {
        log.info ("REST PATCH /api/v1/films/{} - partial update", id);
        return ResponseEntity.ok (filmService.partialUpdate (id, dto));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a film by ID")
    @ApiResponse (
            responseCode = "200", description = "Film found",
            content = @Content (schema = @Schema (implementation = FilmResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Film not found")
    public ResponseEntity<FilmResponseDto> findById (
            @Parameter (description = "Film primary key") @PathVariable Integer id) {
        log.info ("REST GET /api/v1/films/{}", id);
        return ResponseEntity.ok (filmService.findById (id));
    }

    @GetMapping
    @Operation (summary = "List all films (paginated)")
    @ApiResponse (responseCode = "200", description = "Films retrieved")
    public ResponseEntity<Page<FilmResponseDto>> findAll (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST GET /api/v1/films - page {}, size {}", pageable.getPageNumber (), pageable.getPageSize ());
        return ResponseEntity.ok (filmService.findAll (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "List all films (unpaged)")
    @ApiResponse (responseCode = "200", description = "All films retrieved")
    public ResponseEntity<List<FilmResponseDto>> getAllFilms () {
        log.info ("REST GET /api/v1/films/all");
        return ResponseEntity.ok (filmService.findAll ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a film by ID")
    @ApiResponse (responseCode = "204", description = "Film deleted successfully")
    @ApiResponse (responseCode = "404", description = "Film not found")
    public ResponseEntity<Void> delete (
            @Parameter (description = "Film primary key") @PathVariable Integer id) {
        log.info ("REST DELETE /api/v1/films/{}", id);
        filmService.delete (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total number of films")
    @ApiResponse (responseCode = "200", description = "Count returned")
    public ResponseEntity<Long> count () {
        return ResponseEntity.ok (filmService.count ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check whether a film exists by ID")
    @ApiResponse (responseCode = "200", description = "Existence flag returned")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Film primary key") @PathVariable Integer id) {
        return ResponseEntity.ok (filmService.existsById (id));
    }
}


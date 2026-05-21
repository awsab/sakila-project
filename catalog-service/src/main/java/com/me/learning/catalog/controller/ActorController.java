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

import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;
import com.me.learning.catalog.service.ActorService;

@Slf4j
@RestController
@RequestMapping ("/api/v1/actors")
@RequiredArgsConstructor
@Tag (name = "Actor", description = "Actor management APIs")
public class ActorController {

    private final ActorService actorService;

    @PostMapping
    @Operation (summary = "Create a new actor")
    @ApiResponse (
            responseCode = "201", description = "Actor created successfully",
            content = @Content (schema = @Schema (implementation = ActorResponseDto.class)))
    @ApiResponse (responseCode = "400", description = "Validation error")
    public ResponseEntity<ActorResponseDto> create (
            @Valid @RequestBody ActorRequestDto dto) {
        log.info ("REST POST /api/v1/actors - creating actor: {} {}", dto.firstName (), dto.lastName ());
        return ResponseEntity.status (HttpStatus.CREATED).body (actorService.create (dto));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update an actor")
    @ApiResponse (
            responseCode = "200", description = "Actor updated successfully",
            content = @Content (schema = @Schema (implementation = ActorResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Actor not found")
    public ResponseEntity<ActorResponseDto> update (
            @Parameter (description = "Actor primary key") @PathVariable Integer id,
            @Valid @RequestBody ActorRequestDto dto) {
        log.info ("REST PUT /api/v1/actors/{} - full update", id);
        return ResponseEntity.ok (actorService.update (id, dto));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update an actor")
    @ApiResponse (
            responseCode = "200", description = "Actor patched successfully",
            content = @Content (schema = @Schema (implementation = ActorResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Actor not found")
    public ResponseEntity<ActorResponseDto> partialUpdate (
            @Parameter (description = "Actor primary key") @PathVariable Integer id,
            @RequestBody ActorRequestDto dto) {
        log.info ("REST PATCH /api/v1/actors/{} - partial update", id);
        return ResponseEntity.ok (actorService.partialUpdate (id, dto));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get an actor by ID")
    @ApiResponse (
            responseCode = "200", description = "Actor found",
            content = @Content (schema = @Schema (implementation = ActorResponseDto.class)))
    @ApiResponse (responseCode = "404", description = "Actor not found")
    public ResponseEntity<ActorResponseDto> findById (
            @Parameter (description = "Actor primary key") @PathVariable Integer id) {
        log.info ("REST GET /api/v1/actors/{}", id);
        return ResponseEntity.ok (actorService.findById (id));
    }

    @GetMapping
    @Operation (summary = "List all actors (paginated)")
    @ApiResponse (responseCode = "200", description = "Actors retrieved")
    public ResponseEntity<Page<ActorResponseDto>> findAll (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST GET /api/v1/actors - page {}, size {}", pageable.getPageNumber (), pageable.getPageSize ());
        return ResponseEntity.ok (actorService.findAll (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "List all actors (unpaged)")
    @ApiResponse (responseCode = "200", description = "All actors retrieved")
    public ResponseEntity<List<ActorResponseDto>> getAllActors () {
        log.info ("REST GET /api/v1/actors/all");
        return ResponseEntity.ok (actorService.findAll ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete an actor by ID")
    @ApiResponse (responseCode = "204", description = "Actor deleted successfully")
    @ApiResponse (responseCode = "404", description = "Actor not found")
    public ResponseEntity<Void> delete (
            @Parameter (description = "Actor primary key") @PathVariable Integer id) {
        log.info ("REST DELETE /api/v1/actors/{}", id);
        actorService.delete (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total number of actors")
    @ApiResponse (responseCode = "200", description = "Count returned")
    public ResponseEntity<Long> count () {
        return ResponseEntity.ok (actorService.count ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check whether an actor exists by ID")
    @ApiResponse (responseCode = "200", description = "Existence flag returned")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Actor primary key") @PathVariable Integer id) {
        return ResponseEntity.ok (actorService.existsById (id));
    }
}


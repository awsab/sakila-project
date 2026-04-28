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

import com.me.learning.parent.inventoryservice.dto.request.ActorRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.ActorResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.ActorUpdateDTO;
import com.me.learning.parent.inventoryservice.service.ActorService;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : REST Controller for Actor operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/actors")
@RequiredArgsConstructor
@Tag (name = "Actor", description = "Actor management APIs")
public class ActorController {

    private final ActorService actorService;

    @PostMapping
    @Operation (summary = "Create a new actor", description = "Creates a new actor in the system")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "201", description = "Actor created successfully",
                    content = @Content (schema = @Schema (implementation = ActorResponseDTO.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Actor already exists")
    })
    public ResponseEntity<ActorResponseDTO> createActor (
            @Valid @RequestBody final ActorRequestDTO requestDTO) {
        log.info ("REST request to create Actor: {}", requestDTO);
        ActorResponseDTO response = actorService.createActor (requestDTO);
        return ResponseEntity.status (HttpStatus.CREATED).body (response);
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Update an actor", description = "Updates an existing actor by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Actor updated successfully"),
            @ApiResponse (responseCode = "404", description = "Actor not found"),
            @ApiResponse (responseCode = "409", description = "Duplicate actor name")
    })
    public ResponseEntity<ActorResponseDTO> updateActor (
            @Parameter (description = "Actor ID") @PathVariable Integer id,
            @Valid @RequestBody ActorUpdateDTO updateDTO) {
        log.info ("REST request to update Actor with ID: {}", id);
        ActorResponseDTO response = actorService.updateActor (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update an actor", description = "Partially updates an actor by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Actor updated successfully"),
            @ApiResponse (responseCode = "404", description = "Actor not found")
    })
    public ResponseEntity<ActorResponseDTO> patchActor (
            @Parameter (description = "Actor ID") @PathVariable Integer id,
            @RequestBody ActorUpdateDTO updateDTO) {
        log.info ("REST request to patch Actor with ID: {}", id);
        ActorResponseDTO response = actorService.patchActor (id, updateDTO);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get actor by ID", description = "Retrieves an actor by their ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "200", description = "Actor found"),
            @ApiResponse (responseCode = "404", description = "Actor not found")
    })
    public ResponseEntity<ActorResponseDTO> getActorById (
            @Parameter (description = "Actor ID") @PathVariable Integer id) {
        log.info ("REST request to get Actor with ID: {}", id);
        ActorResponseDTO response = actorService.getActorById (id);
        return ResponseEntity.ok (response);
    }

    @GetMapping
    @Operation (summary = "Get all actors", description = "Retrieves all actors with optional pagination")
    @ApiResponse (responseCode = "200", description = "Actors retrieved successfully")
    public ResponseEntity<Page<ActorResponseDTO>> getAllActors (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Actors with pagination");
        Page<ActorResponseDTO> response = actorService.getAllActors (pageable);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/search")
    @Operation (summary = "Search actors by name", description = "Searches actors by first or last name")
    @ApiResponse (responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<ActorResponseDTO>> searchActors (
            @Parameter (description = "Search term") @RequestParam String searchTerm) {
        log.info ("REST request to search Actors with term: {}", searchTerm);
        List<ActorResponseDTO> response = actorService.searchActorsByName (searchTerm);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-first-name")
    @Operation (summary = "Get actors by first name", description = "Retrieves actors matching first name")
    @ApiResponse (responseCode = "200", description = "Actors retrieved successfully")
    public ResponseEntity<List<ActorResponseDTO>> getActorsByFirstName (
            @Parameter (description = "First name") @RequestParam String firstName) {
        log.info ("REST request to get Actors by first name: {}", firstName);
        List<ActorResponseDTO> response = actorService.getActorsByFirstName (firstName);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/by-last-name")
    @Operation (summary = "Get actors by last name", description = "Retrieves actors matching last name")
    @ApiResponse (responseCode = "200", description = "Actors retrieved successfully")
    public ResponseEntity<List<ActorResponseDTO>> getActorsByLastName (
            @Parameter (description = "Last name") @RequestParam String lastName) {
        log.info ("REST request to get Actors by last name: {}", lastName);
        List<ActorResponseDTO> response = actorService.getActorsByLastName (lastName);
        return ResponseEntity.ok (response);
    }

    @GetMapping ("/sorted")
    @Operation (summary = "Get actors sorted by last name", description = "Retrieves all actors sorted by last name")
    @ApiResponse (responseCode = "200", description = "Actors retrieved successfully")
    public ResponseEntity<List<ActorResponseDTO>> getAllActorsSorted () {
        log.info ("REST request to get all Actors sorted by last name");
        List<ActorResponseDTO> response = actorService.getAllActorsSortedByLastName ();
        return ResponseEntity.ok (response);
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete an actor", description = "Deletes an actor by ID")
    @ApiResponses (value = {
            @ApiResponse (responseCode = "204", description = "Actor deleted successfully"),
            @ApiResponse (responseCode = "404", description = "Actor not found")
    })
    public ResponseEntity<Void> deleteActor (
            @Parameter (description = "Actor ID") @PathVariable Integer id) {
        log.info ("REST request to delete Actor with ID: {}", id);
        actorService.deleteActor (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total actors", description = "Returns the total count of actors")
    @ApiResponse (responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countActors () {
        log.info ("REST request to count all Actors");
        long count = actorService.countActors ();
        return ResponseEntity.ok (count);
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if actor exists", description = "Checks if an actor exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Actor ID") @PathVariable Integer id) {
        log.info ("REST request to check if Actor exists with ID: {}", id);
        boolean exists = actorService.existsById (id);
        return ResponseEntity.ok (exists);
    }
}

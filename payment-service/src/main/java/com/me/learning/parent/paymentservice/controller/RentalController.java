package com.me.learning.parent.paymentservice.controller;

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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.parent.paymentservice.dto.RentalRequest;
import com.me.learning.parent.paymentservice.dto.RentalResponse;
import com.me.learning.parent.paymentservice.dto.RentalUpdateRequest;
import com.me.learning.parent.paymentservice.service.RentalService;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : REST Controller for Rental operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/rentals")
@RequiredArgsConstructor
@Tag (name = "Rental", description = "Rental management APIs")
public class RentalController {

    private final RentalService service;

    @PostMapping
    @Operation (summary = "Create a new rental")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "Rental created",
                    content = @Content (schema = @Schema (implementation = RentalResponse.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Rental already exists")
    })
    public ResponseEntity<RentalResponse> createRental (
            @Valid @RequestBody final RentalRequest request) {
        log.info ("REST request to create Rental");
        return ResponseEntity.status (HttpStatus.CREATED).body (service.createRental (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a rental by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Rental updated"),
            @ApiResponse (responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<RentalResponse> updateRental (
            @Parameter (description = "Rental ID") @PathVariable Integer id,
            @Valid @RequestBody RentalUpdateRequest request) {
        log.info ("REST request to update Rental with ID: {}", id);
        return ResponseEntity.ok (service.updateRental (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a rental by ID")
    @ApiResponse (responseCode = "200", description = "Rental patched")
    public ResponseEntity<RentalResponse> patchRental (
            @Parameter (description = "Rental ID") @PathVariable Integer id,
            @RequestBody RentalUpdateRequest request) {
        log.info ("REST request to patch Rental with ID: {}", id);
        return ResponseEntity.ok (service.patchRental (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a rental by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Rental found"),
            @ApiResponse (responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<RentalResponse> getRentalById (
            @Parameter (description = "Rental ID") @PathVariable Integer id) {
        log.info ("REST request to get Rental with ID: {}", id);
        return ResponseEntity.ok (service.getRentalById (id));
    }

    @GetMapping
    @Operation (summary = "Get all rentals with pagination")
    @ApiResponse (responseCode = "200", description = "Rentals retrieved")
    public ResponseEntity<Page<RentalResponse>> getAllRentals (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Rentals");
        return ResponseEntity.ok (service.getAllRentals (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "Get all rentals as a list")
    @ApiResponse (responseCode = "200", description = "Rentals retrieved")
    public ResponseEntity<List<RentalResponse>> getAllRentalsList () {
        log.info ("REST request to get all Rentals as list");
        return ResponseEntity.ok (service.getAllRentals ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a rental by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "Rental deleted"),
            @ApiResponse (responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<Void> deleteRental (
            @Parameter (description = "Rental ID") @PathVariable Integer id) {
        log.info ("REST request to delete Rental with ID: {}", id);
        service.deleteRental (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total rentals")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> countRentals () {
        return ResponseEntity.ok (service.countRentals ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if rental exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Rental ID") @PathVariable Integer id) {
        return ResponseEntity.ok (service.existsById (id));
    }
}

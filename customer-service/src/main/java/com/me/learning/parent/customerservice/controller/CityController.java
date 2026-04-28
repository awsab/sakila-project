package com.me.learning.parent.customerservice.controller;

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

import com.me.learning.parent.customerservice.dto.CityRequest;
import com.me.learning.parent.customerservice.dto.CityResponse;
import com.me.learning.parent.customerservice.dto.CityUpdateRequest;
import com.me.learning.parent.customerservice.service.CityService;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : REST Controller for City operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/cities")
@RequiredArgsConstructor
@Tag (name = "City", description = "City management APIs")
public class CityController {

    private final CityService cityService;

    @PostMapping
    @Operation (summary = "Create a new city")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "City created",
                    content = @Content (schema = @Schema (implementation = CityResponse.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "City already exists")
    })
    public ResponseEntity<CityResponse> createCity (
            @Valid @RequestBody final CityRequest request) {
        log.info ("REST request to create City: {}", request);
        return ResponseEntity.status (HttpStatus.CREATED).body (cityService.createCity (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a city by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "City updated"),
            @ApiResponse (responseCode = "404", description = "City not found")
    })
    public ResponseEntity<CityResponse> updateCity (
            @Parameter (description = "City ID") @PathVariable Integer id,
            @Valid @RequestBody CityUpdateRequest request) {
        log.info ("REST request to update City with ID: {}", id);
        return ResponseEntity.ok (cityService.updateCity (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a city by ID")
    @ApiResponse (responseCode = "200", description = "City patched")
    public ResponseEntity<CityResponse> patchCity (
            @Parameter (description = "City ID") @PathVariable Integer id,
            @RequestBody CityUpdateRequest request) {
        log.info ("REST request to patch City with ID: {}", id);
        return ResponseEntity.ok (cityService.patchCity (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a city by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "City found"),
            @ApiResponse (responseCode = "404", description = "City not found")
    })
    public ResponseEntity<CityResponse> getCityById (
            @Parameter (description = "City ID") @PathVariable Integer id) {
        log.info ("REST request to get City with ID: {}", id);
        return ResponseEntity.ok (cityService.getCityById (id));
    }

    @GetMapping
    @Operation (summary = "Get all cities with pagination")
    @ApiResponse (responseCode = "200", description = "Cities retrieved")
    public ResponseEntity<Page<CityResponse>> getAllCities (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Cities");
        return ResponseEntity.ok (cityService.getAllCities (pageable));
    }

    @GetMapping ("/by-country")
    @Operation (summary = "Get cities by country ID")
    @ApiResponse (responseCode = "200", description = "Cities retrieved")
    public ResponseEntity<List<CityResponse>> getCitiesByCountry (
            @Parameter (description = "Country ID") @RequestParam Integer countryId) {
        log.info ("REST request to get Cities for country ID: {}", countryId);
        return ResponseEntity.ok (cityService.getCitiesByCountryId (countryId));
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a city by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "City deleted"),
            @ApiResponse (responseCode = "404", description = "City not found")
    })
    public ResponseEntity<Void> deleteCity (
            @Parameter (description = "City ID") @PathVariable Integer id) {
        log.info ("REST request to delete City with ID: {}", id);
        cityService.deleteCity (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total cities")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> countCities () {
        return ResponseEntity.ok (cityService.countCities ());
    }
}


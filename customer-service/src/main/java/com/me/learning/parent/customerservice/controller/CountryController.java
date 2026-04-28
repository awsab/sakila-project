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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.parent.customerservice.dto.CountryRequest;
import com.me.learning.parent.customerservice.dto.CountryResponse;
import com.me.learning.parent.customerservice.dto.CountryUpdateRequest;
import com.me.learning.parent.customerservice.service.CountryService;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : REST Controller for Country operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/countries")
@RequiredArgsConstructor
@Tag (name = "Country", description = "Country management APIs")
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    @Operation (summary = "Create a new country")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "Country created",
                    content = @Content (schema = @Schema (implementation = CountryResponse.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Country already exists")
    })
    public ResponseEntity<CountryResponse> createCountry (
            @Valid @RequestBody final CountryRequest request) {
        log.info ("REST request to create Country: {}", request);
        return ResponseEntity.status (HttpStatus.CREATED).body (countryService.createCountry (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a country by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Country updated"),
            @ApiResponse (responseCode = "404", description = "Country not found"),
            @ApiResponse (responseCode = "409", description = "Duplicate country name")
    })
    public ResponseEntity<CountryResponse> updateCountry (
            @Parameter (description = "Country ID") @PathVariable Integer id,
            @Valid @RequestBody CountryUpdateRequest request) {
        log.info ("REST request to update Country with ID: {}", id);
        return ResponseEntity.ok (countryService.updateCountry (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a country by ID")
    @ApiResponse (responseCode = "200", description = "Country patched")
    public ResponseEntity<CountryResponse> patchCountry (
            @Parameter (description = "Country ID") @PathVariable Integer id,
            @RequestBody CountryUpdateRequest request) {
        log.info ("REST request to patch Country with ID: {}", id);
        return ResponseEntity.ok (countryService.patchCountry (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a country by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Country found"),
            @ApiResponse (responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<CountryResponse> getCountryById (
            @Parameter (description = "Country ID") @PathVariable Integer id) {
        log.info ("REST request to get Country with ID: {}", id);
        return ResponseEntity.ok (countryService.getCountryById (id));
    }

    @GetMapping
    @Operation (summary = "Get all countries with pagination")
    @ApiResponse (responseCode = "200", description = "Countries retrieved")
    public ResponseEntity<Page<CountryResponse>> getAllCountries (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Countries");
        return ResponseEntity.ok (countryService.getAllCountries (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "Get all countries as a list")
    @ApiResponse (responseCode = "200", description = "Countries retrieved")
    public ResponseEntity<List<CountryResponse>> getAllCountriesList () {
        log.info ("REST request to get all Countries as list");
        return ResponseEntity.ok (countryService.getAllCountries ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a country by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "Country deleted"),
            @ApiResponse (responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<Void> deleteCountry (
            @Parameter (description = "Country ID") @PathVariable Integer id) {
        log.info ("REST request to delete Country with ID: {}", id);
        countryService.deleteCountry (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total countries")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> countCountries () {
        return ResponseEntity.ok (countryService.countCountries ());
    }
}


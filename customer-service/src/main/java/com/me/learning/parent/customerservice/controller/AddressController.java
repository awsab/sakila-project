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

import com.me.learning.parent.customerservice.dto.AddressRequest;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.AddressUpdateRequest;
import com.me.learning.parent.customerservice.service.AddressService;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : REST Controller for Address operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/addresses")
@RequiredArgsConstructor
@Tag (name = "Address", description = "Address management APIs")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation (summary = "Create a new address")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "Address created",
                    content = @Content (schema = @Schema (implementation = AddressResponse.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "404", description = "City not found")
    })
    public ResponseEntity<AddressResponse> createAddress (
            @Valid @RequestBody final AddressRequest request) {
        log.info ("REST request to create Address");
        return ResponseEntity.status (HttpStatus.CREATED).body (addressService.createAddress (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update an address by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Address updated"),
            @ApiResponse (responseCode = "404", description = "Address or City not found")
    })
    public ResponseEntity<AddressResponse> updateAddress (
            @Parameter (description = "Address ID") @PathVariable Integer id,
            @Valid @RequestBody AddressUpdateRequest request) {
        log.info ("REST request to update Address with ID: {}", id);
        return ResponseEntity.ok (addressService.updateAddress (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update an address by ID")
    @ApiResponse (responseCode = "200", description = "Address patched")
    public ResponseEntity<AddressResponse> patchAddress (
            @Parameter (description = "Address ID") @PathVariable Integer id,
            @RequestBody AddressUpdateRequest request) {
        log.info ("REST request to patch Address with ID: {}", id);
        return ResponseEntity.ok (addressService.patchAddress (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get an address by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Address found"),
            @ApiResponse (responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<AddressResponse> getAddressById (
            @Parameter (description = "Address ID") @PathVariable Integer id) {
        log.info ("REST request to get Address with ID: {}", id);
        return ResponseEntity.ok (addressService.getAddressById (id));
    }

    @GetMapping
    @Operation (summary = "Get all addresses with pagination")
    @ApiResponse (responseCode = "200", description = "Addresses retrieved")
    public ResponseEntity<Page<AddressResponse>> getAllAddresses (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Addresses");
        return ResponseEntity.ok (addressService.getAllAddresses (pageable));
    }

    @GetMapping ("/by-city")
    @Operation (summary = "Get addresses by city ID")
    @ApiResponse (responseCode = "200", description = "Addresses retrieved")
    public ResponseEntity<List<AddressResponse>> getAddressesByCity (
            @Parameter (description = "City ID") @RequestParam Integer cityId) {
        log.info ("REST request to get Addresses for city ID: {}", cityId);
        return ResponseEntity.ok (addressService.getAddressesByCityId (cityId));
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete an address by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "Address deleted"),
            @ApiResponse (responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<Void> deleteAddress (
            @Parameter (description = "Address ID") @PathVariable Integer id) {
        log.info ("REST request to delete Address with ID: {}", id);
        addressService.deleteAddress (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total addresses")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> countAddresses () {
        return ResponseEntity.ok (addressService.countAddresses ());
    }
}


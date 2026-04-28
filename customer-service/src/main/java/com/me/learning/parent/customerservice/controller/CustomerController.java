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

import com.me.learning.parent.customerservice.dto.CustomerDetailResponse;
import com.me.learning.parent.customerservice.dto.CustomerRequest;
import com.me.learning.parent.customerservice.dto.CustomerResponse;
import com.me.learning.parent.customerservice.dto.CustomerUpdateRequest;
import com.me.learning.parent.customerservice.service.CustomerService;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : REST Controller for Customer operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/customers")
@RequiredArgsConstructor
@Tag (name = "Customer", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation (summary = "Create a new customer")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "Customer created",
                    content = @Content (schema = @Schema (implementation = CustomerResponse.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<CustomerResponse> createCustomer (
            @Valid @RequestBody final CustomerRequest request) {
        log.info ("REST request to create Customer: {} {}", request.firstName (), request.lastName ());
        return ResponseEntity.status (HttpStatus.CREATED).body (customerService.createCustomer (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a customer by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Customer updated"),
            @ApiResponse (responseCode = "404", description = "Customer not found"),
            @ApiResponse (responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<CustomerResponse> updateCustomer (
            @Parameter (description = "Customer ID") @PathVariable Integer id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        log.info ("REST request to update Customer with ID: {}", id);
        return ResponseEntity.ok (customerService.updateCustomer (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a customer by ID")
    @ApiResponse (responseCode = "200", description = "Customer patched")
    public ResponseEntity<CustomerResponse> patchCustomer (
            @Parameter (description = "Customer ID") @PathVariable Integer id,
            @RequestBody CustomerUpdateRequest request) {
        log.info ("REST request to patch Customer with ID: {}", id);
        return ResponseEntity.ok (customerService.patchCustomer (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a customer by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Customer found",
                    content = @Content (schema = @Schema (implementation = CustomerDetailResponse.class))),
            @ApiResponse (responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDetailResponse> getCustomerById (
            @Parameter (description = "Customer ID") @PathVariable Integer id) {
        log.info ("REST request to get Customer with ID: {}", id);
        return ResponseEntity.ok (customerService.getCustomerById (id));
    }

    @GetMapping
    @Operation (summary = "Get all customers with pagination")
    @ApiResponse (responseCode = "200", description = "Customers retrieved")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Customers");
        return ResponseEntity.ok (customerService.getAllCustomers (pageable));
    }

    @GetMapping ("/active")
    @Operation (summary = "Get all active customers")
    @ApiResponse (responseCode = "200", description = "Active customers retrieved")
    public ResponseEntity<List<CustomerResponse>> getActiveCustomers () {
        log.info ("REST request to get active Customers");
        return ResponseEntity.ok (customerService.getActiveCustomers ());
    }

    @GetMapping ("/by-store")
    @Operation (summary = "Get customers by store ID")
    @ApiResponse (responseCode = "200", description = "Customers retrieved")
    public ResponseEntity<List<CustomerResponse>> getCustomersByStore (
            @Parameter (description = "Store ID") @RequestParam Short storeId) {
        log.info ("REST request to get Customers for store ID: {}", storeId);
        return ResponseEntity.ok (customerService.getCustomersByStoreId (storeId));
    }

    @GetMapping ("/search")
    @Operation (summary = "Search customers by last name")
    @ApiResponse (responseCode = "200", description = "Search results retrieved")
    public ResponseEntity<List<CustomerResponse>> searchByLastName (
            @Parameter (description = "Last name search term") @RequestParam String lastName) {
        log.info ("REST request to search Customers by last name: {}", lastName);
        return ResponseEntity.ok (customerService.searchByLastName (lastName));
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a customer by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "Customer deleted"),
            @ApiResponse (responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer (
            @Parameter (description = "Customer ID") @PathVariable Integer id) {
        log.info ("REST request to delete Customer with ID: {}", id);
        customerService.deleteCustomer (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total customers")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> countCustomers () {
        return ResponseEntity.ok (customerService.countCustomers ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if customer exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Customer ID") @PathVariable Integer id) {
        return ResponseEntity.ok (customerService.existsById (id));
    }
}


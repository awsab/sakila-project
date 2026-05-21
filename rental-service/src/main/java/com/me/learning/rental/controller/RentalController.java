package com.me.learning.rental.controller;

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

import com.me.learning.rental.dto.RentalCheckoutRequestDto;
import com.me.learning.rental.dto.RentalCheckoutResponseDto;
import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.dto.RentalResponseDto;
import com.me.learning.rental.service.RentalService;

/**
 * REST controller for {@link com.me.learning.rental.entity.Rental} management.
 *
 * <p>Base path: {@code /api/v1/rentals}
 *
 * <p>All endpoints delegate to {@link RentalService} and follow the standard
 * response conventions:
 * <ul>
 *   <li>{@code POST}   → 201 Created with the created resource in the body</li>
 *   <li>{@code PUT}    → 200 OK with the fully updated resource</li>
 *   <li>{@code PATCH}  → 200 OK with the partially updated resource</li>
 *   <li>{@code GET}    → 200 OK with requested resource(s)</li>
 *   <li>{@code DELETE} → 204 No Content</li>
 * </ul>
 *
 * <p>Error responses are handled globally by
 * {@code com.me.learning.framework.web.errors.GlobalExceptionHandler}.
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/rentals")
@RequiredArgsConstructor
@Tag (name = "Rental", description = "Rental management APIs")
public class RentalController {

    private final RentalService rentalService;

    // ── Business action: checkout (rent + collect payment) ───────────────────

    @PostMapping ("/checkout")
    @Operation (
            summary = "Rent a CD and collect payment in one transaction",
            description = "Implements the Sakila checkout flow: creates the rental row first, then "
                    + "creates a linked payment row using the generated rental ID. The `payment.rental` "
                    + "field, if supplied, is ignored during checkout because the rental does not exist yet."
    )
    @ApiResponse (
            responseCode = "201", description = "Checkout completed successfully",
            content = @Content (schema = @Schema (implementation = RentalCheckoutResponseDto.class)))
    @ApiResponse (
            responseCode = "400", description = "Validation error or payment/rental mismatch")
    public ResponseEntity<RentalCheckoutResponseDto> checkout (
            @Valid @RequestBody RentalCheckoutRequestDto dto) {
        log.info ("REST POST /api/v1/rentals/checkout — customerId {}, inventoryId {}",
                dto.rental ().customerId (), dto.rental ().inventoryId ());
        return ResponseEntity.status (HttpStatus.CREATED).body (rentalService.checkout (dto));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation (
            summary = "Create a new rental",
            description = "Creates a rental record. The `id` field in the request body is ignored; "
                    + "the ID is always assigned by the database."
    )
    @ApiResponse (
            responseCode = "201", description = "Rental created successfully",
            content = @Content (schema = @Schema (implementation = RentalResponseDto.class)))
    @ApiResponse (
            responseCode = "400", description = "Validation error — check field constraints")
    public ResponseEntity<RentalResponseDto> create (
            @Valid @RequestBody RentalRequestDto dto) {
        log.info ("REST POST /api/v1/rentals — creating rental for customerId: {}", dto.customerId ());
        return ResponseEntity.status (HttpStatus.CREATED).body (rentalService.create (dto));
    }

    // ── Full update ───────────────────────────────────────────────────────────

    @PutMapping ("/{id}")
    @Operation (
            summary = "Fully update a rental",
            description = "Replaces ALL editable fields of the rental identified by `id`. "
                    + "Any field not sent in the request body will be set to its supplied value "
                    + "(null-safe update is not applied — use PATCH for partial updates)."
    )
    @ApiResponse (
            responseCode = "200", description = "Rental updated successfully",
            content = @Content (schema = @Schema (implementation = RentalResponseDto.class)))
    @ApiResponse (
            responseCode = "404", description = "Rental not found")
    public ResponseEntity<RentalResponseDto> update (
            @Parameter (description = "Rental primary key") @PathVariable Integer id,
            @Valid @RequestBody RentalRequestDto dto) {
        log.info ("REST PUT /api/v1/rentals/{} — full update", id);
        return ResponseEntity.ok (rentalService.update (id, dto));
    }

    // ── Partial update ────────────────────────────────────────────────────────

    @PatchMapping ("/{id}")
    @Operation (
            summary = "Partially update a rental",
            description = "Updates only the non-null fields supplied in the request body. "
                    + "Fields omitted or explicitly set to `null` are left unchanged."
    )
    @ApiResponse (
            responseCode = "200", description = "Rental patched successfully",
            content = @Content (schema = @Schema (implementation = RentalResponseDto.class)))
    @ApiResponse (
            responseCode = "404", description = "Rental not found")
    public ResponseEntity<RentalResponseDto> partialUpdate (
            @Parameter (description = "Rental primary key") @PathVariable Integer id,
            @RequestBody RentalRequestDto dto) {
        log.info ("REST PATCH /api/v1/rentals/{} — partial update", id);
        return ResponseEntity.ok (rentalService.partialUpdate (id, dto));
    }

    // ── Read by ID ────────────────────────────────────────────────────────────

    @GetMapping ("/{id}")
    @Operation (
            summary = "Get a rental by ID",
            description = "Returns the rental record identified by the given primary key."
    )
    @ApiResponse (
            responseCode = "200", description = "Rental found",
            content = @Content (schema = @Schema (implementation = RentalResponseDto.class)))
    @ApiResponse (
            responseCode = "404", description = "Rental not found")
    public ResponseEntity<RentalResponseDto> findById (
            @Parameter (description = "Rental primary key") @PathVariable Integer id) {
        log.info ("REST GET /api/v1/rentals/{}", id);
        return ResponseEntity.ok (rentalService.findById (id));
    }

    // ── List / Paginated ──────────────────────────────────────────────────────

    @GetMapping
    @Operation (
            summary = "List all rentals (paginated)",
            description = "Returns a page of rentals. Default page size is 20. "
                    + "Supports Spring Data pagination parameters: `page`, `size`, `sort`."
    )
    @ApiResponse (
            responseCode = "200", description = "Rentals retrieved")
    public ResponseEntity<Page<RentalResponseDto>> findAll (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST GET /api/v1/rentals — page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return ResponseEntity.ok (rentalService.findAll (pageable));
    }

    @GetMapping ("/all")
    @Operation (
            summary = "List all rentals (unpaged)",
            description = "Returns the complete list of rentals without pagination. "
                    + "Use with caution on large data sets; prefer the paginated endpoint."
    )
    @ApiResponse (
            responseCode = "200", description = "All rentals retrieved")
    public ResponseEntity<List<RentalResponseDto>> getAllRentals () {
        log.info ("REST GET /api/v1/rentals/all");
        return ResponseEntity.ok (rentalService.findAll ());
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a rental by ID")
    @ApiResponse (
            responseCode = "204", description = "Rental deleted successfully")
    @ApiResponse (
            responseCode = "404", description = "Rental not found")
    public ResponseEntity<Void> delete (
            @Parameter (description = "Rental primary key") @PathVariable Integer id) {
        log.info ("REST DELETE /api/v1/rentals/{}", id);
        rentalService.delete (id);
        return ResponseEntity.noContent ().build ();
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    @GetMapping ("/count")
    @Operation (summary = "Count total number of rentals")
    @ApiResponse (
            responseCode = "200", description = "Count returned")
    public ResponseEntity<Long> count () {
        return ResponseEntity.ok (rentalService.count ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check whether a rental exists by ID")
    @ApiResponse (
            responseCode = "200", description = "Existence flag returned")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Rental primary key") @PathVariable Integer id) {
        return ResponseEntity.ok (rentalService.existsById (id));
    }
}


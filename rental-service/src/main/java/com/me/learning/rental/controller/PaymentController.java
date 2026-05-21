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

import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.PaymentResponseDto;
import com.me.learning.rental.service.PaymentService;

/**
 * REST controller for {@link com.me.learning.rental.entity.Payment} management.
 *
 * <p>Base path: {@code /api/v1/payments}
 *
 * <p>All endpoints delegate to {@link PaymentService} and follow the standard
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
@RequestMapping ("/api/v1/payments")
@RequiredArgsConstructor
@Tag (name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation (
            summary = "Create a new payment",
            description = "Creates a payment record. The `id` field in the request body is ignored; "
                    + "the ID is always assigned by the database. If `rental.id` is supplied, it must "
                    + "reference an existing rental."
    )
    @ApiResponse (
            responseCode = "201", description = "Payment created successfully",
            content = @Content (schema = @Schema (implementation = PaymentResponseDto.class)))
    @ApiResponse (
            responseCode = "400", description = "Validation error — check field constraints")
    @ApiResponse (
            responseCode = "404", description = "Referenced rental does not exist")
    public ResponseEntity<PaymentResponseDto> create (
            @Valid @RequestBody PaymentRequestDto dto) {
        log.info ("REST POST /api/v1/payments — creating payment for customerId: {}", dto.customerId ());
        return ResponseEntity.status (HttpStatus.CREATED).body (paymentService.create (dto));
    }

    // ── Full update ───────────────────────────────────────────────────────────

    @PutMapping ("/{id}")
    @Operation (
            summary = "Fully update a payment",
            description = "Replaces ALL editable fields of the payment identified by `id`. "
                    + "Any field not sent in the request body will be set to its supplied value "
                    + "(null-safe update is not applied — use PATCH for partial updates)."
    )
    @ApiResponse (
            responseCode = "200", description = "Payment updated successfully",
            content = @Content (schema = @Schema (implementation = PaymentResponseDto.class)))
    @ApiResponse (
            responseCode = "404", description = "Payment or referenced rental not found")
    public ResponseEntity<PaymentResponseDto> update (
            @Parameter (description = "Payment primary key") @PathVariable Integer id,
            @Valid @RequestBody PaymentRequestDto dto) {
        log.info ("REST PUT /api/v1/payments/{} — full update", id);
        return ResponseEntity.ok (paymentService.update (id, dto));
    }

    // ── Partial update ────────────────────────────────────────────────────────

    @PatchMapping ("/{id}")
    @Operation (
            summary = "Partially update a payment",
            description = "Updates only the non-null fields supplied in the request body. "
                    + "Fields omitted or explicitly set to `null` are left unchanged."
    )
    @ApiResponse (
            responseCode = "200", description = "Payment patched successfully",
            content = @Content (schema = @Schema (implementation = PaymentResponseDto.class)))
    @ApiResponse (
            responseCode = "404", description = "Payment or referenced rental not found")
    public ResponseEntity<PaymentResponseDto> partialUpdate (
            @Parameter (description = "Payment primary key") @PathVariable Integer id,
            @RequestBody PaymentRequestDto dto) {
        log.info ("REST PATCH /api/v1/payments/{} — partial update", id);
        return ResponseEntity.ok (paymentService.partialUpdate (id, dto));
    }

    // ── Read by ID ────────────────────────────────────────────────────────────

    @GetMapping ("/{id}")
    @Operation (
            summary = "Get a payment by ID",
            description = "Returns the payment record along with its linked rental ID, if present."
    )
    @ApiResponse (
            responseCode = "200", description = "Payment found",
            content = @Content (schema = @Schema (implementation = PaymentResponseDto.class)))
    @ApiResponse (
            responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentResponseDto> findById (
            @Parameter (description = "Payment primary key") @PathVariable Integer id) {
        log.info ("REST GET /api/v1/payments/{}", id);
        return ResponseEntity.ok (paymentService.findById (id));
    }

    // ── List / Paginated ──────────────────────────────────────────────────────

    @GetMapping
    @Operation (
            summary = "List all payments (paginated)",
            description = "Returns a page of payments. Default page size is 20. "
                    + "Supports Spring Data pagination parameters: `page`, `size`, `sort`."
    )
    @ApiResponse (
            responseCode = "200", description = "Payments retrieved")
    public ResponseEntity<Page<PaymentResponseDto>> findAll (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST GET /api/v1/payments — page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return ResponseEntity.ok (paymentService.findAll (pageable));
    }

    @GetMapping ("/all")
    @Operation (
            summary = "List all payments (unpaged)",
            description = "Returns the complete list of payments without pagination. "
                    + "Use with caution on large data sets; prefer the paginated endpoint."
    )
    @ApiResponse (
            responseCode = "200", description = "All payments retrieved")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments () {
        log.info ("REST GET /api/v1/payments/all");
        return ResponseEntity.ok (paymentService.findAll ());
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a payment by ID")
    @ApiResponse (
            responseCode = "204", description = "Payment deleted successfully")
    @ApiResponse (
            responseCode = "404", description = "Payment not found")
    public ResponseEntity<Void> delete (
            @Parameter (description = "Payment primary key") @PathVariable Integer id) {
        log.info ("REST DELETE /api/v1/payments/{}", id);
        paymentService.delete (id);
        return ResponseEntity.noContent ().build ();
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    @GetMapping ("/count")
    @Operation (summary = "Count total number of payments")
    @ApiResponse (
            responseCode = "200", description = "Count returned")
    public ResponseEntity<Long> count () {
        return ResponseEntity.ok (paymentService.count ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check whether a payment exists by ID")
    @ApiResponse (
            responseCode = "200", description = "Existence flag returned")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Payment primary key") @PathVariable Integer id) {
        return ResponseEntity.ok (paymentService.existsById (id));
    }
}


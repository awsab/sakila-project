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

import com.me.learning.parent.paymentservice.dto.PaymentRequest;
import com.me.learning.parent.paymentservice.dto.PaymentResponse;
import com.me.learning.parent.paymentservice.dto.PaymentUpdateRequest;
import com.me.learning.parent.paymentservice.service.PaymentService;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : REST Controller for Payment operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/payments")
@RequiredArgsConstructor
@Tag (name = "Payment", description = "Payment management APIs")
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    @Operation (summary = "Create a new payment")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "Payment created",
                    content = @Content (schema = @Schema (implementation = PaymentResponse.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "Payment already exists")
    })
    public ResponseEntity<PaymentResponse> createPayment (
            @Valid @RequestBody final PaymentRequest request) {
        log.info ("REST request to create Payment");
        return ResponseEntity.status (HttpStatus.CREATED).body (service.createPayment (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a payment by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Payment updated"),
            @ApiResponse (responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> updatePayment (
            @Parameter (description = "Payment ID") @PathVariable Integer id,
            @Valid @RequestBody PaymentUpdateRequest request) {
        log.info ("REST request to update Payment with ID: {}", id);
        return ResponseEntity.ok (service.updatePayment (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a payment by ID")
    @ApiResponse (responseCode = "200", description = "Payment patched")
    public ResponseEntity<PaymentResponse> patchPayment (
            @Parameter (description = "Payment ID") @PathVariable Integer id,
            @RequestBody PaymentUpdateRequest request) {
        log.info ("REST request to patch Payment with ID: {}", id);
        return ResponseEntity.ok (service.patchPayment (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a payment by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "Payment found"),
            @ApiResponse (responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> getPaymentById (
            @Parameter (description = "Payment ID") @PathVariable Integer id) {
        log.info ("REST request to get Payment with ID: {}", id);
        return ResponseEntity.ok (service.getPaymentById (id));
    }

    @GetMapping
    @Operation (summary = "Get all payments with pagination")
    @ApiResponse (responseCode = "200", description = "Payments retrieved")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all Payments");
        return ResponseEntity.ok (service.getAllPayments (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "Get all payments as a list")
    @ApiResponse (responseCode = "200", description = "Payments retrieved")
    public ResponseEntity<List<PaymentResponse>> getAllPaymentsList () {
        log.info ("REST request to get all Payments as list");
        return ResponseEntity.ok (service.getAllPayments ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a payment by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "Payment deleted"),
            @ApiResponse (responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<Void> deletePayment (
            @Parameter (description = "Payment ID") @PathVariable Integer id) {
        log.info ("REST request to delete Payment with ID: {}", id);
        service.deletePayment (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total payments")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> countPayments () {
        return ResponseEntity.ok (service.countPayments ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if payment exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "Payment ID") @PathVariable Integer id) {
        return ResponseEntity.ok (service.existsById (id));
    }
}

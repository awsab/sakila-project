package com.me.learning.rental.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Composite response DTO for the Sakila checkout use case.
 *
 * <p>Returns both the created rental and the payment collected for that rental
 * in a single payload.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record RentalCheckoutResponseDto(
        RentalResponseDto rental,
        PaymentResponseDto payment
) implements Serializable {
}


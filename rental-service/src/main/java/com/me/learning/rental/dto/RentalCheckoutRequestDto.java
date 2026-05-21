package com.me.learning.rental.dto;

import java.io.Serializable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Composite write DTO for the Sakila checkout use case.
 *
 * <p>Represents the act of renting a CD and collecting its payment in the same
 * business transaction: first a {@code rental} row is created, then a linked
 * {@code payment} row is inserted with the generated {@code rental_id}.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record RentalCheckoutRequestDto(
        @Valid @NotNull RentalRequestDto rental,
        @Valid @NotNull PaymentRequestDto payment
) implements Serializable {
}


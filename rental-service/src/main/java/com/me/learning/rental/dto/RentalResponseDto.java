package com.me.learning.rental.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Read-model DTO for {@link com.me.learning.rental.entity.Rental}.
 *
 * <p>Keeps the payload flat: all Rental scalar fields are included directly.
 * Cross-service references ({@code inventoryId}, {@code customerId}, {@code staffId})
 * are already plain IDs in the entity — no further flattening is required.
 *
 * <p>This is the canonical response shape returned by every
 * {@code GET}, {@code POST}, {@code PUT}, and {@code PATCH} endpoint.
 * {@link RentalRequestDto} continues to be used solely as the inbound
 * write payload.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record RentalResponseDto(
        Integer id,
        Instant rentalDate,
        Integer inventoryId,
        Integer customerId,
        Instant returnDate,
        Short staffId,
        Instant lastUpdate
) implements Serializable {
}


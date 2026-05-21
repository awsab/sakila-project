package com.me.learning.rental.dto;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for {@link com.me.learning.rental.entity.Rental}
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record RentalRequestDto(Integer id,
                                @NotNull Instant rentalDate,
                                @NotNull Integer inventoryId,
                                @NotNull Integer customerId,
                                Instant returnDate,
                                @NotNull Short staffId,
                                @NotNull Instant lastUpdate)
        implements Serializable {
}


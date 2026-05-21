package com.me.learning.rental.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for {@link com.me.learning.rental.entity.Payment}
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record PaymentRequestDto(Integer id,
                                 @NotNull Integer customerId,
                                 @NotNull Short staffId,
                                 RentalRequestDto rental,
                                 @NotNull @Digits (integer = 3, fraction = 2) BigDecimal amount,
                                 @NotNull Instant paymentDate,
                                 @NotNull Instant lastUpdate)
        implements Serializable {
}


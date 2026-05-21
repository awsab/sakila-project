package com.me.learning.rental.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Read-model DTO for {@link com.me.learning.rental.entity.Payment}.
 *
 * <p>Keeps the payload flat: all Payment scalar fields are included, and the
 * related rental is represented only by its primary-key ID rather than as a
 * nested object.
 *
 * <p>This is the canonical response shape returned by every
 * {@code GET}, {@code POST}, {@code PUT}, and {@code PATCH} endpoint.
 * {@link PaymentRequestDto} continues to be used solely as the inbound
 * write payload.
 *
 * <ul>
 *   <li>{@code rentalId} → {@code Payment.rental.id}</li>
 * </ul>
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record PaymentResponseDto(
        Integer id,
        Integer customerId,
        Short staffId,
        Integer rentalId,
        BigDecimal amount,
        Instant paymentDate,
        Instant lastUpdate
) implements Serializable {
}


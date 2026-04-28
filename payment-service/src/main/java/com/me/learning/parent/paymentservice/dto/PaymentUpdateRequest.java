package com.me.learning.parent.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Update request DTO for Payment entity
 * Since    : Version 1.0
 */
public record PaymentUpdateRequest(

        @NotBlank(message = "Payment name must not be blank")
        @Size(max = 50, message = "Payment name must not exceed 50 characters")
        String name

        // TODO: add updatable FK fields

) {
}

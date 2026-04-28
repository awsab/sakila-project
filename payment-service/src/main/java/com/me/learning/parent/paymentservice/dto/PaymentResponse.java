package com.me.learning.parent.paymentservice.dto;

import java.time.Instant;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Response DTO for Payment entity
 * Since    : Version 1.0
 */
public record PaymentResponse(

        Integer id,
        String name,
        Instant lastUpdate

        // TODO: add FK response fields (e.g. Integer parentId, String parentName)

) {
}

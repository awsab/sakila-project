package com.me.learning.parent.customerservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDetailResponse(

        Integer id,
        Short staffId,
        BigDecimal amount,
        Instant paymentDate,
        Instant lastUpdate

) {
}


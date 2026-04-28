package com.me.learning.parent.customerservice.dto;

import java.time.Instant;

public record CustomerResponse(

        Integer id,
        Short storeId,
        String firstName,
        String lastName,
        String email,
        Integer addressId,
        Boolean active,
        Instant createDate,
        Instant lastUpdate

) {
}


package com.me.learning.parent.customerservice.dto;

import java.time.Instant;

public record StaffResponse(

        Short id,
        String firstName,
        String lastName,
        Integer addressId,
        String email,
        Short storeId,
        Boolean active,
        String username,
        Instant lastUpdate

) {
}


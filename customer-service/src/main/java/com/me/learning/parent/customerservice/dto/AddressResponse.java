package com.me.learning.parent.customerservice.dto;

import java.time.Instant;

public record AddressResponse(

        Integer id,
        String address,
        String address2,
        String district,
        Integer cityId,
        String cityName,
        String postalCode,
        String phone,
        Instant lastUpdate

) {
}


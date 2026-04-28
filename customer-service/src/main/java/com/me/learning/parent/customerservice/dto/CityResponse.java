package com.me.learning.parent.customerservice.dto;

import java.time.Instant;

public record CityResponse(

        Integer id,
        String city,
        Integer countryId,
        String countryName,
        Instant lastUpdate

) {
}


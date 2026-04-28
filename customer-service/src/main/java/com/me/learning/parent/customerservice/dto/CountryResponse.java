package com.me.learning.parent.customerservice.dto;

import java.time.Instant;

public record CountryResponse(

        Integer id,
        String country,
        Instant lastUpdate

) {
}


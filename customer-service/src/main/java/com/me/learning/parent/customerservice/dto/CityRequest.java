package com.me.learning.parent.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CityRequest(

        @NotBlank(message = "City name must not be blank")
        @Size(max = 50, message = "City name must not exceed 50 characters")
        String city,

        @NotNull(message = "Country ID must not be null")
        @Positive(message = "Country ID must be a positive number")
        Integer countryId

) {
}


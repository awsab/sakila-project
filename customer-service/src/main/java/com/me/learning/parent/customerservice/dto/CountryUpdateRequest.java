package com.me.learning.parent.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryUpdateRequest(

        @NotBlank(message = "Country name must not be blank")
        @Size(max = 50, message = "Country name must not exceed 50 characters")
        String country

) {
}


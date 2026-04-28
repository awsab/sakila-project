package com.me.learning.parent.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AddressRequest(

        @NotBlank(message = "Address must not be blank")
        @Size(max = 50, message = "Address must not exceed 50 characters")
        String address,

        @Size(max = 50, message = "Address line 2 must not exceed 50 characters")
        String address2,

        @NotBlank(message = "District must not be blank")
        @Size(max = 20, message = "District must not exceed 20 characters")
        String district,

        @NotNull(message = "City ID must not be null")
        @Positive(message = "City ID must be a positive number")
        Integer cityId,

        @Size(max = 10, message = "Postal code must not exceed 10 characters")
        @Pattern(regexp = "^[A-Za-z0-9\\- ]*$", message = "Postal code contains invalid characters")
        String postalCode,

        @NotBlank(message = "Phone must not be blank")
        @Size(max = 20, message = "Phone must not exceed 20 characters")
        @Pattern(regexp = "^[+\\d\\s\\-()]*$", message = "Phone number contains invalid characters")
        String phone

) {
}


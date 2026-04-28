package com.me.learning.parent.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StaffRequest(

        @NotBlank(message = "First name must not be blank")
        @Size(max = 45, message = "First name must not exceed 45 characters")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(max = 45, message = "Last name must not exceed 45 characters")
        String lastName,

        @NotNull(message = "Address ID must not be null")
        @Positive(message = "Address ID must be a positive number")
        Integer addressId,

        @Email(message = "Email must be a valid email address")
        @Size(max = 50, message = "Email must not exceed 50 characters")
        String email,

        @NotNull(message = "Store ID must not be null")
        @Positive(message = "Store ID must be a positive number")
        Short storeId,

        @NotNull(message = "Active flag must not be null")
        Boolean active,

        @NotBlank(message = "Username must not be blank")
        @Size(max = 16, message = "Username must not exceed 16 characters")
        String username,

        @Size(max = 40, message = "Password must not exceed 40 characters")
        String password

) {
}


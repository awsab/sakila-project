package com.me.learning.parent.inventoryservice.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : DTO for creating new films (no ID needed)
 * Since    : Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank (message = "Title is required")
    @Size (max = 255, message = "Title must not exceed 255 characters")
    @JsonProperty ("title")
    private String title;

    @JsonProperty ("description")
    private String description;

    @JsonProperty ("releaseYear")
    private int releaseYear;

    @JsonProperty ("rentalDuration")
    private Short rentalDuration;

    @NotNull (message = "Rental rate is required")
    @DecimalMin (value = "0.00", message = "Rental rate must be positive")
    @JsonProperty ("rentalRate")
    private BigDecimal rentalRate;

    @JsonProperty ("length")
    private Integer length;

    @NotNull (message = "Replacement cost is required")
    @DecimalMin (value = "0.00", message = "Replacement cost must be positive")
    @JsonProperty ("replacementCost")
    private BigDecimal replacementCost;

    @JsonProperty ("rating")
    private String rating;

    @JsonProperty ("specialFeatures")
    private String specialFeatures;
}

package com.me.learning.parent.inventoryservice.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : DTO for film responses
 * Since    : Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty ("filmId")
    private Integer id;

    @JsonProperty ("title")
    private String title;

    @JsonProperty ("description")
    private String description;

    @JsonProperty ("releaseYear")
    private int releaseYear;

    @JsonProperty ("rentalDuration")
    private Short rentalDuration;

    @JsonProperty ("rentalRate")
    private BigDecimal rentalRate;

    @JsonProperty ("length")
    private Integer length;

    @JsonProperty ("replacementCost")
    private BigDecimal replacementCost;

    @JsonProperty ("rating")
    private String rating;

    @JsonProperty ("specialFeatures")
    private String specialFeatures;

    @JsonProperty ("lastUpdate")
    private Instant lastUpdate;
}

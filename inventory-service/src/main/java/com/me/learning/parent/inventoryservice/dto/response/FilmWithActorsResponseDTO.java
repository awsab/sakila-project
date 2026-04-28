package com.me.learning.parent.inventoryservice.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilmWithActorsResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty ("filmId")
    private Integer id;

    @JsonProperty ("title")
    private String title;

    @JsonProperty ("description")
    private String description;

    @JsonProperty ("releaseYear")
    private LocalDate releaseYear;

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

    @JsonProperty ("actors")
    private Set<ActorResponseDTO> actors;

    @JsonProperty ("categories")
    private Set<CategoryResponseDTO> categories;

    @JsonProperty ("lastUpdate")
    private Instant lastUpdate;
}

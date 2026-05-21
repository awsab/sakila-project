package com.me.learning.catalog.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Read-model DTO for {@link com.me.learning.catalog.entity.Film}.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record FilmResponseDto(
        Integer id,
        String title,
        String description,
        Short releaseYear,
        Short languageId,
        String languageName,
        Short originalLanguageId,
        String originalLanguageName,
        Short rentalDuration,
        BigDecimal rentalRate,
        Short length,
        BigDecimal replacementCost,
        String rating,
        String specialFeatures,
        Instant lastUpdate,
        List<ActorSummaryDto> actors,
        List<CategorySummaryDto> categories
) implements Serializable {
}


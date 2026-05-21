package com.me.learning.catalog.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Write-model DTO for {@link com.me.learning.catalog.entity.Film}.
 *
 * <p>The film is created/updated with scalar fields plus relationship data:</p>
 * <ul>
 *   <li>{@code actorIds} maps to the {@code film_actor} join table.</li>
 *   <li>{@code categoryIds} maps to the {@code film_category} join table.</li>
 *   <li>{@code inlineActors} creates new actors first, then links them.</li>
 *   <li>{@code inlineCategories} creates new categories first, then links them.</li>
 * </ul>
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record FilmRequestDto(
        Integer id,
        @NotNull @Size (max = 255) String title,
        String description,
        Short releaseYear,
        @NotNull Short languageId,
        Short originalLanguageId,
        @NotNull Short rentalDuration,
        @NotNull @Digits (integer = 2, fraction = 2) BigDecimal rentalRate,
        Short length,
        @NotNull @Digits (integer = 3, fraction = 2) BigDecimal replacementCost,
        @Size (max = 5) String rating,
        String specialFeatures,
        Instant lastUpdate,
        List<Integer> actorIds,
        List<Short> categoryIds,
        List<ActorRequestDto> inlineActors,
        List<CategoryRequestDto> inlineCategories
) implements Serializable {
}


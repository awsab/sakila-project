package com.me.learning.catalog.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Read-model DTO for {@link com.me.learning.catalog.entity.Category}.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record CategoryResponseDto(
        Short id,
        String name,
        Instant lastUpdate
) implements Serializable {
}


package com.me.learning.catalog.dto;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Write-model DTO for {@link com.me.learning.catalog.entity.Category}.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record CategoryRequestDto(
        Short id,
        @NotNull @Size (max = 25) String name,
        Instant lastUpdate
) implements Serializable {
}


package com.me.learning.catalog.dto;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Write-model DTO for {@link com.me.learning.catalog.entity.Actor}.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record ActorRequestDto(
        Integer id,
        @NotNull @Size (max = 45) String firstName,
        @NotNull @Size (max = 45) String lastName,
        Instant lastUpdate
) implements Serializable {
}


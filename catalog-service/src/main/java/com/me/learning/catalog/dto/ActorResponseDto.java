package com.me.learning.catalog.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Read-model DTO for {@link com.me.learning.catalog.entity.Actor}.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record ActorResponseDto(
        Integer id,
        String firstName,
        String lastName,
        Instant lastUpdate
) implements Serializable {
}


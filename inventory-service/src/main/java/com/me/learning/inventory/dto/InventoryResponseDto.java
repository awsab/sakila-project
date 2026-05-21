package com.me.learning.inventory.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryResponseDto(
        Integer id,
        Integer filmId,
        Short storeId,
        Instant lastUpdate
) implements Serializable {
}


package com.me.learning.inventory.dto;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryRequestDto(
        Integer id,
        @NotNull Integer filmId,
        @NotNull @Valid StoreRefDto store,
        Instant lastUpdate
) implements Serializable {
}


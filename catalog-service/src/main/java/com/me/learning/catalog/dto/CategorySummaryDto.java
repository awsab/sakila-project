package com.me.learning.catalog.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Lightweight category projection used inside film responses.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record CategorySummaryDto(
        Short id,
        String name
) implements Serializable {
}


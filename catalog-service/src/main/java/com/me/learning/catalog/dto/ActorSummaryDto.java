package com.me.learning.catalog.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Lightweight actor projection used inside film responses.
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public record ActorSummaryDto(
        Integer id,
        String firstName,
        String lastName
) implements Serializable {
}


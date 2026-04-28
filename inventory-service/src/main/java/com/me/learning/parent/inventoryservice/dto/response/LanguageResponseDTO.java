package com.me.learning.parent.inventoryservice.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LanguageResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty ("languageId")
    private Integer id;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("lastUpdate")
    private Instant lastUpdate;
}

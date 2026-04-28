package com.me.learning.parent.inventoryservice.dto.update;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : DTO for updating existing actors (ID required)
 * Since    : Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActorUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull (message = "Actor ID is required for update")
    @JsonProperty ("actorId")
    private Integer id;

    @NotBlank (message = "First name is required")
    @Size (max = 45, message = "First name must not exceed 45 characters")
    @JsonProperty ("firstName")
    private String firstName;

    @NotBlank (message = "Last name is required")
    @Size (max = 45, message = "Last name must not exceed 45 characters")
    @JsonProperty ("lastName")
    private String lastName;

    @JsonProperty ("lastUpdate")
    private LocalDateTime lastUpdate;
}

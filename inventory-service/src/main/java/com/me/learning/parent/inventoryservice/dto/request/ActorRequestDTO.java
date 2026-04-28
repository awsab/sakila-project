package com.me.learning.parent.inventoryservice.dto.request;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
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
 * Usage    : DTO for creating new actors (no ID needed)
 * Since    : Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActorRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank (message = "First name is required")
    @Size (max = 45, message = "First name must not exceed 45 characters")
    @JsonProperty ("firstName")
    private String firstName;

    @NotBlank (message = "Last name is required")
    @Size (max = 45, message = "Last name must not exceed 45 characters")
    @JsonProperty ("lastName")
    private String lastName;
}

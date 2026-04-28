/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.parent.inventoryservice.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActorResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty ("actorId")
    private Integer id;

    @JsonProperty ("firstName")
    private String firstName;

    @JsonProperty ("lastName")
    private String lastName;

    @JsonProperty ("lastUpdate")
    private LocalDateTime lastUpdate;
}

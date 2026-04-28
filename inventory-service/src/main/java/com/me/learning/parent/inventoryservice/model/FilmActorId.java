package com.me.learning.parent.inventoryservice.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class FilmActorId implements Serializable {

    private static final long serialVersionUID = -5570646075852713085L;

    @Column (name = "actor_id", columnDefinition = "smallint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.SMALLINT)
    private Integer actorId;

    @Column (name = "film_id", columnDefinition = "smallint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.SMALLINT)
    private Integer filmId;
}

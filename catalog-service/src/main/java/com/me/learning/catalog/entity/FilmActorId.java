package com.me.learning.catalog.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


/**
 * Composite primary key for the {@link FilmActor} join table.
 * JPA requires embeddable IDs to implement {@link Serializable}.
 */
@Getter
@Setter
@Embeddable
public class FilmActorId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column (name = "film_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer filmId;

    @Column (name = "actor_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer actorId;

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FilmActorId other)) {
            return false;
        }
        return Objects.equals (filmId, other.filmId)
                && Objects.equals (actorId, other.actorId);
    }

    @Override
    public int hashCode () {
        return Objects.hash (filmId, actorId);
    }

}


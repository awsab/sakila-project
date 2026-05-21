package com.me.learning.catalog.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * Join-table entity that links a {@link Film} to an {@link Actor}.
 *
 * <p>The composite primary key {@code (film_id, actor_id)} is modelled with
 * {@link EmbeddedId} and {@link MapsId} so that the FK columns stored in
 * {@link FilmActorId} are kept in sync with the association references.
 *
 * Maps to the {@code film_actor} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "film_actor")
public class FilmActor {

    @EmbeddedId
    private FilmActorId id = new FilmActorId ();

    @NotNull
    @MapsId ("filmId")
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "film_id", nullable = false)
    private Film film;

    @NotNull
    @MapsId ("actorId")
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "actor_id", nullable = false)
    private Actor actor;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

}


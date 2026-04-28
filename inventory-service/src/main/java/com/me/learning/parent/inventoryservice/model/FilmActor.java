package com.me.learning.parent.inventoryservice.model;

import java.io.Serializable;
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

@Getter
@Setter
@Entity
@Table (name = "film_actor")
public class FilmActor implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private FilmActorId id;

    @MapsId ("filmId")
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "film_id", nullable = false)
    private Film film;

    @MapsId ("actorId")
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "actor_id", nullable = false)
    private Actor actor;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;
}

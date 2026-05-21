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
 * Join-table entity that links a {@link Film} to a {@link Category}.
 *
 * <p>The composite primary key {@code (film_id, category_id)} is modelled with
 * {@link EmbeddedId} and {@link MapsId} so that the FK columns stored in
 * {@link FilmCategoryId} are kept in sync with the association references.
 *
 * Maps to the {@code film_category} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "film_category")
public class FilmCategory {

    @EmbeddedId
    private FilmCategoryId id = new FilmCategoryId ();

    @NotNull
    @MapsId ("filmId")
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "film_id", nullable = false)
    private Film film;

    @NotNull
    @MapsId ("categoryId")
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

}


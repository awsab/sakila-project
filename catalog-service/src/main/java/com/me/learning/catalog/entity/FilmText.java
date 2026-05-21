package com.me.learning.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;


/**
 * MySQL-specific full-text search mirror of film title and description.
 *
 * <p>In the original Sakila schema, this table is maintained by database triggers
 * on the {@code film} table. The {@code film_id} is NOT auto-generated here —
 * it must match an existing {@link Film#getId()} value.
 *
 * <p>This entity is intended for full-text search queries only; standard CRUD on
 * film data should go through {@link Film}.
 *
 * Maps to the {@code film_text} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "film_text")
public class FilmText {

    /**
     * Primary key — mirrors the {@code film_id} from the {@link Film} table.
     * Not auto-generated; the value must be set explicitly to match the parent film.
     */
    @Id
    @Column (name = "film_id", columnDefinition = "smallint not null")
    private Integer filmId;

    @Size (max = 255)
    @NotNull
    @Column (name = "title", nullable = false)
    private String title;

    /** Optional full-text searchable description (TEXT column). */
    @Column (name = "description", columnDefinition = "text")
    private String description;

}


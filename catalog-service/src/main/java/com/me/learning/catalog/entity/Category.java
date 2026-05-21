package com.me.learning.catalog.entity;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * Represents a movie genre / category (e.g. Action, Comedy).
 * Maps to the {@code category} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "category")
public class Category {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "category_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @Size (max = 25)
    @NotNull
    @Column (name = "name", nullable = false, length = 25)
    private String name;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    /** All film-category assignments for this category (via the film_category join table). */
    @OneToMany (mappedBy = "category")
    private Set<FilmCategory> filmCategories = new LinkedHashSet<> ();

}


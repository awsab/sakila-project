package com.me.learning.catalog.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * Represents a spoken/written language used for film dubbing or originals.
 * Maps to the {@code language} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "language")
public class Language {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "language_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @Size (max = 20)
    @NotNull
    @Column (name = "name", nullable = false, length = 20)
    private String name;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

}


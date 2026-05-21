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
 * Represents a film performer/actor.
 * Maps to the {@code actor} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "actor")
public class Actor {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "actor_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Size (max = 45)
    @NotNull
    @Column (name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Size (max = 45)
    @NotNull
    @Column (name = "last_name", nullable = false, length = 45)
    private String lastName;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    /** All film appearances for this actor (via the film_actor join table). */
    @OneToMany (mappedBy = "actor")
    private Set<FilmActor> filmActors = new LinkedHashSet<> ();

}


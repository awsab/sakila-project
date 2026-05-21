package com.me.learning.inventory.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * Represents a single physical disc / copy of a film held at a store.
 *
 * <p>This entity is the bridge between the Catalog and the Store:
 * <ul>
 *   <li>{@code filmId} — cross-service reference to {@code film.film_id} owned
 *       by <em>catalog-service</em>.  Only the ID is stored here; the title,
 *       description and all other film metadata are resolved via a
 *       service-to-service call to catalog-service when needed.</li>
 *   <li>{@code store} — intra-service FK to {@link Store}; a real JPA
 *       {@code @ManyToOne} relationship because both tables live in this
 *       service's schema.</li>
 * </ul>
 *
 * Maps to the {@code inventory} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "inventory_id", columnDefinition = "mediumint UNSIGNED not null")
    private Integer id;

    /**
     * ID of the film this disc belongs to.
     * Cross-service reference — {@code film} is owned by <em>catalog-service</em>.
     * No JPA join; film metadata is fetched from catalog-service at runtime.
     */
    @NotNull
    @Column (name = "film_id", nullable = false,
            columnDefinition = "smallint UNSIGNED not null")
    private Integer filmId;

    /** The store where this disc is physically located. */
    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "store_id", nullable = false)
    private Store store;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

}


package com.me.learning.catalog.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * The central movie / film entity.
 *
 * <p>Rating is stored as a plain String that must be one of:
 * {@code G}, {@code PG}, {@code PG-13}, {@code R}, {@code NC-17}.
 * (MySQL's ENUM type is not portable; H2 maps the column as VARCHAR.)
 *
 * <p>Special-features is stored as a comma-separated String that may contain
 * any combination of: {@code Trailers}, {@code Commentaries},
 * {@code Deleted Scenes}, {@code Behind the Scenes}.
 * (MySQL's SET type has no direct JPA equivalent.)
 *
 * Maps to the {@code film} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "film")
public class Film {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "film_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Size (max = 255)
    @NotNull
    @Column (name = "title", nullable = false)
    private String title;

    /** Optional long-form plot description (TEXT column). */
    @Column (name = "description", columnDefinition = "text")
    private String description;

    /**
     * Four-digit release year stored as a Short.
     * MySQL YEAR type holds values 1901–2155; Short covers this range.
     */
    @Column (name = "release_year", columnDefinition = "year")
    private Short releaseYear;

    /** Primary spoken/dubbed language of the film. */
    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "language_id", nullable = false)
    private Language language;

    /** Original language before dubbing (nullable). */
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "original_language_id")
    private Language originalLanguage;

    /** Minimum rental duration in days. Default: 3. */
    @NotNull
    @ColumnDefault ("3")
    @Column (name = "rental_duration", columnDefinition = "tinyint UNSIGNED not null")
    private Short rentalDuration;

    /** Daily rental price. Default: 4.99. */
    @NotNull
    @ColumnDefault ("4.99")
    @Digits (integer = 2, fraction = 2)
    @Column (name = "rental_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal rentalRate;

    /** Running time in minutes (nullable). */
    @Column (name = "length", columnDefinition = "smallint UNSIGNED")
    private Short length;

    /** Cost to replace the film if lost/damaged. Default: 19.99. */
    @NotNull
    @ColumnDefault ("19.99")
    @Digits (integer = 3, fraction = 2)
    @Column (name = "replacement_cost", nullable = false, precision = 5, scale = 2)
    private BigDecimal replacementCost;

    /**
     * MPAA film rating.
     * Valid values: {@code G}, {@code PG}, {@code PG-13}, {@code R}, {@code NC-17}.
     * Stored as VARCHAR; default is {@code G}.
     */
    @ColumnDefault ("'G'")
    @Size (max = 5)
    @Column (name = "rating", length = 5)
    private String rating;

    /**
     * Comma-separated list of special features included on the disc.
     * Valid tokens: {@code Trailers}, {@code Commentaries},
     *               {@code Deleted Scenes}, {@code Behind the Scenes}.
     */
    @Column (name = "special_features")
    private String specialFeatures;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    /** Actor appearances linked to this film (via the film_actor join table). */
    @OneToMany (mappedBy = "film")
    private Set<FilmActor> filmActors = new LinkedHashSet<> ();

    /** Category assignments for this film (via the film_category join table). */
    @OneToMany (mappedBy = "film")
    private Set<FilmCategory> filmCategories = new LinkedHashSet<> ();

}


package com.me.learning.parent.inventoryservice.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "film")
public class Film implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "film_id", columnDefinition = "smallint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.SMALLINT)
    private Integer id;

    @Size (max = 255)
    @NotNull
    @Column (name = "title", nullable = false)
    private String title;

    @JdbcTypeCode (SqlTypes.VARCHAR)
    @Column (name = "description")
    private String description;

    @Column (name = "release_year")
    private int releaseYear;

    @ColumnDefault ("'3'")
    @Column (name = "rental_duration", columnDefinition = "tinyint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.TINYINT)
    private Short rentalDuration;

    @NotNull
    @ColumnDefault ("4.99")
    @Column (name = "rental_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal rentalRate;

    @Column (name = "length", columnDefinition = "smallint UNSIGNED")
    @JdbcTypeCode (SqlTypes.SMALLINT)
    private Integer length;

    @NotNull
    @ColumnDefault ("19.99")
    @Column (name = "replacement_cost", nullable = false, precision = 5, scale = 2)
    private BigDecimal replacementCost;

    @ColumnDefault ("'G'")
    @JdbcTypeCode (SqlTypes.VARCHAR)
    @Column (name = "rating")
    private String rating;

    @JdbcTypeCode (SqlTypes.VARCHAR)
    @Column (name = "special_features")
    private String specialFeatures;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @OneToMany (mappedBy = "film")
    private Set<FilmCategory> filmCategorySet = new HashSet<> ();

    @OneToMany (mappedBy = "film")
    private Set<FilmActor> filmActorsSet = new HashSet<> ();

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate () {
        this.lastUpdate = Instant.now ();
    }
}

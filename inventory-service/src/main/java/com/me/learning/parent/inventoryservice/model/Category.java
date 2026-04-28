package com.me.learning.parent.inventoryservice.model;

import java.io.Serializable;
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
@Table (name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "category_id", columnDefinition = "tinyint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.TINYINT)
    private Short id;

    @Size (max = 25)
    @NotNull
    @Column (name = "name", nullable = false, length = 25)
    private String name;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @OneToMany (mappedBy = "category")
    private Set<FilmCategory> categoryFilmSet = new HashSet<> ();

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate () {
        this.lastUpdate = Instant.now ();
    }
}

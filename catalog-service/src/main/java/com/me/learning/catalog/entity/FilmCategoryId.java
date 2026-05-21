package com.me.learning.catalog.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


/**
 * Composite primary key for the {@link FilmCategory} join table.
 * JPA requires embeddable IDs to implement {@link Serializable}.
 */
@Getter
@Setter
@Embeddable
public class FilmCategoryId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column (name = "film_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer filmId;

    @Column (name = "category_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short categoryId;

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FilmCategoryId other)) {
            return false;
        }
        return Objects.equals (filmId, other.filmId)
                && Objects.equals (categoryId, other.categoryId);
    }

    @Override
    public int hashCode () {
        return Objects.hash (filmId, categoryId);
    }

}


package com.me.learning.parent.inventoryservice.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class FilmCategoryId implements Serializable {

    private static final long serialVersionUID = 2484719608986104541L;

    @Column (name = "film_id", columnDefinition = "smallint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.SMALLINT)
    private Integer filmId;

    @Column (name = "category_id", columnDefinition = "tinyint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.TINYINT)
    private Short categoryId;


}

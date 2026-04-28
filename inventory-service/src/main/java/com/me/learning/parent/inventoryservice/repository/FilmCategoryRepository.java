package com.me.learning.parent.inventoryservice.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.inventoryservice.model.FilmCategory;
import com.me.learning.parent.inventoryservice.model.FilmCategoryId;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : JPA Repository for FilmCategory junction entity
 * Since    : Version 1.0
 */
@Repository
public interface FilmCategoryRepository extends JpaRepository<FilmCategory, FilmCategoryId>, JpaSpecificationExecutor<FilmCategory> {

    /**
     * Find all film-category relationships by category ID
     */
    List<FilmCategory> findByCategoryId (Short categoryId);

    /**
     * Find all film-category relationships by film ID
     */
    List<FilmCategory> findByFilmId (Integer filmId);

    /**
     * Delete all film-category relationships by category ID
     */
    @Modifying
    @Query ("DELETE FROM FilmCategory fc WHERE fc.category.id = :categoryId")
    void deleteByCategoryId (@Param ("categoryId") Short categoryId);

    /**
     * Delete all film-category relationships by film ID
     */
    @Modifying
    @Query ("DELETE FROM FilmCategory fc WHERE fc.film.id = :filmId")
    void deleteByFilmId (@Param ("filmId") Integer filmId);

    /**
     * Check if relationship exists
     */
    boolean existsByCategoryIdAndFilmId (Short categoryId, Integer filmId);

    /**
     * Count films in a category
     */
    long countByCategoryId (Short categoryId);

    /**
     * Count categories for a film
     */
    long countByFilmId (Integer filmId);
}


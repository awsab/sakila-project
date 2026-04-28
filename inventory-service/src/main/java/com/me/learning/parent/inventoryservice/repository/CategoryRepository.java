package com.me.learning.parent.inventoryservice.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.inventoryservice.model.Category;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : JPA Repository for Category entity
 * Since    : Version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Short>, JpaSpecificationExecutor<Category> {

    /**
     * Find category by name
     */
    Optional<Category> findByName (String name);

    /**
     * Find categories by name containing (case-insensitive)
     */
    List<Category> findByNameContainingIgnoreCase (String name);

    /**
     * Check if category exists by name
     */
    boolean existsByName (String name);

    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByNameAsc ();

    /**
     * Find categories by film
     */
    @Query ("SELECT DISTINCT c FROM Category c JOIN c.categoryFilmSet cf WHERE cf.film.id = :filmId")
    List<Category> findByFilmId (@Param ("filmId") Integer filmId);

    /**
     * Count films in category
     */
    @Query ("SELECT COUNT(fc) FROM FilmCategory fc WHERE fc.category.id = :categoryId")
    long countFilmsByCategoryId (@Param ("categoryId") Short categoryId);
}


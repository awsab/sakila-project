package com.me.learning.parent.inventoryservice.repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.inventoryservice.model.Film;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : JPA Repository for Film entity
 * Since    : Version 1.0
 */
@Repository
public interface FilmRepository extends JpaRepository<Film, Integer>, JpaSpecificationExecutor<Film> {

    /**
     * Find film by title
     */
    Optional<Film> findByTitle (String title);

    /**
     * Find films by title containing (case-insensitive)
     */
    List<Film> findByTitleContainingIgnoreCase (String title);

    /**
     * Find films by release year
     */
    List<Film> findByReleaseYear (int releaseYear);

    /**
     * Find films by rating
     */
    List<Film> findByRating (String rating);

    /**
     * Find films by rental rate less than or equal to
     */
    List<Film> findByRentalRateLessThanEqual (BigDecimal maxRate);

    /**
     * Find films by length between
     */
    List<Film> findByLengthBetween (Integer minLength, Integer maxLength);

    /**
     * Search films by title or description
     */
    @Query ("SELECT f FROM Film f WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Film> searchByTitleOrDescription (@Param ("searchTerm") String searchTerm);

    /**
     * Find films with pagination
     */
    Page<Film> findByRating (String rating, Pageable pageable);

    /**
     * Find films by category
     */
    @Query ("SELECT DISTINCT f FROM Film f JOIN f.filmCategorySet fc WHERE fc.category.id = :categoryId")
    List<Film> findByCategoryId (@Param ("categoryId") Short categoryId);

    /**
     * Find films by actor
     */
    @Query ("SELECT DISTINCT f FROM Film f JOIN f.filmActorsSet fa WHERE fa.actor.id = :actorId")
    List<Film> findByActorId (@Param ("actorId") Integer actorId);

    /**
     * Count films by rating
     */
    long countByRating (String rating);

    /**
     * Check if film exists by title
     */
    boolean existsByTitle (String title);
}


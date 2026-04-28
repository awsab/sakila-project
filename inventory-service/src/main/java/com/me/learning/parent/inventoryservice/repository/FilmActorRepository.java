package com.me.learning.parent.inventoryservice.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.inventoryservice.model.FilmActor;
import com.me.learning.parent.inventoryservice.model.FilmActorId;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : JPA Repository for FilmActor junction entity
 * Since    : Version 1.0
 */
@Repository
public interface FilmActorRepository extends JpaRepository<FilmActor, FilmActorId>, JpaSpecificationExecutor<FilmActor> {

    /**
     * Find all film-actor relationships by actor ID
     */
    List<FilmActor> findByActorId (Integer actorId);

    /**
     * Find all film-actor relationships by film ID
     */
    List<FilmActor> findByFilmId (Integer filmId);

    /**
     * Delete all film-actor relationships by actor ID
     */
    @Modifying
    @Query ("DELETE FROM FilmActor fa WHERE fa.actor.id = :actorId")
    void deleteByActorId (@Param ("actorId") Integer actorId);

    /**
     * Delete all film-actor relationships by film ID
     */
    @Modifying
    @Query ("DELETE FROM FilmActor fa WHERE fa.film.id = :filmId")
    void deleteByFilmId (@Param ("filmId") Integer filmId);

    /**
     * Check if relationship exists
     */
    boolean existsByActorIdAndFilmId (Integer actorId, Integer filmId);

    /**
     * Count films for an actor
     */
    long countByActorId (Integer actorId);

    /**
     * Count actors for a film
     */
    long countByFilmId (Integer filmId);
}


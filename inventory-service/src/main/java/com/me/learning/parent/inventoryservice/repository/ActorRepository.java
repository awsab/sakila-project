package com.me.learning.parent.inventoryservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.inventoryservice.model.Actor;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : JPA Repository for Actor entity
 * Since    : Version 1.0
 */
@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer>, JpaSpecificationExecutor<Actor> {

    /**
     * Find actor by first name and last name
     */
    Optional<Actor> findByFirstNameAndLastName (String firstName, String lastName);

    /**
     * Find actors by first name (case-insensitive)
     */
    List<Actor> findByFirstNameContainingIgnoreCase (String firstName);

    /**
     * Find actors by last name (case-insensitive)
     */
    List<Actor> findByLastNameContainingIgnoreCase (String lastName);

    /**
     * Search actors by first name or last name
     */
    @Query ("SELECT a FROM Actor a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Actor> searchByName (@Param ("searchTerm") String searchTerm);

    /**
     * Check if actor exists by first name and last name
     */
    boolean existsByFirstNameAndLastName (String firstName, String lastName);

    /**
     * Find all actors ordered by last name
     */
    List<Actor> findAllByOrderByLastNameAsc ();
}


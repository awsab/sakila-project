package com.me.learning.parent.inventoryservice.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.inventoryservice.model.Language;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : JPA Repository for Language entity
 * Since    : Version 1.0
 */
@Repository
public interface LanguageRepository extends JpaRepository<Language, Short>, JpaSpecificationExecutor<Language> {

    /**
     * Find language by name
     */
    Optional<Language> findByName (String name);

    /**
     * Find languages by name containing (case-insensitive)
     */
    List<Language> findByNameContainingIgnoreCase (String name);

    /**
     * Check if language exists by name
     */
    boolean existsByName (String name);

    /**
     * Find all languages ordered by name
     */
    List<Language> findAllByOrderByNameAsc ();
}


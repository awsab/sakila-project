package com.me.learning.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.me.learning.catalog.entity.Film;

/**
 * Repository for {@link Film} with eager graph loading for API read models.
 */
public interface FilmRepository extends JpaRepository<Film, Integer>, JpaSpecificationExecutor<Film> {

    @Override
    @NonNull
    @EntityGraph (attributePaths = {
            "language",
            "originalLanguage",
            "filmActors",
            "filmActors.actor",
            "filmCategories",
            "filmCategories.category"
    })
    List<Film> findAll ();

    @Override
    @NonNull
    @EntityGraph (attributePaths = {
            "language",
            "originalLanguage",
            "filmActors",
            "filmActors.actor",
            "filmCategories",
            "filmCategories.category"
    })
    Page<Film> findAll (@NonNull Pageable pageable);

    @Override
    @NonNull
    @EntityGraph (attributePaths = {
            "language",
            "originalLanguage",
            "filmActors",
            "filmActors.actor",
            "filmCategories",
            "filmCategories.category"
    })
    Optional<Film> findById (@NonNull Integer id);
}


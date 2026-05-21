package com.me.learning.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.me.learning.catalog.entity.FilmActor;
import com.me.learning.catalog.entity.FilmActorId;

public interface FilmActorRepository extends JpaRepository<FilmActor, FilmActorId>, JpaSpecificationExecutor<FilmActor> {

    @Modifying
    @Query ("delete from FilmActor fa where fa.film.id = :filmId")
    void deleteByFilmId (@Param ("filmId") Integer filmId);
}


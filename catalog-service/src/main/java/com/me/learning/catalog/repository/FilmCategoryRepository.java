package com.me.learning.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.me.learning.catalog.entity.FilmCategory;
import com.me.learning.catalog.entity.FilmCategoryId;

public interface FilmCategoryRepository extends JpaRepository<FilmCategory, FilmCategoryId>, JpaSpecificationExecutor<FilmCategory> {

    @Modifying
    @Query ("delete from FilmCategory fc where fc.film.id = :filmId")
    void deleteByFilmId (@Param ("filmId") Integer filmId);
}


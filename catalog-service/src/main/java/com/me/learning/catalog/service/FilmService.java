package com.me.learning.catalog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.catalog.dto.FilmRequestDto;
import com.me.learning.catalog.dto.FilmResponseDto;

/**
 * Service interface for {@link com.me.learning.catalog.entity.Film} CRUD operations.
 */
public interface FilmService {

    FilmResponseDto create (FilmRequestDto dto);

    FilmResponseDto update (Integer id, FilmRequestDto dto);

    FilmResponseDto partialUpdate (Integer id, FilmRequestDto dto);

    FilmResponseDto findById (Integer id);

    List<FilmResponseDto> findAll ();

    Page<FilmResponseDto> findAll (Pageable pageable);

    void delete (Integer id);

    boolean existsById (Integer id);

    long count ();
}


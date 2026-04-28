package com.me.learning.parent.inventoryservice.service;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.inventoryservice.dto.request.FilmRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.FilmResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.FilmUpdateDTO;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service interface for Film entity operations
 * Since    : Version 1.0
 */
public interface FilmService {

    /**
     * Create a new film
     *
     * @param requestDTO Film creation request
     *
     * @return Created film response
     */
    FilmResponseDTO createFilm (FilmRequestDTO requestDTO);

    /**
     * Update an existing film
     *
     * @param id        Film ID
     * @param updateDTO Film update request
     *
     * @return Updated film response
     */
    FilmResponseDTO updateFilm (Integer id, FilmUpdateDTO updateDTO);

    /**
     * Partially update an existing film
     *
     * @param id        Film ID
     * @param updateDTO Film update request
     *
     * @return Updated film response
     */
    FilmResponseDTO patchFilm (Integer id, FilmUpdateDTO updateDTO);

    /**
     * Get film by ID
     *
     * @param id Film ID
     *
     * @return Film response
     */
    FilmResponseDTO getFilmById (Integer id);

    /**
     * Get film by title
     *
     * @param title Film title
     *
     * @return Film response
     */
    FilmResponseDTO getFilmByTitle (String title);

    /**
     * Get all films
     *
     * @return List of all films
     */
    List<FilmResponseDTO> getAllFilms ();

    /**
     * Get all films with pagination
     *
     * @param pageable Pagination information
     *
     * @return Page of films
     */
    Page<FilmResponseDTO> getAllFilms (Pageable pageable);

    /**
     * Search films by title
     *
     * @param title Title to search
     *
     * @return List of matching films
     */
    List<FilmResponseDTO> searchFilmsByTitle (String title);

    /**
     * Search films by title or description
     *
     * @param searchTerm Search term
     *
     * @return List of matching films
     */
    List<FilmResponseDTO> searchFilmsByTitleOrDescription (String searchTerm);

    /**
     * Get films by release year
     *
     * @param releaseYear Release year
     *
     * @return List of films
     */
    List<FilmResponseDTO> getFilmsByReleaseYear (int releaseYear);

    /**
     * Get films by rating
     *
     * @param rating Film rating (G, PG, PG-13, R, NC-17)
     *
     * @return List of films
     */
    List<FilmResponseDTO> getFilmsByRating (String rating);

    /**
     * Get films by rating with pagination
     *
     * @param rating   Film rating
     * @param pageable Pagination information
     *
     * @return Page of films
     */
    Page<FilmResponseDTO> getFilmsByRating (String rating, Pageable pageable);

    /**
     * Get films by rental rate less than or equal to max rate
     *
     * @param maxRate Maximum rental rate
     *
     * @return List of films
     */
    List<FilmResponseDTO> getFilmsByRentalRate (BigDecimal maxRate);

    /**
     * Get films by length range
     *
     * @param minLength Minimum length
     * @param maxLength Maximum length
     *
     * @return List of films
     */
    List<FilmResponseDTO> getFilmsByLengthRange (Integer minLength, Integer maxLength);

    /**
     * Get films by category ID
     *
     * @param categoryId Category ID
     *
     * @return List of films
     */
    List<FilmResponseDTO> getFilmsByCategoryId (Short categoryId);

    /**
     * Get films by actor ID
     *
     * @param actorId Actor ID
     *
     * @return List of films
     */
    List<FilmResponseDTO> getFilmsByActorId (Integer actorId);

    /**
     * Count films by rating
     *
     * @param rating Film rating
     *
     * @return Count of films
     */
    long countFilmsByRating (String rating);

    /**
     * Check if film exists by ID
     *
     * @param id Film ID
     *
     * @return true if exists, false otherwise
     */
    boolean existsById (Integer id);

    /**
     * Check if film exists by title
     *
     * @param title Film title
     *
     * @return true if exists, false otherwise
     */
    boolean existsByTitle (String title);

    /**
     * Delete film by ID
     *
     * @param id Film ID
     */
    void deleteFilm (Integer id);

    /**
     * Count total films
     *
     * @return Total count of films
     */
    long countFilms ();
}


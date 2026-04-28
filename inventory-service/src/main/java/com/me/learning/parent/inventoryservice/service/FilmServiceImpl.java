package com.me.learning.parent.inventoryservice.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.inventoryservice.dto.request.FilmRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.FilmResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.FilmUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.FilmMapper;
import com.me.learning.parent.inventoryservice.model.Film;
import com.me.learning.parent.inventoryservice.repository.FilmRepository;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service implementation for Film entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;

    @Override
    @Transactional
    public FilmResponseDTO createFilm (FilmRequestDTO requestDTO) {
        log.debug ("Creating new film: {}", requestDTO.getTitle ());

        // Check if film already exists
        if ( filmRepository.existsByTitle (requestDTO.getTitle ()) ) {
            throw new IllegalArgumentException ("Film with title '" + requestDTO.getTitle () + "' already exists");
        }

        Film film = filmMapper.toEntity (requestDTO);
        Film savedFilm = filmRepository.save (film);

        log.info ("Created film with ID: {}", savedFilm.getId ());
        return filmMapper.toDto (savedFilm);
    }

    @Override
    @Transactional
    public FilmResponseDTO updateFilm (Integer id, FilmUpdateDTO updateDTO) {
        log.debug ("Updating film with ID: {}", id);

        Film existingFilm = filmRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException ("Film", "id", id));

        // Check if title change conflicts with existing film
        if ( !existingFilm.getTitle ().equals (updateDTO.getTitle ()) &&
                filmRepository.existsByTitle (updateDTO.getTitle ()) ) {
            throw new DuplicateResourceException ("Film", "title", updateDTO.getTitle ());
        }

        // Update film fields
        existingFilm.setTitle (updateDTO.getTitle ());
        existingFilm.setDescription (updateDTO.getDescription ());
        existingFilm.setReleaseYear (updateDTO.getReleaseYear ());
        existingFilm.setRentalDuration (updateDTO.getRentalDuration ());
        existingFilm.setRentalRate (updateDTO.getRentalRate ());
        existingFilm.setLength (updateDTO.getLength ());
        existingFilm.setReplacementCost (updateDTO.getReplacementCost ());
        existingFilm.setRating (updateDTO.getRating ());
        existingFilm.setSpecialFeatures (updateDTO.getSpecialFeatures ());

        Film updatedFilm = filmRepository.save (existingFilm);

        log.info ("Updated film with ID: {}", id);
        return filmMapper.toDto (updatedFilm);
    }

    @Override
    @Transactional
    public FilmResponseDTO patchFilm (Integer id, FilmUpdateDTO updateDTO) {
        log.debug ("Partially updating film with ID: {}", id);

        Film existingFilm = filmRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException ("Film", "id", id));

        filmMapper.updateEntity (updateDTO, existingFilm);

        Film updatedFilm = filmRepository.save (existingFilm);

        log.info ("Patched film with ID: {}", id);
        return filmMapper.toDto (updatedFilm);
    }

    @Override
    public FilmResponseDTO getFilmById (Integer id) {
        log.debug ("Fetching film with ID: {}", id);

        Film film = filmRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException ("Film", "id", id));

        return filmMapper.toDto (film);
    }

    @Override
    public FilmResponseDTO getFilmByTitle (String title) {
        log.debug ("Fetching film with title: {}", title);

        Film film = filmRepository.findByTitle (title)
                .orElseThrow (() -> new ResourceNotFoundException ("Film", "title", title));

        return filmMapper.toDto (film);
    }

    @Override
    public List<FilmResponseDTO> getAllFilms () {
        log.debug ("Fetching all films");

        List<Film> films = filmRepository.findAll ();
        return filmMapper.toDtoList (films);
    }

    @Override
    public Page<FilmResponseDTO> getAllFilms (Pageable pageable) {
        log.debug ("Fetching films with pagination: page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());

        Page<Film> filmPage = filmRepository.findAll (pageable);
        return filmPage.map (filmMapper::toDto);
    }

    @Override
    public List<FilmResponseDTO> searchFilmsByTitle (String title) {
        log.debug ("Searching films by title: {}", title);

        List<Film> films = filmRepository.findByTitleContainingIgnoreCase (title);
        return filmMapper.toDtoList (films);
    }

    @Override
    public List<FilmResponseDTO> searchFilmsByTitleOrDescription (String searchTerm) {
        log.debug ("Searching films by title or description: {}", searchTerm);

        List<Film> films = filmRepository.searchByTitleOrDescription (searchTerm);
        return filmMapper.toDtoList (films);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByReleaseYear (int releaseYear) {
        log.debug ("Fetching films by release year: {}", releaseYear);

        List<Film> films = filmRepository.findByReleaseYear (releaseYear);
        return filmMapper.toDtoList (films);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByRating (String rating) {
        log.debug ("Fetching films by rating: {}", rating);

        List<Film> films = filmRepository.findByRating (rating);
        return filmMapper.toDtoList (films);
    }

    @Override
    public Page<FilmResponseDTO> getFilmsByRating (String rating, Pageable pageable) {
        log.debug ("Fetching films by rating with pagination: {}", rating);

        Page<Film> filmPage = filmRepository.findByRating (rating, pageable);
        return filmPage.map (filmMapper::toDto);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByRentalRate (BigDecimal maxRate) {
        log.debug ("Fetching films with rental rate <= {}", maxRate);

        List<Film> films = filmRepository.findByRentalRateLessThanEqual (maxRate);
        return filmMapper.toDtoList (films);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByLengthRange (Integer minLength, Integer maxLength) {
        log.debug ("Fetching films with length between {} and {}", minLength, maxLength);

        List<Film> films = filmRepository.findByLengthBetween (minLength, maxLength);
        return filmMapper.toDtoList (films);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByCategoryId (Short categoryId) {
        log.debug ("Fetching films by category ID: {}", categoryId);

        List<Film> films = filmRepository.findByCategoryId (categoryId);
        return filmMapper.toDtoList (films);
    }

    @Override
    public List<FilmResponseDTO> getFilmsByActorId (Integer actorId) {
        log.debug ("Fetching films by actor ID: {}", actorId);

        List<Film> films = filmRepository.findByActorId (actorId);
        return filmMapper.toDtoList (films);
    }

    @Override
    public long countFilmsByRating (String rating) {
        log.debug ("Counting films by rating: {}", rating);
        return filmRepository.countByRating (rating);
    }

    @Override
    public boolean existsById (Integer id) {
        log.debug ("Checking if film exists with ID: {}", id);
        return filmRepository.existsById (id);
    }

    @Override
    public boolean existsByTitle (String title) {
        log.debug ("Checking if film exists with title: {}", title);
        return filmRepository.existsByTitle (title);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"films", "film"}, allEntries = true)
    public void deleteFilm (Integer id) {
        log.debug ("Deleting film with ID: {}", id);

        if ( !filmRepository.existsById (id) ) {
            throw new ResourceNotFoundException ("Film", "id", id);
        }

        filmRepository.deleteById (id);
        log.info ("Deleted film with ID: {}", id);
    }

    @Override
    public long countFilms () {
        log.debug ("Counting total films");
        return filmRepository.count ();
    }
}


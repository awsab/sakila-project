package com.me.learning.parent.inventoryservice.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.inventoryservice.dto.request.LanguageRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.LanguageResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.LanguageUpdateDTO;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service interface for Language entity operations
 * Since    : Version 1.0
 */
public interface LanguageService {

    /**
     * Create a new language
     *
     * @param requestDTO Language creation request
     *
     * @return Created language response
     */
    LanguageResponseDTO createLanguage (LanguageRequestDTO requestDTO);

    /**
     * Update an existing language
     *
     * @param id        Language ID
     * @param updateDTO Language update request
     *
     * @return Updated language response
     */
    LanguageResponseDTO updateLanguage (Short id, LanguageUpdateDTO updateDTO);

    /**
     * Partially update an existing language
     *
     * @param id        Language ID
     * @param updateDTO Language update request
     *
     * @return Updated language response
     */
    LanguageResponseDTO patchLanguage (Short id, LanguageUpdateDTO updateDTO);

    /**
     * Get language by ID
     *
     * @param id Language ID
     *
     * @return Language response
     */
    LanguageResponseDTO getLanguageById (Short id);

    /**
     * Get language by name
     *
     * @param name Language name
     *
     * @return Language response
     */
    LanguageResponseDTO getLanguageByName (String name);

    /**
     * Get all languages
     *
     * @return List of all languages
     */
    List<LanguageResponseDTO> getAllLanguages ();

    /**
     * Get all languages with pagination
     *
     * @param pageable Pagination information
     *
     * @return Page of languages
     */
    Page<LanguageResponseDTO> getAllLanguages (Pageable pageable);

    /**
     * Search languages by name
     *
     * @param name Name to search
     *
     * @return List of matching languages
     */
    List<LanguageResponseDTO> searchLanguagesByName (String name);

    /**
     * Get all languages sorted by name
     *
     * @return List of languages sorted by name
     */
    List<LanguageResponseDTO> getAllLanguagesSortedByName ();

    /**
     * Check if language exists by ID
     *
     * @param id Language ID
     *
     * @return true if exists, false otherwise
     */
    boolean existsById (Short id);

    /**
     * Check if language exists by name
     *
     * @param name Language name
     *
     * @return true if exists, false otherwise
     */
    boolean existsByName (String name);

    /**
     * Delete language by ID
     *
     * @param id Language ID
     */
    void deleteLanguage (Short id);

    /**
     * Count total languages
     *
     * @return Total count of languages
     */
    long countLanguages ();
}


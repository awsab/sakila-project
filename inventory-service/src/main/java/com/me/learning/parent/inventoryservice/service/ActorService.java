package com.me.learning.parent.inventoryservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.inventoryservice.dto.request.ActorRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.ActorResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.ActorUpdateDTO;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service interface for Actor entity operations
 * Since    : Version 1.0
 */
public interface ActorService {

    /**
     * Create a new actor
     *
     * @param requestDTO Actor creation request
     *
     * @return Created actor response
     */
    ActorResponseDTO createActor (ActorRequestDTO requestDTO);

    /**
     * Update an existing actor
     *
     * @param id        Actor ID
     * @param updateDTO Actor update request
     *
     * @return Updated actor response
     */
    ActorResponseDTO updateActor (Integer id, ActorUpdateDTO updateDTO);

    /**
     * Partially update an existing actor
     *
     * @param id        Actor ID
     * @param updateDTO Actor update request
     *
     * @return Updated actor response
     */
    ActorResponseDTO patchActor (Integer id, ActorUpdateDTO updateDTO);

    /**
     * Get actor by ID
     *
     * @param id Actor ID
     *
     * @return Actor response
     */
    ActorResponseDTO getActorById (Integer id);

    /**
     * Get all actors
     *
     * @return List of all actors
     */
    List<ActorResponseDTO> getAllActors ();

    /**
     * Get all actors with pagination
     *
     * @param pageable Pagination information
     *
     * @return Page of actors
     */
    Page<ActorResponseDTO> getAllActors (Pageable pageable);

    /**
     * Search actors by name (first or last)
     *
     * @param searchTerm Search term
     *
     * @return List of matching actors
     */
    List<ActorResponseDTO> searchActorsByName (String searchTerm);

    /**
     * Get actors by first name
     *
     * @param firstName First name to search
     *
     * @return List of matching actors
     */
    List<ActorResponseDTO> getActorsByFirstName (String firstName);

    /**
     * Get actors by last name
     *
     * @param lastName Last name to search
     *
     * @return List of matching actors
     */
    List<ActorResponseDTO> getActorsByLastName (String lastName);

    /**
     * Get all actors sorted by last name
     *
     * @return List of actors sorted by last name
     */
    List<ActorResponseDTO> getAllActorsSortedByLastName ();

    /**
     * Check if actor exists by ID
     *
     * @param id Actor ID
     *
     * @return true if exists, false otherwise
     */
    boolean existsById (Integer id);

    /**
     * Check if actor exists by name
     *
     * @param firstName First name
     * @param lastName  Last name
     *
     * @return true if exists, false otherwise
     */
    boolean existsByName (String firstName, String lastName);

    /**
     * Delete actor by ID
     *
     * @param id Actor ID
     */
    void deleteActor (Integer id);

    /**
     * Count total actors
     *
     * @return Total count of actors
     */
    long countActors ();
}


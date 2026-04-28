package com.me.learning.parent.inventoryservice.service;

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
import com.me.learning.parent.inventoryservice.dto.request.ActorRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.ActorResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.ActorUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.ActorMapper;
import com.me.learning.parent.inventoryservice.model.Actor;
import com.me.learning.parent.inventoryservice.repository.ActorRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service implementation for Actor entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class ActorServiceImpl implements ActorService {

    private static final String ACTOR_RESOURCE = "Actor";
    private static final String FIELD_ID = "id";
    private static final String FIELD_FULL_NAME = "firstName+lastName";
    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    @Override
    @Transactional
    public ActorResponseDTO createActor (ActorRequestDTO requestDTO) {
        log.debug ("Creating new actor: {} {}", requestDTO.getFirstName (), requestDTO.getLastName ());

        if ( actorRepository.existsByFirstNameAndLastName (requestDTO.getFirstName (), requestDTO.getLastName ()) ) {
            throw new DuplicateResourceException (ACTOR_RESOURCE, FIELD_FULL_NAME,
                    requestDTO.getFirstName () + " " + requestDTO.getLastName ());
        }

        Actor actor = actorMapper.toEntity (requestDTO);
        Actor savedActor = actorRepository.save (actor);

        log.info ("Created actor with ID: {}", savedActor.getId ());
        return actorMapper.toDto (savedActor);
    }

    @Override
    @Transactional
    public ActorResponseDTO updateActor (Integer id, ActorUpdateDTO updateDTO) {
        log.debug ("Updating actor with ID: {}", id);

        Actor existingActor = actorRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (ACTOR_RESOURCE, FIELD_ID, id));

        if ( (!existingActor.getFirstName ().equals (updateDTO.getFirstName ()) ||
                !existingActor.getLastName ().equals (updateDTO.getLastName ())) &&
                actorRepository.existsByFirstNameAndLastName (updateDTO.getFirstName (), updateDTO.getLastName ()) ) {
            throw new DuplicateResourceException (ACTOR_RESOURCE, FIELD_FULL_NAME,
                    updateDTO.getFirstName () + " " + updateDTO.getLastName ());
        }

        existingActor.setFirstName (updateDTO.getFirstName ());
        existingActor.setLastName (updateDTO.getLastName ());

        Actor updatedActor = actorRepository.save (existingActor);

        log.info ("Updated actor with ID: {}", id);
        return actorMapper.toDto (updatedActor);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"actors", "actor"}, allEntries = true)
    public ActorResponseDTO patchActor (Integer id, ActorUpdateDTO updateDTO) {
        log.debug ("Partially updating actor with ID: {}", id);

        if ( !id.equals (updateDTO.getId ()) ) {
            throw new IllegalArgumentException ("Actor Id in the request parameter and the body is different : " + id);
        }

        Actor existingActor = actorRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (ACTOR_RESOURCE, FIELD_ID, id));

        actorMapper.updateEntity (updateDTO, existingActor);

        Actor updatedActor = actorRepository.save (existingActor);

        log.info ("Patched actor with ID: {}", id);
        return actorMapper.toDto (updatedActor);
    }

    @Override
    public ActorResponseDTO getActorById (Integer id) {
        log.debug ("Fetching actor with ID: {}", id);

        Actor actor = actorRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (ACTOR_RESOURCE, FIELD_ID, id));

        return actorMapper.toDto (actor);
    }

    @Override
    public List<ActorResponseDTO> getAllActors () {
        log.debug ("Fetching all actors");

        List<Actor> actors = actorRepository.findAll ();
        return actorMapper.toDtoList (actors);
    }

    @Override
    public Page<ActorResponseDTO> getAllActors (Pageable pageable) {
        log.debug ("Fetching actors with pagination: page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());

        Page<Actor> actorPage = actorRepository.findAll (pageable);
        return actorPage.map (actorMapper::toDto);
    }

    @Override
    public List<ActorResponseDTO> searchActorsByName (String searchTerm) {
        log.debug ("Searching actors by name: {}", searchTerm);

        List<Actor> actors = actorRepository.searchByName (searchTerm);
        return actorMapper.toDtoList (actors);
    }

    @Override
    public List<ActorResponseDTO> getActorsByFirstName (String firstName) {
        log.debug ("Fetching actors by first name: {}", firstName);

        List<Actor> actors = actorRepository.findByFirstNameContainingIgnoreCase (firstName);
        return actorMapper.toDtoList (actors);
    }

    @Override
    public List<ActorResponseDTO> getActorsByLastName (String lastName) {
        log.debug ("Fetching actors by last name: {}", lastName);

        List<Actor> actors = actorRepository.findByLastNameContainingIgnoreCase (lastName);
        return actorMapper.toDtoList (actors);
    }

    @Override
    public List<ActorResponseDTO> getAllActorsSortedByLastName () {
        log.debug ("Fetching all actors sorted by last name");

        List<Actor> actors = actorRepository.findAllByOrderByLastNameAsc ();
        return actorMapper.toDtoList (actors);
    }

    @Override
    public boolean existsById (Integer id) {
        log.debug ("Checking if actor exists with ID: {}", id);
        return actorRepository.existsById (id);
    }

    @Override
    public boolean existsByName (String firstName, String lastName) {
        log.debug ("Checking if actor exists with name: {} {}", firstName, lastName);
        return actorRepository.existsByFirstNameAndLastName (firstName, lastName);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"actors", "actor"}, allEntries = true)
    public void deleteActor (Integer id) {
        log.debug ("Deleting actor with ID: {}", id);

        if ( !actorRepository.existsById (id) ) {
            throw new ResourceNotFoundException (ACTOR_RESOURCE, FIELD_ID, id);
        }

        actorRepository.deleteById (id);
        log.info ("Deleted actor with ID: {}", id);
    }

    @Override
    public long countActors () {
        log.debug ("Counting total actors");
        return actorRepository.count ();
    }
}

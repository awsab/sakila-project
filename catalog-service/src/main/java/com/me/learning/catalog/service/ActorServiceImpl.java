package com.me.learning.catalog.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;
import com.me.learning.catalog.entity.Actor;
import com.me.learning.catalog.mapper.ActorMapper;
import com.me.learning.catalog.repository.ActorRepository;
import com.me.learning.framework.web.errors.ResourceNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class ActorServiceImpl implements ActorService {

    private static final String RESOURCE_ACTOR = "Actor";
    private static final String FIELD_ID = "id";

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    @Override
    @Transactional
    public ActorResponseDto create (ActorRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.id () != null) {
            throw new IllegalArgumentException ("New actor should not have an ID");
        }

        Actor actor = actorMapper.toEntity (dto);
        actor.setId (null);
        actor.setLastUpdate (resolveLastUpdate (dto));
        Actor saved = actorRepository.save (actor);
        log.info ("Created actor with ID: {}", saved.getId ());
        return actorMapper.toResponseDto (saved);
    }

    @Override
    @Transactional
    public ActorResponseDto update (Integer id, ActorRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.firstName () == null) {
            throw new IllegalArgumentException ("First name cannot be null");
        }
        if (dto.lastName () == null) {
            throw new IllegalArgumentException ("Last name cannot be null");
        }
        if (dto.id () == null) {
            throw new IllegalArgumentException ("Actor ID cannot be null");
        }

        Actor actor = actorMapper.toEntity (dto);
        actor.setId (id);
        actor.setLastUpdate (resolveLastUpdate (dto));

        Actor saved = actorRepository.save (actor);
        log.info ("Updated actor with ID: {}", id);
        return actorMapper.toResponseDto (saved);
    }

    @Override
    @Transactional
    public ActorResponseDto partialUpdate (Integer id, ActorRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.id () == null) {
            throw new IllegalArgumentException ("Actor ID cannot be null");
        }

        Actor existing = actorRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_ACTOR, FIELD_ID, id));

        actorMapper.partialUpdate (dto, existing);
        existing.setLastUpdate (resolveLastUpdate (dto));

        Actor saved = actorRepository.save (existing);
        log.info ("Patched actor with ID: {}", id);
        return actorMapper.toResponseDto (saved);
    }

    @Override
    public ActorResponseDto findById (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        return actorRepository.findById (id)
                .map (actorMapper::toResponseDto)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_ACTOR, FIELD_ID, id));
    }

    @Override
    public List<ActorResponseDto> findAll () {
        return actorRepository.findAll ().stream ().map (actorMapper::toResponseDto).toList ();
    }

    @Override
    public Page<ActorResponseDto> findAll (Pageable pageable) {
        return actorRepository.findAll (pageable).map (actorMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void delete (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        if (!actorRepository.existsById (id)) {
            throw new IllegalArgumentException ("Actor with ID " + id + " does not exist");
        }
        actorRepository.deleteById (id);
        log.info ("Deleted actor with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        return actorRepository.existsById (id);
    }

    @Override
    public long count () {
        long total = actorRepository.count ();
        if (total < 0) {
            throw new IllegalArgumentException ("Count cannot be negative");
        }
        return total;
    }

    private Instant resolveLastUpdate (ActorRequestDto dto) {
        return Optional.ofNullable (dto.lastUpdate ()).orElseGet (Instant::now);
    }
}

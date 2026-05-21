package com.me.learning.catalog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;

/**
 * Service interface for {@link com.me.learning.catalog.entity.Actor} CRUD operations.
 */
public interface ActorService {

    ActorResponseDto create (ActorRequestDto dto);

    ActorResponseDto update (Integer id, ActorRequestDto dto);

    ActorResponseDto partialUpdate (Integer id, ActorRequestDto dto);

    ActorResponseDto findById (Integer id);

    List<ActorResponseDto> findAll ();

    Page<ActorResponseDto> findAll (Pageable pageable);

    void delete (Integer id);

    boolean existsById (Integer id);

    long count ();
}


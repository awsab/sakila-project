package com.me.learning.parent.paymentservice.service;

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
import com.me.learning.parent.paymentservice.dto.RentalRequest;
import com.me.learning.parent.paymentservice.dto.RentalResponse;
import com.me.learning.parent.paymentservice.dto.RentalUpdateRequest;
import com.me.learning.parent.paymentservice.entity.Rental;
import com.me.learning.parent.paymentservice.mapper.RentalMapper;
import com.me.learning.parent.paymentservice.repository.RentalRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Service implementation for Rental entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class RentalServiceImpl implements RentalService {

    private static final String RESOURCE   = "Rental";
    private static final String FIELD_ID   = "id";
    private static final String FIELD_NAME = "name";

    private final RentalRepository repository;
    private final RentalMapper mapper;

    @Override
    @Transactional
    @CacheEvict (value = {"rentals", "rental"}, allEntries = true)
    public RentalResponse createRental (RentalRequest request) {
        log.debug ("Creating rental: {}", request.name ());

        if (repository.existsByNameIgnoreCase (request.name ())) {
            throw new DuplicateResourceException (RESOURCE, FIELD_NAME, request.name ());
        }
        Rental entity = mapper.toEntity (request);
        Rental saved  = repository.save (entity);

        log.info ("Created rental with ID: {}", saved.getId ());
        return mapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"rentals", "rental"}, allEntries = true)
    public RentalResponse updateRental (Integer id, RentalUpdateRequest request) {
        log.debug ("Updating rental with ID: {}", id);

        Rental existing = repository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id));

        // TODO: map fields from request to existing entity
        // existing.setName (request.name ());

        Rental updated = repository.save (existing);
        log.info ("Updated rental with ID: {}", id);
        return mapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"rentals", "rental"}, allEntries = true)
    public RentalResponse patchRental (Integer id, RentalUpdateRequest request) {
        log.debug ("Patching rental with ID: {}", id);

        Rental existing = repository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id));

        mapper.updateEntity (request, existing);
        return mapper.toResponse (repository.save (existing));
    }

    @Override
    public RentalResponse getRentalById (Integer id) {
        log.debug ("Fetching rental with ID: {}", id);
        return mapper.toResponse (
                repository.findById (id)
                        .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id)));
    }

    @Override
    public List<RentalResponse> getAllRentals () {
        log.debug ("Fetching all rentals");
        return mapper.toResponseList (repository.findAll ());
    }

    @Override
    public Page<RentalResponse> getAllRentals (Pageable pageable) {
        log.debug ("Fetching rentals - page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return repository.findAll (pageable).map (mapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"rentals", "rental"}, allEntries = true)
    public void deleteRental (Integer id) {
        log.debug ("Deleting rental with ID: {}", id);

        if ( !repository.existsById (id) ) {
            throw new ResourceNotFoundException (RESOURCE, FIELD_ID, id);
        }

        repository.deleteById (id);
        log.info ("Deleted rental with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        return repository.existsById (id);
    }

    @Override
    public long countRentals () {
        return repository.count ();
    }
}

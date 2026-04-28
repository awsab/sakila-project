package com.me.learning.parent.customerservice.service;

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
import com.me.learning.parent.customerservice.dto.CountryRequest;
import com.me.learning.parent.customerservice.dto.CountryResponse;
import com.me.learning.parent.customerservice.dto.CountryUpdateRequest;
import com.me.learning.parent.customerservice.entity.Country;
import com.me.learning.parent.customerservice.mapper.CountryMapper;
import com.me.learning.parent.customerservice.repository.CountryRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service implementation for Country entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class CountryServiceImpl implements CountryService {

    private static final String COUNTRY_RESOURCE = "Country";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "country";

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    @Transactional
    @CacheEvict (value = {"countries", "country"}, allEntries = true)
    public CountryResponse createCountry (CountryRequest request) {
        log.debug ("Creating new country: {}", request.country ());

        if ( countryRepository.existsByCountryIgnoreCase (request.country ()) ) {
            throw new DuplicateResourceException (COUNTRY_RESOURCE, FIELD_NAME, request.country ());
        }

        Country country = countryMapper.toEntity (request);
        Country saved = countryRepository.save (country);

        log.info ("Created country with ID: {}", saved.getId ());
        return countryMapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"countries", "country"}, allEntries = true)
    public CountryResponse updateCountry (Integer id, CountryUpdateRequest request) {
        log.debug ("Updating country with ID: {}", id);

        Country existing = countryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (COUNTRY_RESOURCE, FIELD_ID, id));

        if ( !existing.getCountry ().equalsIgnoreCase (request.country ()) &&
                countryRepository.existsByCountryIgnoreCase (request.country ()) ) {
            throw new DuplicateResourceException (COUNTRY_RESOURCE, FIELD_NAME, request.country ());
        }

        existing.setCountry (request.country ());
        Country updated = countryRepository.save (existing);

        log.info ("Updated country with ID: {}", id);
        return countryMapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"countries", "country"}, allEntries = true)
    public CountryResponse patchCountry (Integer id, CountryUpdateRequest request) {
        log.debug ("Patching country with ID: {}", id);

        Country existing = countryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (COUNTRY_RESOURCE, FIELD_ID, id));

        countryMapper.updateEntity (request, existing);
        Country updated = countryRepository.save (existing);

        log.info ("Patched country with ID: {}", id);
        return countryMapper.toResponse (updated);
    }

    @Override
    public CountryResponse getCountryById (Integer id) {
        log.debug ("Fetching country with ID: {}", id);

        Country country = countryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (COUNTRY_RESOURCE, FIELD_ID, id));

        return countryMapper.toResponse (country);
    }

    @Override
    public List<CountryResponse> getAllCountries () {
        log.debug ("Fetching all countries");
        return countryMapper.toResponseList (countryRepository.findAll ());
    }

    @Override
    public Page<CountryResponse> getAllCountries (Pageable pageable) {
        log.debug ("Fetching countries â€” page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return countryRepository.findAll (pageable).map (countryMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"countries", "country"}, allEntries = true)
    public void deleteCountry (Integer id) {
        log.debug ("Deleting country with ID: {}", id);

        if ( !countryRepository.existsById (id) ) {
            throw new ResourceNotFoundException (COUNTRY_RESOURCE, FIELD_ID, id);
        }

        countryRepository.deleteById (id);
        log.info ("Deleted country with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        return countryRepository.existsById (id);
    }

    @Override
    public long countCountries () {
        return countryRepository.count ();
    }
}


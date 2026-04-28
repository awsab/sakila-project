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
import com.me.learning.parent.customerservice.dto.CityRequest;
import com.me.learning.parent.customerservice.dto.CityResponse;
import com.me.learning.parent.customerservice.dto.CityUpdateRequest;
import com.me.learning.parent.customerservice.entity.City;
import com.me.learning.parent.customerservice.mapper.CityMapper;
import com.me.learning.parent.customerservice.repository.CityRepository;
import com.me.learning.parent.customerservice.repository.CountryRepository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service implementation for City entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class CityServiceImpl implements CityService {

    private static final String CITY_RESOURCE = "City";
    private static final String COUNTRY_RESOURCE = "Country";
    private static final String FIELD_ID = "id";

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final CityMapper cityMapper;

    @Override
    @Transactional
    @CacheEvict (value = {"cities", "city"}, allEntries = true)
    public CityResponse createCity (CityRequest request) {
        log.debug ("Creating new city: {} in country ID: {}", request.city (), request.countryId ());

        if ( !countryRepository.existsById (request.countryId ()) ) {
            throw new ResourceNotFoundException (COUNTRY_RESOURCE, FIELD_ID, request.countryId ());
        }

        if ( cityRepository.existsByCityIgnoreCaseAndCountryId (request.city (), request.countryId ()) ) {
            throw new DuplicateResourceException (CITY_RESOURCE, "city+countryId",
                    request.city () + "/" + request.countryId ());
        }

        City city = cityMapper.toEntity (request);
        City saved = cityRepository.save (city);

        log.info ("Created city with ID: {}", saved.getId ());
        return cityMapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"cities", "city"}, allEntries = true)
    public CityResponse updateCity (Integer id, CityUpdateRequest request) {
        log.debug ("Updating city with ID: {}", id);

        City existing = cityRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CITY_RESOURCE, FIELD_ID, id));

        if ( !countryRepository.existsById (request.countryId ()) ) {
            throw new ResourceNotFoundException (COUNTRY_RESOURCE, FIELD_ID, request.countryId ());
        }

        existing.setCity (request.city ());
        existing.getCountry ().setId (request.countryId ());
        City updated = cityRepository.save (existing);

        log.info ("Updated city with ID: {}", id);
        return cityMapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"cities", "city"}, allEntries = true)
    public CityResponse patchCity (Integer id, CityUpdateRequest request) {
        log.debug ("Patching city with ID: {}", id);

        City existing = cityRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CITY_RESOURCE, FIELD_ID, id));

        cityMapper.updateEntity (request, existing);
        City updated = cityRepository.save (existing);

        log.info ("Patched city with ID: {}", id);
        return cityMapper.toResponse (updated);
    }

    @Override
    public CityResponse getCityById (Integer id) {
        log.debug ("Fetching city with ID: {}", id);

        City city = cityRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (CITY_RESOURCE, FIELD_ID, id));

        return cityMapper.toResponse (city);
    }

    @Override
    public List<CityResponse> getAllCities () {
        log.debug ("Fetching all cities");
        return cityMapper.toResponseList (cityRepository.findAll ());
    }

    @Override
    public Page<CityResponse> getAllCities (Pageable pageable) {
        log.debug ("Fetching cities â€” page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return cityRepository.findAll (pageable).map (cityMapper::toResponse);
    }

    @Override
    public List<CityResponse> getCitiesByCountryId (Integer countryId) {
        log.debug ("Fetching cities for country ID: {}", countryId);
        return cityMapper.toResponseList (cityRepository.findByCountryId (countryId));
    }

    @Override
    @Transactional
    @CacheEvict (value = {"cities", "city"}, allEntries = true)
    public void deleteCity (Integer id) {
        log.debug ("Deleting city with ID: {}", id);

        if ( !cityRepository.existsById (id) ) {
            throw new ResourceNotFoundException (CITY_RESOURCE, FIELD_ID, id);
        }

        cityRepository.deleteById (id);
        log.info ("Deleted city with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        return cityRepository.existsById (id);
    }

    @Override
    public long countCities () {
        return cityRepository.count ();
    }
}


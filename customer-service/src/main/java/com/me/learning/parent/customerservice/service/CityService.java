package com.me.learning.parent.customerservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.customerservice.dto.CityRequest;
import com.me.learning.parent.customerservice.dto.CityResponse;
import com.me.learning.parent.customerservice.dto.CityUpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service interface for City entity operations
 * Since    : Version 1.0
 */
public interface CityService {

    /** Create a new city. */
    CityResponse createCity (CityRequest request);

    /** Fully update an existing city. */
    CityResponse updateCity (Integer id, CityUpdateRequest request);

    /** Partially update an existing city. */
    CityResponse patchCity (Integer id, CityUpdateRequest request);

    /** Retrieve a city by its ID. */
    CityResponse getCityById (Integer id);

    /** Retrieve all cities. */
    List<CityResponse> getAllCities ();

    /** Retrieve all cities with pagination. */
    Page<CityResponse> getAllCities (Pageable pageable);

    /** Retrieve all cities belonging to a country. */
    List<CityResponse> getCitiesByCountryId (Integer countryId);

    /** Delete a city by ID. */
    void deleteCity (Integer id);

    /** Check whether a city exists by ID. */
    boolean existsById (Integer id);

    /** Count total cities. */
    long countCities ();
}


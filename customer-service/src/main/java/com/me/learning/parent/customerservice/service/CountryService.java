package com.me.learning.parent.customerservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.customerservice.dto.CountryRequest;
import com.me.learning.parent.customerservice.dto.CountryResponse;
import com.me.learning.parent.customerservice.dto.CountryUpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service interface for Country entity operations
 * Since    : Version 1.0
 */
public interface CountryService {

    /** Create a new country. */
    CountryResponse createCountry (CountryRequest request);

    /** Fully update an existing country. */
    CountryResponse updateCountry (Integer id, CountryUpdateRequest request);

    /** Partially update an existing country. */
    CountryResponse patchCountry (Integer id, CountryUpdateRequest request);

    /** Retrieve a country by its ID. */
    CountryResponse getCountryById (Integer id);

    /** Retrieve all countries. */
    List<CountryResponse> getAllCountries ();

    /** Retrieve all countries with pagination. */
    Page<CountryResponse> getAllCountries (Pageable pageable);

    /** Delete a country by ID. */
    void deleteCountry (Integer id);

    /** Check whether a country exists by ID. */
    boolean existsById (Integer id);

    /** Count total countries. */
    long countCountries ();
}


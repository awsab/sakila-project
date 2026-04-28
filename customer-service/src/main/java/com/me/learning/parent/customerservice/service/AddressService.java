package com.me.learning.parent.customerservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.customerservice.dto.AddressRequest;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.AddressUpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Service interface for Address entity operations
 * Since    : Version 1.0
 */
public interface AddressService {

    /** Create a new address. */
    AddressResponse createAddress (AddressRequest request);

    /** Fully update an existing address. */
    AddressResponse updateAddress (Integer id, AddressUpdateRequest request);

    /** Partially update an existing address. */
    AddressResponse patchAddress (Integer id, AddressUpdateRequest request);

    /** Retrieve an address by its ID. */
    AddressResponse getAddressById (Integer id);

    /** Retrieve all addresses. */
    List<AddressResponse> getAllAddresses ();

    /** Retrieve all addresses with pagination. */
    Page<AddressResponse> getAllAddresses (Pageable pageable);

    /** Retrieve all addresses belonging to a city. */
    List<AddressResponse> getAddressesByCityId (Integer cityId);

    /** Delete an address by ID. */
    void deleteAddress (Integer id);

    /** Check whether an address exists by ID. */
    boolean existsById (Integer id);

    /** Count total addresses. */
    long countAddresses ();
}


package com.me.learning.parent.paymentservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.paymentservice.dto.RentalRequest;
import com.me.learning.parent.paymentservice.dto.RentalResponse;
import com.me.learning.parent.paymentservice.dto.RentalUpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Service interface for Rental entity operations
 * Since    : Version 1.0
 */
public interface RentalService {

    /** Create a new rental. */
    RentalResponse createRental (RentalRequest request);

    /** Fully update an existing rental. */
    RentalResponse updateRental (Integer id, RentalUpdateRequest request);

    /** Partially update an existing rental. */
    RentalResponse patchRental (Integer id, RentalUpdateRequest request);

    /** Retrieve a rental by its ID. */
    RentalResponse getRentalById (Integer id);

    /** Retrieve all rentals. */
    List<RentalResponse> getAllRentals ();

    /** Retrieve all rentals with pagination. */
    Page<RentalResponse> getAllRentals (Pageable pageable);

    /** Delete a rental by ID. */
    void deleteRental (Integer id);

    /** Check whether a rental exists by ID. */
    boolean existsById (Integer id);

    /** Count total rentals. */
    long countRentals ();
}

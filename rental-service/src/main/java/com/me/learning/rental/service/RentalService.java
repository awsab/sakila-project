package com.me.learning.rental.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.rental.dto.RentalCheckoutRequestDto;
import com.me.learning.rental.dto.RentalCheckoutResponseDto;
import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.dto.RentalResponseDto;

/**
 * Service interface for {@link com.me.learning.rental.entity.Rental} CRUD operations.
 *
 * <p>All read operations are executed in a read-only transaction.
 * Write operations ({@code create}, {@code update}, {@code partialUpdate}, {@code delete})
 * participate in a full read-write transaction.
 *
 * <p>Every method that returns rental data produces a {@link RentalResponseDto} —
 * a flat representation of the rental's scalar fields.
 * {@link RentalRequestDto} is used exclusively as the inbound write payload.
 * </p>
 */
public interface RentalService {

    /**
     * Execute the Sakila checkout use case in a single transaction: create the
     * rental row and then insert the linked payment row.
     *
     * @param dto composite rental + payment payload
     * @return the created rental and its linked payment
     * @throws com.me.learning.framework.web.errors.BadRequestException if the payment customer/staff
     *         does not match the rental customer/staff or either section is missing
     */
    RentalCheckoutResponseDto checkout (RentalCheckoutRequestDto dto);

    /**
     * Create a new rental.
     *
     * @param dto the rental data; {@code dto.id()} is ignored — the ID is DB-generated
     * @return the persisted rental as a flat {@link RentalResponseDto}
     */
    RentalResponseDto create (RentalRequestDto dto);

    /**
     * Fully replace all fields of an existing rental.
     *
     * @param id  path-variable rental ID
     * @param dto new field values; all non-ID fields are overwritten
     * @return the updated rental as a flat {@link RentalResponseDto}
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if the rental does not exist
     */
    RentalResponseDto update (Integer id, RentalRequestDto dto);

    /**
     * Partially update an existing rental — only non-{@code null} DTO fields are applied.
     *
     * @param id  path-variable rental ID
     * @param dto partial field values; {@code null} fields are left unchanged
     * @return the updated rental as a flat {@link RentalResponseDto}
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if the rental does not exist
     */
    RentalResponseDto partialUpdate (Integer id, RentalRequestDto dto);

    /**
     * Retrieve a single rental by its primary key.
     *
     * @param id rental primary key
     * @return the rental as a flat {@link RentalResponseDto}
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if not found
     */
    RentalResponseDto findById (Integer id);

    /**
     * Retrieve all rentals as an unpaged list.
     *
     * @return list of all rentals as flat {@link RentalResponseDto}s
     */
    List<RentalResponseDto> findAll ();

    /**
     * Retrieve a page of rentals.
     *
     * @param pageable pagination and sort parameters
     * @return a page of rentals as flat {@link RentalResponseDto}s
     */
    Page<RentalResponseDto> findAll (Pageable pageable);

    /**
     * Delete a rental by ID.
     *
     * @param id rental primary key
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if not found
     */
    void delete (Integer id);

    /**
     * Check whether a rental with the given ID exists.
     *
     * @param id rental primary key
     * @return {@code true} if a rental with {@code id} exists
     */
    boolean existsById (Integer id);

    /**
     * Count the total number of rentals.
     *
     * @return total count
     */
    long count ();
}


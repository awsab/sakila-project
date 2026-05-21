package com.me.learning.rental.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.PaymentResponseDto;

/**
 * Service interface for {@link com.me.learning.rental.entity.Payment} CRUD operations.
 *
 * <p>All read operations are executed in a read-only transaction.
 * Write operations ({@code create}, {@code update}, {@code partialUpdate}, {@code delete})
 * participate in a full read-write transaction.
 *
 * <p>Every method that returns payment data produces a {@link PaymentResponseDto} —
 * a flat representation that includes only the ID of the related rental
 * ({@code rentalId}). {@link PaymentRequestDto} is used exclusively as the
 * inbound write payload.
 * </p>
 */
public interface PaymentService {

    /**
     * Create a new payment.
     *
     * @param dto the payment data; {@code dto.id()} is ignored — the ID is DB-generated
     * @return the persisted payment as a flat {@link PaymentResponseDto}
     */
    PaymentResponseDto create (PaymentRequestDto dto);

    /**
     * Fully replace all fields of an existing payment.
     *
     * @param id  path-variable payment ID
     * @param dto new field values; all non-ID fields are overwritten
     * @return the updated payment as a flat {@link PaymentResponseDto}
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if the payment or
     *         the supplied rental does not exist
     */
    PaymentResponseDto update (Integer id, PaymentRequestDto dto);

    /**
     * Partially update an existing payment — only non-{@code null} DTO fields are applied.
     *
     * @param id  path-variable payment ID
     * @param dto partial field values; {@code null} fields are left unchanged
     * @return the updated payment as a flat {@link PaymentResponseDto}
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if the payment or
     *         the supplied rental does not exist
     */
    PaymentResponseDto partialUpdate (Integer id, PaymentRequestDto dto);

    /**
     * Retrieve a single payment by its primary key.
     *
     * @param id payment primary key
     * @return the payment as a flat {@link PaymentResponseDto}
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if not found
     */
    PaymentResponseDto findById (Integer id);

    /**
     * Retrieve all payments as an unpaged list.
     *
     * @return list of all payments as flat {@link PaymentResponseDto}s
     */
    List<PaymentResponseDto> findAll ();

    /**
     * Retrieve a page of payments.
     *
     * @param pageable pagination and sort parameters
     * @return a page of payments as flat {@link PaymentResponseDto}s
     */
    Page<PaymentResponseDto> findAll (Pageable pageable);

    /**
     * Delete a payment by ID.
     *
     * @param id payment primary key
     * @throws com.me.learning.framework.web.errors.ResourceNotFoundException if not found
     */
    void delete (Integer id);

    /**
     * Check whether a payment with the given ID exists.
     *
     * @param id payment primary key
     * @return {@code true} if a payment with {@code id} exists
     */
    boolean existsById (Integer id);

    /**
     * Count the total number of payments.
     *
     * @return total count
     */
    long count ();
}


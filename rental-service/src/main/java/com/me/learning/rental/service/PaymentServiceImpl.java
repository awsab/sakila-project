package com.me.learning.rental.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.PaymentResponseDto;
import com.me.learning.rental.entity.Payment;
import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.mapper.PaymentMapper;
import com.me.learning.rental.repository.PaymentRepository;
import com.me.learning.rental.repository.RentalRepository;

/**
 * Default implementation of {@link PaymentService}.
 *
 * <h3>Transaction strategy</h3>
 * <ul>
 *   <li>Class-level {@code @Transactional(readOnly = true)} covers all read paths.</li>
 *   <li>Write methods override with {@code @Transactional} (read-write).</li>
 * </ul>
 *
 * <h3>Rental association</h3>
 * {@link PaymentRequestDto#rental()} may optionally carry the ID of an existing
 * {@link Rental}. On create/update the rental is loaded from
 * {@link RentalRepository}, ensuring we set a JPA-managed entity reference
 * rather than a detached one created by MapStruct.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private static final String RESOURCE_PAYMENT = "Payment";
    private static final String RESOURCE_RENTAL = "Rental";
    private static final String FIELD_ID = "id";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;

    // ── Write operations ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public PaymentResponseDto create (PaymentRequestDto dto) {
        log.debug ("Creating new payment for customerId: {}, amount: {}",
                dto.customerId (), dto.amount ());

        Payment payment = paymentMapper.toEntity (dto);
        payment.setId (null); // ID is always DB-generated; ignore any value in the DTO
        payment.setRental (resolveRentalReference (dto));

        Payment saved = paymentRepository.save (payment);
        log.info ("Created payment with ID: {}", saved.getId ());

        return paymentMapper.toResponseDto (paymentRepository.findById (saved.getId ()).orElseThrow ());
    }

    @Override
    @Transactional
    public PaymentResponseDto update (Integer id, PaymentRequestDto dto) {
        log.debug ("Fully updating payment with ID: {}", id);

        Payment existing = paymentRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_PAYMENT, FIELD_ID, id));

        existing.setCustomerId (dto.customerId ());
        existing.setStaffId (dto.staffId ());
        existing.setAmount (dto.amount ());
        existing.setPaymentDate (dto.paymentDate ());
        existing.setLastUpdate (dto.lastUpdate ());
        existing.setRental (resolveRentalReference (dto));

        paymentRepository.save (existing);
        log.info ("Updated payment with ID: {}", id);

        return paymentMapper.toResponseDto (paymentRepository.findById (id).orElseThrow ());
    }

    @Override
    @Transactional
    public PaymentResponseDto partialUpdate (Integer id, PaymentRequestDto dto) {
        log.debug ("Partially updating payment with ID: {}", id);

        Payment existing = paymentRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_PAYMENT, FIELD_ID, id));

        // Apply only non-null scalar fields via the MapStruct partial-update method.
        paymentMapper.partialUpdate (dto, existing);

        // Replace any detached Rental set by MapStruct with a managed entity.
        if (dto.rental () != null) {
            existing.setRental (resolveRentalReference (dto));
        }

        paymentRepository.save (existing);
        log.info ("Patched payment with ID: {}", id);

        return paymentMapper.toResponseDto (paymentRepository.findById (id).orElseThrow ());
    }

    @Override
    @Transactional
    public void delete (Integer id) {
        log.debug ("Deleting payment with ID: {}", id);

        if (!paymentRepository.existsById (id)) {
            throw new ResourceNotFoundException (RESOURCE_PAYMENT, FIELD_ID, id);
        }

        paymentRepository.deleteById (id);
        log.info ("Deleted payment with ID: {}", id);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    @Override
    public PaymentResponseDto findById (Integer id) {
        log.debug ("Fetching payment with ID: {}", id);

        return paymentRepository.findById (id)
                .map (paymentMapper::toResponseDto)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_PAYMENT, FIELD_ID, id));
    }

    @Override
    public List<PaymentResponseDto> findAll () {
        log.debug ("Fetching all payments");
        return paymentRepository.findAll ()
                .stream ()
                .map (paymentMapper::toResponseDto)
                .toList ();
    }

    @Override
    public Page<PaymentResponseDto> findAll (Pageable pageable) {
        log.debug ("Fetching payments — page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return paymentRepository.findAll (pageable).map (paymentMapper::toResponseDto);
    }

    @Override
    public boolean existsById (Integer id) {
        return paymentRepository.existsById (id);
    }

    @Override
    public long count () {
        return paymentRepository.count ();
    }

    private Rental resolveRentalReference (PaymentRequestDto dto) {
        if (dto.rental () == null) {
            return null;
        }
        if (dto.rental ().id () == null) {
            return null;
        }
        return rentalRepository.findById (dto.rental ().id ())
                .orElseThrow (() -> new ResourceNotFoundException (
                        RESOURCE_RENTAL, FIELD_ID, dto.rental ().id ()));
    }
}


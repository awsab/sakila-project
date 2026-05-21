package com.me.learning.rental.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.BadRequestException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.RentalCheckoutRequestDto;
import com.me.learning.rental.dto.RentalCheckoutResponseDto;
import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.dto.RentalResponseDto;
import com.me.learning.rental.entity.Payment;
import com.me.learning.rental.entity.Rental;
import com.me.learning.rental.mapper.PaymentMapper;
import com.me.learning.rental.mapper.RentalMapper;
import com.me.learning.rental.repository.PaymentRepository;
import com.me.learning.rental.repository.RentalRepository;

/**
 * Default implementation of {@link RentalService}.
 *
 * <h3>Transaction strategy</h3>
 * <ul>
 *   <li>Class-level {@code @Transactional(readOnly = true)} covers all read paths.</li>
 *   <li>Write methods override with {@code @Transactional} (read-write).</li>
 * </ul>
 *
 * <h3>No FK resolution required</h3>
 * {@link Rental} holds all cross-service references as plain ID columns
 * ({@code inventoryId}, {@code customerId}, {@code staffId}) — there are no
 * intra-service {@code @ManyToOne} associations to resolve, so no secondary
 * repository lookup is needed on create or update.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class RentalServiceImpl implements RentalService {

    private static final String RESOURCE_RENTAL = "Rental";
    private static final String RESOURCE_PAYMENT = "Payment";
    private static final String FIELD_ID = "id";

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final RentalMapper rentalMapper;
    private final PaymentMapper paymentMapper;

    // ── Write operations ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public RentalCheckoutResponseDto checkout (RentalCheckoutRequestDto dto) {
        validateCheckoutRequest (dto);

        log.debug ("Executing checkout for customerId: {}, inventoryId: {}",
                dto.rental ().customerId (), dto.rental ().inventoryId ());


        Rental rental = rentalMapper.toEntity (dto.rental ());
        rental.setId (null);
        Rental savedRental = rentalRepository.save (rental);

        Payment payment = paymentMapper.toEntity (dto.payment ());
        payment.setId (null);
        payment.setRental (savedRental);
        Payment savedPayment = paymentRepository.save (payment);

        log.info ("Checkout completed with rental ID: {} and payment ID: {}",
                savedRental.getId (), savedPayment.getId ());

        return new RentalCheckoutResponseDto (
                rentalMapper.toResponseDto (rentalRepository.findById (savedRental.getId ()).orElseThrow ()),
                paymentMapper.toResponseDto (paymentRepository.findById (savedPayment.getId ()).orElseThrow ())
        );
    }

    @Override
    @Transactional
    public RentalResponseDto create (RentalRequestDto dto) {
        log.debug ("Creating new rental for customerId: {}, inventoryId: {}",
                dto.customerId (), dto.inventoryId ());

        Rental rental = rentalMapper.toEntity (dto);
        rental.setId (null); // ID is always DB-generated; ignore any value in the DTO

        Rental saved = rentalRepository.save (rental);
        log.info ("Created rental with ID: {}", saved.getId ());

        return rentalMapper.toResponseDto (rentalRepository.findById (saved.getId ()).orElseThrow ());
    }

    @Override
    @Transactional
    public RentalResponseDto update (Integer id, RentalRequestDto dto) {
        log.debug ("Fully updating rental with ID: {}", id);

        Rental existing = rentalRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_RENTAL, FIELD_ID, id));

        existing.setRentalDate (dto.rentalDate ());
        existing.setInventoryId (dto.inventoryId ());
        existing.setCustomerId (dto.customerId ());
        existing.setReturnDate (dto.returnDate ());
        existing.setStaffId (dto.staffId ());
        existing.setLastUpdate (dto.lastUpdate ());

        rentalRepository.save (existing);
        log.info ("Updated rental with ID: {}", id);

        return rentalMapper.toResponseDto (rentalRepository.findById (id).orElseThrow ());
    }

    @Override
    @Transactional
    public RentalResponseDto partialUpdate (Integer id, RentalRequestDto dto) {
        log.debug ("Partially updating rental with ID: {}", id);

        Rental existing = rentalRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_RENTAL, FIELD_ID, id));

        // Apply only non-null scalar fields via the MapStruct partial-update method.
        rentalMapper.partialUpdate (dto, existing);

        rentalRepository.save (existing);
        log.info ("Patched rental with ID: {}", id);

        return rentalMapper.toResponseDto (rentalRepository.findById (id).orElseThrow ());
    }

    @Override
    @Transactional
    public void delete (Integer id) {
        log.debug ("Deleting rental with ID: {}", id);

        if (!rentalRepository.existsById (id)) {
            throw new ResourceNotFoundException (RESOURCE_RENTAL, FIELD_ID, id);
        }

        rentalRepository.deleteById (id);
        log.info ("Deleted rental with ID: {}", id);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    @Override
    public RentalResponseDto findById (Integer id) {
        log.debug ("Fetching rental with ID: {}", id);

        return rentalRepository.findById (id)
                .map (rentalMapper::toResponseDto)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_RENTAL, FIELD_ID, id));
    }

    @Override
    public List<RentalResponseDto> findAll () {
        log.debug ("Fetching all rentals");
        return rentalRepository.findAll ()
                .stream ()
                .map (rentalMapper::toResponseDto)
                .toList ();
    }

    @Override
    public Page<RentalResponseDto> findAll (Pageable pageable) {
        log.debug ("Fetching rentals — page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return rentalRepository.findAll (pageable).map (rentalMapper::toResponseDto);
    }

    @Override
    public boolean existsById (Integer id) {
        return rentalRepository.existsById (id);
    }

    @Override
    public long count () {
        return rentalRepository.count ();
    }

    private void validateCheckoutRequest (RentalCheckoutRequestDto dto) {
        if (dto.rental () == null) {
            throw new BadRequestException (
                    "Rental details are required for checkout", RESOURCE_RENTAL, "rental.missing");
        }
        if (dto.payment () == null) {
            throw new BadRequestException (
                    "Payment details are required for checkout", RESOURCE_PAYMENT, "payment.missing");
        }

        PaymentRequestDto payment = dto.payment ();
        RentalRequestDto rental = dto.rental ();

        if (!Objects.equals (payment.customerId (), rental.customerId ())) {
            throw new BadRequestException (
                    "Payment customerId must match rental customerId",
                    RESOURCE_PAYMENT,
                    "payment.customer.mismatch"
            );
        }

        if (!Objects.equals (payment.staffId (), rental.staffId ())) {
            throw new BadRequestException (
                    "Payment staffId must match rental staffId",
                    RESOURCE_PAYMENT,
                    "payment.staff.mismatch"
            );
        }
    }
}


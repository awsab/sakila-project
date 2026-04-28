package com.me.learning.parent.paymentservice.service;

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
import com.me.learning.parent.paymentservice.dto.PaymentRequest;
import com.me.learning.parent.paymentservice.dto.PaymentResponse;
import com.me.learning.parent.paymentservice.dto.PaymentUpdateRequest;
import com.me.learning.parent.paymentservice.entity.Payment;
import com.me.learning.parent.paymentservice.mapper.PaymentMapper;
import com.me.learning.parent.paymentservice.repository.PaymentRepository;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Service implementation for Payment entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private static final String RESOURCE   = "Payment";
    private static final String FIELD_ID   = "id";
    private static final String FIELD_NAME = "name";

    private final PaymentRepository repository;
    private final PaymentMapper mapper;

    @Override
    @Transactional
    @CacheEvict (value = {"payments", "payment"}, allEntries = true)
    public PaymentResponse createPayment (PaymentRequest request) {
        log.debug ("Creating payment: {}", request.name ());

        if (repository.existsByNameIgnoreCase (request.name ())) {
            throw new DuplicateResourceException (RESOURCE, FIELD_NAME, request.name ());
        }
        Payment entity = mapper.toEntity (request);
        Payment saved  = repository.save (entity);

        log.info ("Created payment with ID: {}", saved.getId ());
        return mapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"payments", "payment"}, allEntries = true)
    public PaymentResponse updatePayment (Integer id, PaymentUpdateRequest request) {
        log.debug ("Updating payment with ID: {}", id);

        Payment existing = repository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id));

        // TODO: map fields from request to existing entity
        // existing.setName (request.name ());

        Payment updated = repository.save (existing);
        log.info ("Updated payment with ID: {}", id);
        return mapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"payments", "payment"}, allEntries = true)
    public PaymentResponse patchPayment (Integer id, PaymentUpdateRequest request) {
        log.debug ("Patching payment with ID: {}", id);

        Payment existing = repository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id));

        mapper.updateEntity (request, existing);
        return mapper.toResponse (repository.save (existing));
    }

    @Override
    public PaymentResponse getPaymentById (Integer id) {
        log.debug ("Fetching payment with ID: {}", id);
        return mapper.toResponse (
                repository.findById (id)
                        .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id)));
    }

    @Override
    public List<PaymentResponse> getAllPayments () {
        log.debug ("Fetching all payments");
        return mapper.toResponseList (repository.findAll ());
    }

    @Override
    public Page<PaymentResponse> getAllPayments (Pageable pageable) {
        log.debug ("Fetching payments - page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return repository.findAll (pageable).map (mapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"payments", "payment"}, allEntries = true)
    public void deletePayment (Integer id) {
        log.debug ("Deleting payment with ID: {}", id);

        if ( !repository.existsById (id) ) {
            throw new ResourceNotFoundException (RESOURCE, FIELD_ID, id);
        }

        repository.deleteById (id);
        log.info ("Deleted payment with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        return repository.existsById (id);
    }

    @Override
    public long countPayments () {
        return repository.count ();
    }
}

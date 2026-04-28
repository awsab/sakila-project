package com.me.learning.parent.paymentservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.parent.paymentservice.dto.PaymentRequest;
import com.me.learning.parent.paymentservice.dto.PaymentResponse;
import com.me.learning.parent.paymentservice.dto.PaymentUpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : Service interface for Payment entity operations
 * Since    : Version 1.0
 */
public interface PaymentService {

    /** Create a new payment. */
    PaymentResponse createPayment (PaymentRequest request);

    /** Fully update an existing payment. */
    PaymentResponse updatePayment (Integer id, PaymentUpdateRequest request);

    /** Partially update an existing payment. */
    PaymentResponse patchPayment (Integer id, PaymentUpdateRequest request);

    /** Retrieve a payment by its ID. */
    PaymentResponse getPaymentById (Integer id);

    /** Retrieve all payments. */
    List<PaymentResponse> getAllPayments ();

    /** Retrieve all payments with pagination. */
    Page<PaymentResponse> getAllPayments (Pageable pageable);

    /** Delete a payment by ID. */
    void deletePayment (Integer id);

    /** Check whether a payment exists by ID. */
    boolean existsById (Integer id);

    /** Count total payments. */
    long countPayments ();
}

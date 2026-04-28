package com.me.learning.parent.paymentservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.paymentservice.entity.Payment;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : JPA Repository for Payment entity
 * Since    : Version 1.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByNameIgnoreCase (String name);

    boolean existsByNameIgnoreCase (String name);
    // TODO: add domain-specific query methods
}

package com.me.learning.rental.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.me.learning.rental.entity.Payment;

/**
 * Repository for {@link Payment}.
 *
 * <p>The {@link EntityGraph} on {@code findAll()} and {@code findById()} eagerly fetches
 * the {@code rental} association in a single JOIN query, preventing the N+1 problem
 * that would otherwise arise because the {@code @ManyToOne} on {@link Payment}
 * is mapped as {@code FetchType.LAZY} while
 * {@link com.me.learning.rental.dto.PaymentRequestDto} embeds
 * {@link com.me.learning.rental.dto.RentalRequestDto}.
 * </p>
 */
public interface PaymentRepository extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Payment> {

    @Override
    @NonNull
    @EntityGraph (attributePaths = {"rental"})
    List<Payment> findAll ();

    @Override
    @NonNull
    @EntityGraph (attributePaths = {"rental"})
    Page<Payment> findAll (@NonNull Pageable pageable);

    @Override
    @NonNull
    @EntityGraph (attributePaths = {"rental"})
    Optional<Payment> findById (@NonNull Integer id);
}


package com.me.learning.rental.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.me.learning.rental.entity.Rental;

/**
 * Repository for {@link Rental}.
 *
 * <p>{@link Rental} contains only scalar fields and cross-service ID references
 * — there are no intra-service {@code @ManyToOne} associations to eager-fetch,
 * so no {@code @EntityGraph} override is required. The standard {@link JpaRepository}
 * methods are sufficient.
 */
public interface RentalRepository extends JpaRepository<Rental, Integer>, JpaSpecificationExecutor<Rental> {

    @Override
    @NonNull
    List<Rental> findAll ();

    @Override
    @NonNull
    Page<Rental> findAll (@NonNull Pageable pageable);

    @Override
    @NonNull
    Optional<Rental> findById (@NonNull Integer id);
}


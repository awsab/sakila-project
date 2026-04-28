package com.me.learning.parent.customerservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.customerservice.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmailIgnoreCase(String email);

    @Query("""
            select distinct c
            from Customer c
            left join fetch c.address a
            left join fetch a.city
            left join fetch c.payments p
            left join fetch p.staff
            where c.id = :id
            """)
    Optional<Customer> findByIdWithDetails(@Param ("id") Integer id);

    List<Customer> findByStoreId(Short storeId);

    List<Customer> findByLastNameIgnoreCaseContaining(String lastName);

    List<Customer> findByActiveTrue();

    boolean existsByEmailIgnoreCase(String email);
}


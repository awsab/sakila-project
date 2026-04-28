package com.me.learning.parent.customerservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.customerservice.entity.Country;


@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    Optional<Country> findByCountryIgnoreCase(String country);

    boolean existsByCountryIgnoreCase(String country);
}


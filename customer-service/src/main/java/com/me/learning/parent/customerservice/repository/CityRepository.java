package com.me.learning.parent.customerservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.customerservice.entity.City;


@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    List<City> findByCountryId(Integer countryId);

    Optional<City> findByCityIgnoreCaseAndCountryId(String city, Integer countryId);

    boolean existsByCityIgnoreCaseAndCountryId(String city, Integer countryId);
}


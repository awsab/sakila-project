package com.me.learning.parent.customerservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.customerservice.entity.Address;


@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCityId(Integer cityId);

    List<Address> findByPostalCode(String postalCode);

    List<Address> findByPhoneContaining(String phone);
}


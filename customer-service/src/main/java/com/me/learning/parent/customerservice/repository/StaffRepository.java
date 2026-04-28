package com.me.learning.parent.customerservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.me.learning.parent.customerservice.entity.Staff;


@Repository
public interface StaffRepository extends JpaRepository<Staff, Short> {

    Optional<Staff> findByUsernameIgnoreCase(String username);

    Optional<Staff> findByEmailIgnoreCase(String email);

    List<Staff> findByStoreIdId(Short storeId);

    List<Staff> findByActiveTrue();

    boolean existsByUsernameIgnoreCase(String username);
}


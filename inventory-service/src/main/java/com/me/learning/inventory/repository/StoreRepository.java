package com.me.learning.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.me.learning.inventory.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Short> {
}


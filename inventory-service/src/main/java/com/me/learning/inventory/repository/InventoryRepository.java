package com.me.learning.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.me.learning.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"store"})
    List<Inventory> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"store"})
    Page<Inventory> findAll(@NonNull Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"store"})
    Optional<Inventory> findById(@NonNull Integer id);
}


package com.me.learning.inventory.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.inventory.dto.InventoryRequestDto;
import com.me.learning.inventory.dto.InventoryResponseDto;

public interface InventoryService {

    InventoryResponseDto create(InventoryRequestDto dto);

    InventoryResponseDto update(Integer id, InventoryRequestDto dto);

    InventoryResponseDto partialUpdate(Integer id, InventoryRequestDto dto);

    InventoryResponseDto findById(Integer id);

    List<InventoryResponseDto> findAll();

    Page<InventoryResponseDto> findAll(Pageable pageable);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();
}


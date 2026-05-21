package com.me.learning.inventory.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.inventory.dto.InventoryRequestDto;
import com.me.learning.inventory.dto.InventoryResponseDto;
import com.me.learning.inventory.entity.Inventory;
import com.me.learning.inventory.entity.Store;
import com.me.learning.inventory.mapper.InventoryMapper;
import com.me.learning.inventory.repository.InventoryRepository;
import com.me.learning.inventory.repository.StoreRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private static final String RESOURCE_INVENTORY = "Inventory";
    private static final String RESOURCE_STORE = "Store";
    private static final String FIELD_ID = "id";

    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public InventoryResponseDto create(InventoryRequestDto dto) {
        log.debug("Creating new inventory for film {}", dto.filmId());

        Inventory inventory = inventoryMapper.toEntity(dto);
        inventory.setId(null);
        inventory.setStore(loadStore(dto.store().id()));
        if (inventory.getLastUpdate() == null) {
            inventory.setLastUpdate(Instant.now());
        }

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Created inventory with ID: {}", saved.getId());

        return inventoryMapper.toResponseDto(inventoryRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public InventoryResponseDto update(Integer id, InventoryRequestDto dto) {
        log.debug("Fully updating inventory with ID: {}", id);

        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_INVENTORY, FIELD_ID, id));

        existing.setFilmId(dto.filmId());

        Short newStoreId = dto.store().id();
        if (!newStoreId.equals(existing.getStore().getId())) {
            existing.setStore(loadStore(newStoreId));
        }

        existing.setLastUpdate(dto.lastUpdate() != null ? dto.lastUpdate() : Instant.now());

        inventoryRepository.save(existing);
        log.info("Updated inventory with ID: {}", id);

        return inventoryMapper.toResponseDto(inventoryRepository.findById(id).orElseThrow());
    }

    @Override
    @Transactional
    public InventoryResponseDto partialUpdate(Integer id, InventoryRequestDto dto) {
        log.debug("Partially updating inventory with ID: {}", id);

        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_INVENTORY, FIELD_ID, id));

        inventoryMapper.partialUpdate(dto, existing);

        if (dto.store() != null && dto.store().id() != null) {
            existing.setStore(loadStore(dto.store().id()));
        }
        if (existing.getLastUpdate() == null) {
            existing.setLastUpdate(Instant.now());
        }

        inventoryRepository.save(existing);
        log.info("Patched inventory with ID: {}", id);

        return inventoryMapper.toResponseDto(inventoryRepository.findById(id).orElseThrow());
    }

    @Override
    public InventoryResponseDto findById(Integer id) {
        log.debug("Fetching inventory with ID: {}", id);

        return inventoryRepository.findById(id)
                .map(inventoryMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_INVENTORY, FIELD_ID, id));
    }

    @Override
    public List<InventoryResponseDto> findAll() {
        log.debug("Fetching all inventories");
        return inventoryRepository.findAll().stream().map(inventoryMapper::toResponseDto).toList();
    }

    @Override
    public Page<InventoryResponseDto> findAll(Pageable pageable) {
        log.debug("Fetching inventories - page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return inventoryRepository.findAll(pageable).map(inventoryMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.debug("Deleting inventory with ID: {}", id);

        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURCE_INVENTORY, FIELD_ID, id);
        }

        inventoryRepository.deleteById(id);
        log.info("Deleted inventory with ID: {}", id);
    }

    @Override
    public boolean existsById(Integer id) {
        return inventoryRepository.existsById(id);
    }

    @Override
    public long count() {
        return inventoryRepository.count();
    }

    private Store loadStore(Short storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_STORE, FIELD_ID, storeId));
    }
}


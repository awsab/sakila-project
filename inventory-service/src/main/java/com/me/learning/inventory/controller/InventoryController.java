package com.me.learning.inventory.controller;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.inventory.dto.InventoryRequestDto;
import com.me.learning.inventory.dto.InventoryResponseDto;
import com.me.learning.inventory.service.InventoryService;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @Operation(summary = "Create a new inventory item")
    @ApiResponse(responseCode = "201", description = "Inventory created successfully",
            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Referenced store does not exist")
    public ResponseEntity<InventoryResponseDto> create(@Valid @RequestBody InventoryRequestDto dto) {
        log.info("REST POST /api/v1/inventories - creating inventory for film {}", dto.filmId());
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Fully update an inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory updated successfully",
            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Inventory or referenced store not found")
    public ResponseEntity<InventoryResponseDto> update(
            @Parameter(description = "Inventory primary key") @PathVariable Integer id,
            @Valid @RequestBody InventoryRequestDto dto) {
        log.info("REST PUT /api/v1/inventories/{} - full update", id);
        return ResponseEntity.ok(inventoryService.update(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an inventory item")
    @ApiResponse(responseCode = "200", description = "Inventory patched successfully",
            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Inventory or referenced store not found")
    public ResponseEntity<InventoryResponseDto> partialUpdate(
            @Parameter(description = "Inventory primary key") @PathVariable Integer id,
            @RequestBody InventoryRequestDto dto) {
        log.info("REST PATCH /api/v1/inventories/{} - partial update", id);
        return ResponseEntity.ok(inventoryService.partialUpdate(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an inventory item by ID")
    @ApiResponse(responseCode = "200", description = "Inventory found",
            content = @Content(schema = @Schema(implementation = InventoryResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Inventory not found")
    public ResponseEntity<InventoryResponseDto> findById(
            @Parameter(description = "Inventory primary key") @PathVariable Integer id) {
        log.info("REST GET /api/v1/inventories/{}", id);
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @GetMapping
    @Operation(summary = "List all inventory items (paginated)")
    @ApiResponse(responseCode = "200", description = "Inventories retrieved")
    public ResponseEntity<Page<InventoryResponseDto>> findAll(@PageableDefault(size = 20) Pageable pageable) {
        log.info("REST GET /api/v1/inventories - page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(inventoryService.findAll(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "List all inventory items (unpaged)")
    @ApiResponse(responseCode = "200", description = "All inventories retrieved")
    public ResponseEntity<List<InventoryResponseDto>> getAllInventories() {
        log.info("REST GET /api/v1/inventories/all");
        return ResponseEntity.ok(inventoryService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an inventory item by ID")
    @ApiResponse(responseCode = "204", description = "Inventory deleted successfully")
    @ApiResponse(responseCode = "404", description = "Inventory not found")
    public ResponseEntity<Void> delete(@Parameter(description = "Inventory primary key") @PathVariable Integer id) {
        log.info("REST DELETE /api/v1/inventories/{}", id);
        inventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Count total number of inventory items")
    @ApiResponse(responseCode = "200", description = "Count returned")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(inventoryService.count());
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Check whether an inventory item exists by ID")
    @ApiResponse(responseCode = "200", description = "Existence flag returned")
    public ResponseEntity<Boolean> existsById(
            @Parameter(description = "Inventory primary key") @PathVariable Integer id) {
        return ResponseEntity.ok(inventoryService.existsById(id));
    }
}


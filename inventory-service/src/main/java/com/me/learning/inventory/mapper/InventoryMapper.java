package com.me.learning.inventory.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.inventory.dto.InventoryRequestDto;
import com.me.learning.inventory.dto.InventoryResponseDto;
import com.me.learning.inventory.entity.Inventory;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface InventoryMapper {

    @Mapping(source = "store", target = "store")
    Inventory toEntity(InventoryRequestDto inventoryRequestDto);

    @Mapping(source = "store", target = "store")
    InventoryRequestDto toDto(Inventory inventory);

    @Mapping(source = "store.id", target = "storeId")
    InventoryResponseDto toResponseDto(Inventory inventory);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "store", target = "store")
    Inventory partialUpdate(InventoryRequestDto inventoryRequestDto, @MappingTarget Inventory inventory);
}


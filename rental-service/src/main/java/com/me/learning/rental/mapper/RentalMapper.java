package com.me.learning.rental.mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.rental.dto.RentalRequestDto;
import com.me.learning.rental.dto.RentalResponseDto;
import com.me.learning.rental.entity.Rental;

/**
 * Maps between {@link Rental} and {@link RentalRequestDto} / {@link RentalResponseDto}.
 *
 * <p>{@link Rental} holds only scalar fields and cross-service IDs — there are no
 * intra-service {@code @ManyToOne} associations, so no nested mapper delegation
 * is required. MapStruct maps all fields by name automatically.
 */
@Mapper (unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface RentalMapper {

    Rental toEntity (RentalRequestDto rentalRequestDto);

    RentalRequestDto toDto (Rental rental);

    /**
     * Map a {@link Rental} to a flat {@link RentalResponseDto}.
     *
     * <p>All scalar fields are mapped by name automatically — no nested entity
     * traversal is needed because cross-service references are already plain IDs.
     */
    RentalResponseDto toResponseDto (Rental rental);

    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Rental partialUpdate (RentalRequestDto rentalRequestDto, @MappingTarget Rental rental);
}


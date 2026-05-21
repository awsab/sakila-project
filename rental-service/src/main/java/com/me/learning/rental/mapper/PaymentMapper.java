package com.me.learning.rental.mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.rental.dto.PaymentRequestDto;
import com.me.learning.rental.dto.PaymentResponseDto;
import com.me.learning.rental.entity.Payment;

/**
 * Maps between {@link Payment} and {@link PaymentRequestDto} / {@link PaymentResponseDto}.
 *
 * <p>{@code uses = RentalMapper.class} delegates the {@code Rental ↔ RentalRequestDto}
 * conversion to the dedicated mapper. Without this, MapStruct silently drops the nested
 * {@code rental} field due to the field-name mismatch
 * (entity field {@code rental} vs DTO field {@code rental}).
 * </p>
 */
@Mapper (unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {RentalMapper.class})
public interface PaymentMapper {

    @Mapping (source = "rental", target = "rental")
    Payment toEntity (PaymentRequestDto paymentRequestDto);

    @Mapping (source = "rental", target = "rental")
    PaymentRequestDto toDto (Payment payment);

    /**
     * Map a {@link Payment} to a flat {@link PaymentResponseDto} that exposes only
     * the ID of the related rental.
     */
    @Mapping (source = "rental.id", target = "rentalId")
    PaymentResponseDto toResponseDto (Payment payment);

    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping (source = "rental", target = "rental")
    Payment partialUpdate (PaymentRequestDto paymentRequestDto, @MappingTarget Payment payment);
}


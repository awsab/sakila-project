package com.me.learning.parent.paymentservice.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.paymentservice.dto.RentalRequest;
import com.me.learning.parent.paymentservice.dto.RentalResponse;
import com.me.learning.parent.paymentservice.dto.RentalUpdateRequest;
import com.me.learning.parent.paymentservice.entity.Rental;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : MapStruct mapper for Rental entity
 * Since    : Version 1.0
 *
 * FK mapping reminder:
 *   Request  -> Entity:  @Mapping(target = "parent.id", source = "parentId")
 *   Entity   -> Response: @Mapping(source = "parent.id", target = "parentId")
 *                         @Mapping(source = "parent.name", target = "parentName")
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RentalMapper {

    // TODO: add @Mapping annotations for FK fields
    Rental toEntity (RentalRequest request);

    // TODO: add @Mapping annotations for FK fields
    RentalResponse toResponse (Rental entity);

    List<RentalResponse> toResponseList (List<Rental> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity (RentalUpdateRequest request, @MappingTarget Rental entity);
}

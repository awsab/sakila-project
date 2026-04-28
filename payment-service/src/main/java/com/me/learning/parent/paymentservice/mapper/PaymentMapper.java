package com.me.learning.parent.paymentservice.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.paymentservice.dto.PaymentRequest;
import com.me.learning.parent.paymentservice.dto.PaymentResponse;
import com.me.learning.parent.paymentservice.dto.PaymentUpdateRequest;
import com.me.learning.parent.paymentservice.entity.Payment;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 21/04/2026
 * Usage    : MapStruct mapper for Payment entity
 * Since    : Version 1.0
 *
 * FK mapping reminder:
 *   Request  -> Entity:  @Mapping(target = "parent.id", source = "parentId")
 *   Entity   -> Response: @Mapping(source = "parent.id", target = "parentId")
 *                         @Mapping(source = "parent.name", target = "parentName")
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    // TODO: add @Mapping annotations for FK fields
    Payment toEntity (PaymentRequest request);

    // TODO: add @Mapping annotations for FK fields
    PaymentResponse toResponse (Payment entity);

    List<PaymentResponse> toResponseList (List<Payment> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity (PaymentUpdateRequest request, @MappingTarget Payment entity);
}

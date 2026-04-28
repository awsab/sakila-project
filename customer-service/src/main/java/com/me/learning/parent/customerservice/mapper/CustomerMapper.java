package com.me.learning.parent.customerservice.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.customerservice.dto.CustomerDetailResponse;
import com.me.learning.parent.customerservice.dto.CustomerRequest;
import com.me.learning.parent.customerservice.dto.CustomerResponse;
import com.me.learning.parent.customerservice.dto.CustomerUpdateRequest;
import com.me.learning.parent.customerservice.dto.PaymentDetailResponse;
import com.me.learning.parent.customerservice.entity.Customer;
import com.me.learning.parent.customerservice.entity.Payment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AddressMapper.class)
public interface CustomerMapper {

    @Mapping(target = "address.id", source = "addressId")
    Customer toEntity(CustomerRequest request);

    @Mapping(source = "address.id", target = "addressId")
    CustomerResponse toResponse(Customer customer);

    @Mapping(source = "address.id", target = "addressId")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "payments", source = "payments")
    CustomerDetailResponse toDetailResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);


    PaymentDetailResponse toPaymentDetailResponse(Payment payment);

    List<PaymentDetailResponse> toPaymentDetailResponseList(Set<Payment> payments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "address.id", source = "addressId")
    void updateEntity(CustomerUpdateRequest request, @MappingTarget Customer customer);
}


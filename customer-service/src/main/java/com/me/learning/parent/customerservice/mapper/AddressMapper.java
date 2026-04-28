package com.me.learning.parent.customerservice.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.customerservice.dto.AddressRequest;
import com.me.learning.parent.customerservice.dto.AddressResponse;
import com.me.learning.parent.customerservice.dto.AddressUpdateRequest;
import com.me.learning.parent.customerservice.entity.Address;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    @Mapping(target = "city.id", source = "cityId")
    Address toEntity(AddressRequest request);

    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "city.city", target = "cityName")
    AddressResponse toResponse(Address address);

    List<AddressResponse> toResponseList(List<Address> addresses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "city.id", source = "cityId")
    void updateEntity(AddressUpdateRequest request, @MappingTarget Address address);
}


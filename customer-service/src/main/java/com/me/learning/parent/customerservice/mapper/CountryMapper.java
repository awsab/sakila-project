package com.me.learning.parent.customerservice.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.customerservice.dto.CountryRequest;
import com.me.learning.parent.customerservice.dto.CountryResponse;
import com.me.learning.parent.customerservice.dto.CountryUpdateRequest;
import com.me.learning.parent.customerservice.entity.Country;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {

    Country toEntity(CountryRequest request);

    CountryResponse toResponse(Country country);

    List<CountryResponse> toResponseList(List<Country> countries);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(CountryUpdateRequest request, @MappingTarget Country country);
}


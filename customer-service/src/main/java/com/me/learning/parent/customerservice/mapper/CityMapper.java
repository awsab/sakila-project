package com.me.learning.parent.customerservice.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.customerservice.dto.CityRequest;
import com.me.learning.parent.customerservice.dto.CityResponse;
import com.me.learning.parent.customerservice.dto.CityUpdateRequest;
import com.me.learning.parent.customerservice.entity.City;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityMapper {

    @Mapping(target = "country.id", source = "countryId")
    City toEntity(CityRequest request);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "country.country", target = "countryName")
    CityResponse toResponse(City city);

    List<CityResponse> toResponseList(List<City> cities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "country.id", source = "countryId")
    void updateEntity(CityUpdateRequest request, @MappingTarget City city);
}


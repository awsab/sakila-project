package com.me.learning.parent.customerservice.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.parent.customerservice.dto.StaffRequest;
import com.me.learning.parent.customerservice.dto.StaffResponse;
import com.me.learning.parent.customerservice.dto.StaffUpdateRequest;
import com.me.learning.parent.customerservice.entity.Staff;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffMapper {

    @Mapping(target = "addressId.id", source = "addressId")
    Staff toEntity(StaffRequest request);

    @Mapping(source = "addressId.id", target = "addressId")
    StaffResponse toResponse(Staff staff);

    List<StaffResponse> toResponseList(List<Staff> staffList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "addressId.id", source = "addressId")
    void updateEntity(StaffUpdateRequest request, @MappingTarget Staff staff);
}


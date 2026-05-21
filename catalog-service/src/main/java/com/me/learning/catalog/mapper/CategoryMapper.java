package com.me.learning.catalog.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;
import com.me.learning.catalog.dto.CategorySummaryDto;
import com.me.learning.catalog.entity.Category;

/**
 * Mapper for lightweight category projections.
 */
@Mapper (unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
        )
public interface CategoryMapper {

    Category toEntity (CategoryRequestDto categoryRequestDto);

    CategoryResponseDto toResponseDto (Category category);

    CategorySummaryDto toSummaryDto (Category category);

    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category partialUpdate (CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}


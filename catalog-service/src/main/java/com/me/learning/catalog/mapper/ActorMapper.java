package com.me.learning.catalog.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;
import com.me.learning.catalog.dto.ActorSummaryDto;
import com.me.learning.catalog.entity.Actor;

/**
 * Mapper for lightweight actor projections.
 */
@Mapper (unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    Actor toEntity (ActorRequestDto actorRequestDto);

    ActorResponseDto toResponseDto (Actor actor);

    ActorSummaryDto toSummaryDto (Actor actor);

    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Actor partialUpdate (ActorRequestDto actorRequestDto, @MappingTarget Actor actor);
}


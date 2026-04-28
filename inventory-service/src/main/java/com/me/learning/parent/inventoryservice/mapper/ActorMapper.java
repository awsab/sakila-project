package com.me.learning.parent.inventoryservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.me.learning.parent.inventoryservice.dto.request.ActorRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.ActorResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.ActorUpdateDTO;
import com.me.learning.parent.inventoryservice.model.Actor;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : MapStruct mapper for Actor entity
 * Since    : Version 1.0
 */
@Mapper (componentModel = "spring")
public interface ActorMapper {

    // POST - Create new actor
    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "actorFilmSet", ignore = true)
    Actor toEntity (ActorRequestDTO dto);

    // PUT/PATCH - Update existing actor
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "actorFilmSet", ignore = true)
    Actor toEntity (ActorUpdateDTO dto);

    // Update existing entity (for PATCH)
    @Mapping (target = "id", ignore = true)
    @Mapping (target = "actorFilmSet", ignore = true)
    void updateEntity (ActorUpdateDTO dto, @MappingTarget Actor entity);

    // GET - Response mapping
    ActorResponseDTO toDto (Actor entity);

    List<ActorResponseDTO> toDtoList (List<Actor> entityList);
}

package com.me.learning.parent.inventoryservice.mapper;


import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.me.learning.parent.inventoryservice.dto.request.FilmRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.FilmResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.FilmUpdateDTO;
import com.me.learning.parent.inventoryservice.model.Film;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : MapStruct mapper for Film entity
 * Since    : Version 1.0
 */
@Mapper (componentModel = "spring")
public interface FilmMapper {

    // POST - Create new film
    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "filmCategorySet", ignore = true)
    @Mapping (target = "filmActorsSet", ignore = true)
    Film toEntity (FilmRequestDTO dto);

    // PUT - Full update existing film
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "filmCategorySet", ignore = true)
    @Mapping (target = "filmActorsSet", ignore = true)
    Film toEntity (FilmUpdateDTO dto);

    // PATCH - Partial update (only non-null fields)
    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "filmCategorySet", ignore = true)
    @Mapping (target = "filmActorsSet", ignore = true)
    void updateEntity (FilmUpdateDTO dto, @MappingTarget Film entity);

    // GET - Response mapping
    FilmResponseDTO toDto (Film entity);

    List<FilmResponseDTO> toDtoList (List<Film> entityList);
}

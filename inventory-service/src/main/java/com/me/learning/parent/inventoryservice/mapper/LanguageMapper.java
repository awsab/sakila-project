package com.me.learning.parent.inventoryservice.mapper;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.me.learning.parent.inventoryservice.dto.request.LanguageRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.LanguageResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.LanguageUpdateDTO;
import com.me.learning.parent.inventoryservice.model.Language;

@Mapper (componentModel = "spring")
public interface LanguageMapper {

    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    Language toEntity (LanguageRequestDTO dto);

    @Mapping (target = "lastUpdate", ignore = true)
    Language toEntity (LanguageUpdateDTO dto);

    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    void updateEntity (LanguageUpdateDTO dto, @MappingTarget Language entity);

    LanguageResponseDTO toDto (Language entity);

    List<LanguageResponseDTO> toDtoList (List<Language> entityList);
}

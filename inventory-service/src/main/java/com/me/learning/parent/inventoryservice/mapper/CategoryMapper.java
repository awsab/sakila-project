package com.me.learning.parent.inventoryservice.mapper;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.me.learning.parent.inventoryservice.dto.request.CategoryRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.CategoryResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.CategoryUpdateDTO;
import com.me.learning.parent.inventoryservice.model.Category;

@Mapper (componentModel = "spring")
public interface CategoryMapper {

    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "categoryFilmSet", ignore = true)
    Category toEntity (CategoryRequestDTO dto);

    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "categoryFilmSet", ignore = true)
    Category toEntity (CategoryUpdateDTO dto);

    @Mapping (target = "id", ignore = true)
    @Mapping (target = "lastUpdate", ignore = true)
    @Mapping (target = "categoryFilmSet", ignore = true)
    void updateEntity (CategoryUpdateDTO dto, @MappingTarget Category entity);

    CategoryResponseDTO toDto (Category entity);

    List<CategoryResponseDTO> toDtoList (List<Category> entityList);
}

package com.me.learning.catalog.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.me.learning.catalog.dto.ActorSummaryDto;
import com.me.learning.catalog.dto.CategorySummaryDto;
import com.me.learning.catalog.dto.FilmRequestDto;
import com.me.learning.catalog.dto.FilmResponseDto;
import com.me.learning.catalog.entity.Film;
import com.me.learning.catalog.entity.FilmActor;
import com.me.learning.catalog.entity.FilmCategory;

/**
 * Maps inbound film write payloads into {@link Film} entities.
 *
 * <p>Language and join-table links are resolved in the service layer from ID lists,
 * so they are intentionally ignored at mapper level.</p>
 */
@Mapper (unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface FilmMapper {

    @Mapping (target = "language", ignore = true)
    @Mapping (target = "originalLanguage", ignore = true)
    @Mapping (target = "filmActors", ignore = true)
    @Mapping (target = "filmCategories", ignore = true)
    Film toEntity (FilmRequestDto filmRequestDto);

    @BeanMapping (nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping (target = "language", ignore = true)
    @Mapping (target = "originalLanguage", ignore = true)
    @Mapping (target = "filmActors", ignore = true)
    @Mapping (target = "filmCategories", ignore = true)
    Film partialUpdate (FilmRequestDto filmRequestDto, @MappingTarget Film film);

    @Mapping (target = "languageId", source = "language.id")
    @Mapping (target = "languageName", source = "language.name")
    @Mapping (target = "originalLanguageId", source = "originalLanguage.id")
    @Mapping (target = "originalLanguageName", source = "originalLanguage.name")
    @Mapping (target = "actors", expression = "java(toActorSummaryDtos(film))")
    @Mapping (target = "categories", expression = "java(toCategorySummaryDtos(film))")
    FilmResponseDto toFilmResponseDto (Film film);

    default List<ActorSummaryDto> toActorSummaryDtos (Film film) {
        if (film.getFilmActors () == null) {
            return List.of ();
        }
        return film.getFilmActors ().stream ()
                .map (FilmActor::getActor)
                .map (a -> new ActorSummaryDto (a.getId (), a.getFirstName (), a.getLastName ()))
                .toList ();
    }

    default List<CategorySummaryDto> toCategorySummaryDtos (Film film) {
        if (film.getFilmCategories () == null) {
            return List.of ();
        }
        return film.getFilmCategories ().stream ()
                .map (FilmCategory::getCategory)
                .map (c -> new CategorySummaryDto (c.getId (), c.getName ()))
                .toList ();
    }
}


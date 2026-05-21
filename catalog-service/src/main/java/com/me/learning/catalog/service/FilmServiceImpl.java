package com.me.learning.catalog.service;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.catalog.dto.ActorRequestDto;
import com.me.learning.catalog.dto.ActorResponseDto;
import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;
import com.me.learning.catalog.dto.FilmRequestDto;
import com.me.learning.catalog.dto.FilmResponseDto;
import com.me.learning.catalog.entity.Actor;
import com.me.learning.catalog.entity.Category;
import com.me.learning.catalog.entity.Film;
import com.me.learning.catalog.entity.FilmActor;
import com.me.learning.catalog.entity.FilmActorId;
import com.me.learning.catalog.entity.FilmCategory;
import com.me.learning.catalog.entity.FilmCategoryId;
import com.me.learning.catalog.entity.Language;
import com.me.learning.catalog.mapper.ActorMapper;
import com.me.learning.catalog.mapper.CategoryMapper;
import com.me.learning.catalog.mapper.FilmMapper;
import com.me.learning.catalog.repository.ActorRepository;
import com.me.learning.catalog.repository.CategoryRepository;
import com.me.learning.catalog.repository.FilmActorRepository;
import com.me.learning.catalog.repository.FilmCategoryRepository;
import com.me.learning.catalog.repository.FilmRepository;
import com.me.learning.catalog.repository.LanguageRepository;
import com.me.learning.framework.web.errors.ResourceNotFoundException;

/**
 * Default implementation of {@link FilmService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class FilmServiceImpl implements FilmService {

    private static final String RESOURCE_FILM = "Film";
    private static final String RESOURCE_LANGUAGE = "Language";
    private static final String RESOURCE_ACTOR = "Actor";
    private static final String RESOURCE_CATEGORY = "Category";
    private static final String FIELD_ID = "id";

    private final FilmRepository filmRepository;
    private final LanguageRepository languageRepository;
    private final ActorRepository actorRepository;
    private final CategoryRepository categoryRepository;
    private final FilmActorRepository filmActorRepository;
    private final FilmCategoryRepository filmCategoryRepository;

    private final FilmMapper filmMapper;
    private final ActorMapper actorMapper;
    private final CategoryMapper categoryMapper;
    private final ActorService actorService;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public FilmResponseDto create (FilmRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.id () != null) {
            throw new IllegalArgumentException ("ID must not be provided in request");
        }
        Film film = filmMapper.toEntity (dto);
        film.setId (null);
        film.setLanguage (resolveLanguage (dto.languageId ()));
        film.setOriginalLanguage (resolveOriginalLanguage (dto.originalLanguageId ()));
        film.setLastUpdate (dto.lastUpdate () != null ? dto.lastUpdate () : Instant.now ());

        Film saved = filmRepository.save (film);
        replaceActors (saved, mergeActorIds (dto.actorIds (), dto.inlineActors ()));
        replaceCategories (saved, mergeCategoryIds (dto.categoryIds (), dto.inlineCategories ()));

        log.info ("Created film with ID: {}", saved.getId ());
        return findById (saved.getId ());
    }

    @Override
    @Transactional
    public FilmResponseDto update (Integer id, FilmRequestDto dto) {
        validateIdAndRequest (id, dto);
        Film existing = filmRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_FILM, FIELD_ID, id));

        existing.setTitle (dto.title ());
        existing.setDescription (dto.description ());
        existing.setReleaseYear (dto.releaseYear ());
        existing.setLanguage (resolveLanguage (dto.languageId ()));
        existing.setOriginalLanguage (resolveOriginalLanguage (dto.originalLanguageId ()));
        existing.setRentalDuration (dto.rentalDuration ());
        existing.setRentalRate (dto.rentalRate ());
        existing.setLength (dto.length ());
        existing.setReplacementCost (dto.replacementCost ());
        existing.setRating (dto.rating ());
        existing.setSpecialFeatures (dto.specialFeatures ());
        existing.setLastUpdate (dto.lastUpdate () != null ? dto.lastUpdate () : Instant.now ());

        filmRepository.save (existing);
        replaceActors (
                existing,
                mergeActorIds (dto.actorIds () == null ? List.of () : dto.actorIds (), dto.inlineActors ())
        );
        replaceCategories (
                existing,
                mergeCategoryIds (dto.categoryIds () == null ? List.of () : dto.categoryIds (), dto.inlineCategories ())
        );

        log.info ("Updated film with ID: {}", id);
        return findById (id);
    }

    @Override
    @Transactional
    public FilmResponseDto partialUpdate (Integer id, FilmRequestDto dto) {
        validateIdAndRequest (id, dto);
        Film existing = filmRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_FILM, FIELD_ID, id));

        filmMapper.partialUpdate (dto, existing);

        if (dto.languageId () != null) {
            existing.setLanguage (resolveLanguage (dto.languageId ()));
        }
        if (dto.originalLanguageId () != null) {
            existing.setOriginalLanguage (resolveOriginalLanguage (dto.originalLanguageId ()));
        }
        existing.setLastUpdate (dto.lastUpdate () != null ? dto.lastUpdate () : Instant.now ());

        filmRepository.save (existing);

        if (dto.actorIds () != null || dto.inlineActors () != null) {
            replaceActors (existing, mergeActorIds (dto.actorIds (), dto.inlineActors ()));
        }
        if (dto.categoryIds () != null || dto.inlineCategories () != null) {
            replaceCategories (existing, mergeCategoryIds (dto.categoryIds (), dto.inlineCategories ()));
        }

        log.info ("Patched film with ID: {}", id);
        return findById (id);
    }

    @Override
    public FilmResponseDto findById (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException ("Film ID cannot be null");
        }
        return filmRepository.findById (id)
                .map (this::toResponseDto)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_FILM, FIELD_ID, id));
    }

    @Override
    public List<FilmResponseDto> findAll () {
        return filmRepository.findAll ().stream ().map (this::toResponseDto).toList ();
    }

    @Override
    public Page<FilmResponseDto> findAll (Pageable pageable) {
        return filmRepository.findAll (pageable).map (this::toResponseDto);
    }

    @Override
    @Transactional
    public void delete (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException ("Film ID must not be null");
        }
        if (!filmRepository.existsById (id)) {
            throw new ResourceNotFoundException (RESOURCE_FILM, FIELD_ID, id);
        }

        filmActorRepository.deleteByFilmId (id);
        filmCategoryRepository.deleteByFilmId (id);
        filmRepository.deleteById (id);

        log.info ("Deleted film with ID: {}", id);
    }

    @Override
    public boolean existsById (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException ("Film ID must not be null");
        }
        return filmRepository.existsById (id);
    }

    @Override
    public long count () {
        long total = filmRepository.count ();
        if (total < 0) {
            throw new IllegalArgumentException ("Count must not be negative");
        }
        return total;
    }

    private Language resolveLanguage (Short languageId) {
        return languageRepository.findById (languageId)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_LANGUAGE, FIELD_ID, languageId));
    }

    private Language resolveOriginalLanguage (Short originalLanguageId) {
        if (originalLanguageId == null) {
            return null;
        }
        return languageRepository.findById (originalLanguageId)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_LANGUAGE, FIELD_ID, originalLanguageId));
    }

    private void replaceActors (Film film, List<Integer> actorIds) {
        filmActorRepository.deleteByFilmId (film.getId ());

        if (actorIds == null || actorIds.isEmpty ()) {
            return;
        }

        Set<Integer> uniqueActorIds = new LinkedHashSet<> (actorIds);
        for (Integer actorId : uniqueActorIds) {
            Actor actor = resolveActor (actorId);
            FilmActor filmActor = newFilmActor (film, actorId, actor);
            filmActorRepository.save (filmActor);
        }
    }

    private void replaceCategories (Film film, List<Short> categoryIds) {
        filmCategoryRepository.deleteByFilmId (film.getId ());

        if (categoryIds == null || categoryIds.isEmpty ()) {
            return;
        }

        Set<Short> uniqueCategoryIds = new LinkedHashSet<> (categoryIds);
        for (Short categoryId : uniqueCategoryIds) {
            Category category = resolveCategory (categoryId);
            FilmCategory filmCategory = newFilmCategory (film, categoryId, category);
            filmCategoryRepository.save (filmCategory);
        }
    }

    private Actor resolveActor (Integer actorId) {
        return actorRepository.findById (actorId)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_ACTOR, FIELD_ID, actorId));
    }

    private Category resolveCategory (Short categoryId) {
        return categoryRepository.findById (categoryId)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_CATEGORY, FIELD_ID, categoryId));
    }

    private FilmActor newFilmActor (Film film, Integer actorId, Actor actor) {
        FilmActor filmActor = new FilmActor ();
        FilmActorId id = new FilmActorId ();
        id.setFilmId (film.getId ());
        id.setActorId (actorId);
        filmActor.setId (id);
        filmActor.setFilm (film);
        filmActor.setActor (actor);
        filmActor.setLastUpdate (Instant.now ());
        return filmActor;
    }

    private FilmCategory newFilmCategory (Film film, Short categoryId, Category category) {
        FilmCategory filmCategory = new FilmCategory ();
        FilmCategoryId id = new FilmCategoryId ();
        id.setFilmId (film.getId ());
        id.setCategoryId (categoryId);
        filmCategory.setId (id);
        filmCategory.setFilm (film);
        filmCategory.setCategory (category);
        filmCategory.setLastUpdate (Instant.now ());
        return filmCategory;
    }

    private List<Integer> mergeActorIds (List<Integer> actorIds, List<ActorRequestDto> inlineActors) {
        Set<Integer> merged = new LinkedHashSet<> (actorIds == null ? List.of () : actorIds);
        if (inlineActors == null || inlineActors.isEmpty ()) {
            return List.copyOf (merged);
        }

        for (ActorRequestDto inlineActor : inlineActors) {
            ActorResponseDto created = actorService.create (inlineActor);
            merged.add (created.id ());
        }
        return List.copyOf (merged);
    }

    private List<Short> mergeCategoryIds (List<Short> categoryIds, List<CategoryRequestDto> inlineCategories) {
        Set<Short> merged = new LinkedHashSet<> (categoryIds == null ? List.of () : categoryIds);
        if (inlineCategories == null || inlineCategories.isEmpty ()) {
            return List.copyOf (merged);
        }

        for (CategoryRequestDto inlineCategory : inlineCategories) {
            CategoryResponseDto created = categoryService.create (inlineCategory);
            merged.add (created.id ());
        }
        return List.copyOf (merged);
    }

    private FilmResponseDto toResponseDto (Film film) {
        return filmMapper.toFilmResponseDto (film);
    }

    private void validateIdAndRequest (Integer id, FilmRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException ("Film ID cannot be null");
        }
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
    }
}



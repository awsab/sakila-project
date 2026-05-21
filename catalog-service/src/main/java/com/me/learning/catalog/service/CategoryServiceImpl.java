package com.me.learning.catalog.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;
import com.me.learning.catalog.entity.Category;
import com.me.learning.catalog.mapper.CategoryMapper;
import com.me.learning.catalog.repository.CategoryRepository;
import com.me.learning.framework.web.errors.ResourceNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private static final String RESOURCE_CATEGORY = "Category";
    private static final String FIELD_ID = "id";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDto create (CategoryRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.id () != null) {
            throw new IllegalArgumentException ("Category ID must be null when creating a new category");
        }
        Category category = categoryMapper.toEntity (dto);
        category.setId (null);
        category.setLastUpdate (dto.lastUpdate () != null ? dto.lastUpdate () : Instant.now ());
        Category saved = categoryRepository.save (category);
        log.info ("Created category with ID: {}", saved.getId ());
        return categoryMapper.toResponseDto (saved);
    }

    @Override
    @Transactional
    public CategoryResponseDto update (Short id, CategoryRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.name () == null) {
            throw new IllegalArgumentException ("Category name cannot be null");
        }


        Category category = categoryMapper.toEntity (dto);
        category.setId (id);
        category.setLastUpdate (dto.lastUpdate () != null ? dto.lastUpdate () : Instant.now ());

        Category saved = categoryRepository.save (category);
        log.info ("Updated category with ID: {}", id);
        return categoryMapper.toResponseDto (saved);
    }

    @Override
    @Transactional
    public CategoryResponseDto partialUpdate (Short id, CategoryRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        if (dto == null) {
            throw new IllegalArgumentException ("Request cannot be null");
        }
        if (dto.name () == null) {
            throw new IllegalArgumentException ("Category name cannot be null");
        }

        Category existing = categoryRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_CATEGORY, FIELD_ID, id));

        categoryMapper.partialUpdate (dto, existing);
        existing.setLastUpdate (dto.lastUpdate () != null ? dto.lastUpdate () : Instant.now ());

        Category saved = categoryRepository.save (existing);
        log.info ("Patched category with ID: {}", id);
        return categoryMapper.toResponseDto (saved);
    }

    @Override
    public CategoryResponseDto findById (Short id) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        return categoryRepository.findById (id)
                .map (categoryMapper::toResponseDto)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE_CATEGORY, FIELD_ID, id));
    }

    @Override
    public List<CategoryResponseDto> findAll () {
        return categoryRepository.findAll ().stream ().map (categoryMapper::toResponseDto).toList ();
    }

    @Override
    public Page<CategoryResponseDto> findAll (Pageable pageable) {
        return categoryRepository.findAll (pageable).map (categoryMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void delete (Short id) {
        if (!categoryRepository.existsById (id)) {
            throw new ResourceNotFoundException (RESOURCE_CATEGORY, FIELD_ID, id);
        }
        categoryRepository.deleteById (id);
        log.info ("Deleted category with ID: {}", id);
    }

    @Override
    public boolean existsById (Short id) {
        if (id == null) {
            throw new IllegalArgumentException ("ID cannot be null");
        }
        return categoryRepository.existsById (id);
    }

    @Override
    public long count () {
        long total = categoryRepository.count ();
        if (total < 0) {
            throw new IllegalArgumentException ("Count cannot be negative");
        }
        return total;
    }
}


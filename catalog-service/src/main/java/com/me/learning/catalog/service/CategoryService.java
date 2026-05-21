package com.me.learning.catalog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.me.learning.catalog.dto.CategoryRequestDto;
import com.me.learning.catalog.dto.CategoryResponseDto;

/**
 * Service interface for {@link com.me.learning.catalog.entity.Category} CRUD operations.
 */
public interface CategoryService {

    CategoryResponseDto create (CategoryRequestDto dto);

    CategoryResponseDto update (Short id, CategoryRequestDto dto);

    CategoryResponseDto partialUpdate (Short id, CategoryRequestDto dto);

    CategoryResponseDto findById (Short id);

    List<CategoryResponseDto> findAll ();

    Page<CategoryResponseDto> findAll (Pageable pageable);

    void delete (Short id);

    boolean existsById (Short id);

    long count ();
}


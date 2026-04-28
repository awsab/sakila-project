package com.me.learning.parent.inventoryservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import com.me.learning.parent.inventoryservice.dto.request.LanguageRequestDTO;
import com.me.learning.parent.inventoryservice.dto.response.LanguageResponseDTO;
import com.me.learning.parent.inventoryservice.dto.update.LanguageUpdateDTO;
import com.me.learning.parent.inventoryservice.mapper.LanguageMapper;
import com.me.learning.parent.inventoryservice.model.Language;
import com.me.learning.parent.inventoryservice.repository.LanguageRepository;


/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Service implementation for Language entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class LanguageServiceImpl implements LanguageService {

    private static final String LANGUAGE_RESOURCE = "Language";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Override
    @Transactional
    public LanguageResponseDTO createLanguage (LanguageRequestDTO requestDTO) {
        log.debug ("Creating new language: {}", requestDTO.getName ());

        // Check if language already exists
        if ( languageRepository.existsByName (requestDTO.getName ()) ) {
            throw new IllegalArgumentException ("Language with name '" + requestDTO.getName () + "' already exists");
        }

        Language language = languageMapper.toEntity (requestDTO);
        Language savedLanguage = languageRepository.save (language);

        log.info ("Created language with ID: {}", savedLanguage.getId ());
        return languageMapper.toDto (savedLanguage);
    }

    @Override
    @Transactional
    public LanguageResponseDTO updateLanguage (Short id, LanguageUpdateDTO updateDTO) {
        log.debug ("Updating language with ID: {}", id);

        Language existingLanguage = languageRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (LANGUAGE_RESOURCE, FIELD_ID, id));

        // Check if name change conflicts with existing language
        if ( !existingLanguage.getName ().equals (updateDTO.getName ()) &&
                languageRepository.existsByName (updateDTO.getName ()) ) {
            throw new DuplicateResourceException (LANGUAGE_RESOURCE, FIELD_NAME, updateDTO.getName ());
        }

        existingLanguage.setName (updateDTO.getName ());

        Language updatedLanguage = languageRepository.save (existingLanguage);

        log.info ("Updated language with ID: {}", id);
        return languageMapper.toDto (updatedLanguage);
    }

    @Override
    @Transactional
    public LanguageResponseDTO patchLanguage (Short id, LanguageUpdateDTO updateDTO) {
        log.debug ("Partially updating language with ID: {}", id);

        Language existingLanguage = languageRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (LANGUAGE_RESOURCE, FIELD_ID, id));

        languageMapper.updateEntity (updateDTO, existingLanguage);

        Language updatedLanguage = languageRepository.save (existingLanguage);

        log.info ("Patched language with ID: {}", id);
        return languageMapper.toDto (updatedLanguage);
    }

    @Override
    public LanguageResponseDTO getLanguageById (Short id) {
        log.debug ("Fetching language with ID: {}", id);

        Language language = languageRepository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (LANGUAGE_RESOURCE, FIELD_ID, id));

        return languageMapper.toDto (language);
    }

    @Override
    public LanguageResponseDTO getLanguageByName (String name) {
        log.debug ("Fetching language with name: {}", name);

        Language language = languageRepository.findByName (name)
                .orElseThrow (() -> new ResourceNotFoundException (LANGUAGE_RESOURCE, FIELD_NAME, name));

        return languageMapper.toDto (language);
    }

    @Override
    public List<LanguageResponseDTO> getAllLanguages () {
        log.debug ("Fetching all languages");

        List<Language> languages = languageRepository.findAll ();
        return languageMapper.toDtoList (languages);
    }

    @Override
    public Page<LanguageResponseDTO> getAllLanguages (Pageable pageable) {
        log.debug ("Fetching languages with pagination: page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());

        Page<Language> languagePage = languageRepository.findAll (pageable);
        return languagePage.map (languageMapper::toDto);
    }

    @Override
    public List<LanguageResponseDTO> searchLanguagesByName (String name) {
        log.debug ("Searching languages by name: {}", name);

        List<Language> languages = languageRepository.findByNameContainingIgnoreCase (name);
        return languageMapper.toDtoList (languages);
    }

    @Override
    public List<LanguageResponseDTO> getAllLanguagesSortedByName () {
        log.debug ("Fetching all languages sorted by name");

        List<Language> languages = languageRepository.findAllByOrderByNameAsc ();
        return languageMapper.toDtoList (languages);
    }

    @Override
    public boolean existsById (Short id) {
        log.debug ("Checking if language exists with ID: {}", id);
        return languageRepository.existsById (id);
    }

    @Override
    public boolean existsByName (String name) {
        log.debug ("Checking if language exists with name: {}", name);
        return languageRepository.existsByName (name);
    }

    @Override
    @Transactional
    public void deleteLanguage (Short id) {
        log.debug ("Deleting language with ID: {}", id);

        if ( !languageRepository.existsById (id) ) {
            throw new ResourceNotFoundException (LANGUAGE_RESOURCE, FIELD_ID, id);
        }

        languageRepository.deleteById (id);
        log.info ("Deleted language with ID: {}", id);
    }

    @Override
    public long countLanguages () {
        log.debug ("Counting total languages");
        return languageRepository.count ();
    }
}

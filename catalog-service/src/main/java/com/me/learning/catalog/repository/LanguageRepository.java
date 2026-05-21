package com.me.learning.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.me.learning.catalog.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, Short>, JpaSpecificationExecutor<Language> {
}


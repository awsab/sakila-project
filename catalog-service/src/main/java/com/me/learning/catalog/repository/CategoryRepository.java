package com.me.learning.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.me.learning.catalog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Short>, JpaSpecificationExecutor<Category> {
}


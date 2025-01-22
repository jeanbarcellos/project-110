package com.jeanbarcellos.project110.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanbarcellos.project110.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
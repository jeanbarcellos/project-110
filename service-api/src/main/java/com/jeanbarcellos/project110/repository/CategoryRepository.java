package com.jeanbarcellos.project110.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeanbarcellos.project110.entity.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
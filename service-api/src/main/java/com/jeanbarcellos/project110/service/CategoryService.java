package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.project110.entity.Category;
import com.jeanbarcellos.project110.repository.CategoryRepository;

/**
 * Pattern Write-Through
 *
 * Adicionar ao cache logo na criação
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    @Transactional
    public Category createCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category entity = this.getCategoryById(id);

        entity.setName(category.getName());

        return this.categoryRepository.save(entity);
    }

    @Transactional
    public void deleteCategory(Long id) {
        this.categoryRepository.deleteById(id);
    }

}
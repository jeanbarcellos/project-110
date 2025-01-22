package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.project110.entity.Category;
import com.jeanbarcellos.project110.repository.CategoryRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Pattern Write-Through
 *
 * Adicionar ao cache logo na criação
 */
@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Recupera todas as categorias do banco de dados.
     *
     * - Usa cache para armazenar a lista completa de categorias com a chave 'all'.
     * - Sempre consulta o cache antes de buscar no banco.
     */
    @Cacheable(value = "categories", key = "'all'")
    public List<Category> getAllCategories() {
        doLongRunningTask();

        return this.categoryRepository.findAll();
    }

    /**
     * Recupera uma categoria específica pelo ID.
     *
     * - Usa cache para armazenar cada categoria individualmente com a chave baseada no ID.
     * - O cache é preenchido na primeira chamada deste método para um ID específico.
     */
    @Cacheable(value = "categories", key = "#id")
    public Category getCategoryById(Long id) {
        doLongRunningTask();

        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    /**
     * Cria uma nova categoria no banco de dados.
     *
     * - Adiciona ao cache o produto criado.
     * - Invalida o cache da lista completa ('all').
     */
    @CachePut(value = "categories", key = "#result.id")
    @CacheEvict(value = "categories", key = "'all'")
    @Transactional
    public Category createCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    /**
     * Atualiza os dados de uma categoria existente.
     *
     * - Atualiza o cache da categoria específica.
     * - Invalida o cache da lista completa ('all').
     */
    @CachePut(value = "categories", key = "#result.id")
    @CacheEvict(value = "categories", key = "'all'")
    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category entity = this.getCategoryById(id);

        entity.setName(category.getName());

        return this.categoryRepository.save(entity);
    }

    /**
     * Exclui uma categoria do banco de dados.
     *
     * - Remove o cache da categoria específica.
     * - Invalida o cache da lista completa ('all').
     */
    @Caching(evict = {
        @CacheEvict(value = "categories", key = "#id"),
        @CacheEvict(value = "categories", key = "'all'") })
    @Transactional
    public void deleteCategory(Long id) {
        this.categoryRepository.deleteById(id);
    }

    private void doLongRunningTask() {
        log.info("Query no banco de dados");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.project110.dto.CategoryRequest;
import com.jeanbarcellos.project110.dto.CategoryResponse;
import com.jeanbarcellos.project110.entity.Category;
import com.jeanbarcellos.project110.mapper.CategoryMapper;
import com.jeanbarcellos.project110.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Pattern Write-Through
 *
 * Adicionar ao cache logo na criação
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final String CACHE_NAME = "categories";

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    /**
     * Recupera todas as categorias do banco de dados.
     *
     * - Usa cache para armazenar a lista completa de categorias com a chave 'all'.
     * - Sempre consulta o cache antes de buscar no banco.
     */
    @Cacheable(value = CACHE_NAME, key = "'all'")
    public List<CategoryResponse> getAll() {
        log.info("getAllCategories");

        doLongRunningTask();

        var categories = this.categoryRepository.findAll();

        return this.categoryMapper.toResponseList(categories);
    }

    /**
     * Recupera uma categoria específica pelo ID.
     *
     * - Usa cache para armazenar cada categoria individualmente com a chave baseada
     * no ID.
     * - O cache é preenchido na primeira chamada deste método para um ID
     * específico.
     */
    @Cacheable(value = CACHE_NAME, key = "#id")
    public CategoryResponse getById(Long id) {
        log.info("getCategoryById");

        doLongRunningTask();

        var category = this.findByIdOrThrow(id);

        return this.categoryMapper.toResponse(category);
    }

    /**
     * Cria uma nova categoria no banco de dados.
     *
     * - Adiciona ao cache o produto criado.
     * - Invalida o cache da lista completa ('all').
     */
    @CachePut(value = CACHE_NAME, key = "#result.id")
    @CacheEvict(value = CACHE_NAME, key = "'all'")
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        var category = this.categoryMapper.toEntity(request);

        category = this.categoryRepository.save(category);

        return this.categoryMapper.toResponse(category);
    }

    /**
     * Atualiza os dados de uma categoria existente.
     *
     * - Atualiza o cache da categoria específica.
     * - Invalida o cache da lista completa ('all').
     */
    @CachePut(value = CACHE_NAME, key = "#result.id")
    @CacheEvict(value = CACHE_NAME, key = "'all'")
    @Transactional
    public CategoryResponse update(CategoryRequest request) {
        var category = this.findByIdOrThrow(request.getId());

        this.categoryMapper.copy(category, request);

        category = this.categoryRepository.save(category);

        return this.categoryMapper.toResponse(category);
    }

    /**
     * Exclui uma categoria do banco de dados.
     *
     * - Remove o cache da categoria específica.
     * - Invalida o cache da lista completa ('all').
     */
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#id"),
            @CacheEvict(value = CACHE_NAME, key = "'all'") })
    @Transactional
    public void delete(Long id) {
        this.categoryRepository.deleteById(id);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
    }

    private void doLongRunningTask() {
        log.info("Query no banco de dados");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Category findByIdOrThrow(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

}
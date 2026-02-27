package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.util.ThreadUtils;
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

    private static final String LOG_PREFIX = "[CATEGORY-SERVICE]";

    private static final String MSG_ERROR_CATEGORY_NOT_FOUND = "Category not found: %s";

    private static final String CACHE_NAME = "categories";
    private static final String CACHE_KEY_ALL = "'all'";

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    /**
     * Recupera todas as categorias do banco de dados.
     *
     * - Usa cache para armazenar a lista completa de categorias com a chave 'all'.
     * - Sempre consulta o cache antes de buscar no banco.
     */
    @Cacheable(value = CACHE_NAME, key = CACHE_KEY_ALL)
    public List<CategoryResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entities = this.categoryRepository.findAll();

        return this.categoryMapper.toResponseList(entities);
    }

    /**
     * Recupera uma categoria específica pelo ID.
     *
     * - Usa cache para armazenar cada categoria individualmente com a chave baseada    no ID.
     * - O cache é preenchido na primeira chamada deste método para um ID específico.
     */
    @Cacheable(value = CACHE_NAME, key = "#id")
    public CategoryResponse getById(Long id) {
        log.info("{} getById()", LOG_PREFIX);

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entity = this.findByIdOrThrow(id);

        return this.categoryMapper.toResponse(entity);
    }

    /**
     * Cria uma nova categoria no banco de dados.
     *
     * - Adiciona ao cache o produto criado.
     * - Invalida o cache da lista completa ('all').
     */
    @CachePut(value = CACHE_NAME, key = "#result.id")
    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ALL)
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        log.info("{} create()", LOG_PREFIX);

        var entity = this.categoryMapper.toEntity(request);

        entity = this.categoryRepository.save(entity);

        return this.categoryMapper.toResponse(entity);
    }

    /**
     * Atualiza os dados de uma categoria existente.
     *
     * - Atualiza o cache da categoria específica.
     * - Invalida o cache da lista completa ('all').
     */
    @CachePut(value = CACHE_NAME, key = "#result.id")
    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ALL)
    @Transactional
    public CategoryResponse update(CategoryRequest request) {
        log.info("{} update()", LOG_PREFIX);

        var entity = this.findByIdOrThrow(request.getId());

        this.categoryMapper.copy(entity, request);

        entity = this.categoryRepository.save(entity);

        return this.categoryMapper.toResponse(entity);
    }

    /**
     * Exclui uma categoria do banco de dados.
     *
     * - Remove o cache da categoria específica.
     * - Invalida o cache da lista completa ('all').
     */
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#id"),
            @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ALL) })
    @Transactional
    public void delete(Long id) {
        log.info("{} delete()", LOG_PREFIX);

        this.categoryRepository.deleteById(id);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
    }

    private Category findByIdOrThrow(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_CATEGORY_NOT_FOUND, id)));
    }

}

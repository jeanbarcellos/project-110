package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.util.ThreadUtils;
import com.jeanbarcellos.project110.cache.CategoryCache;
import com.jeanbarcellos.project110.dto.CategoryRequest;
import com.jeanbarcellos.project110.dto.CategoryResponse;
import com.jeanbarcellos.project110.entity.Category;
import com.jeanbarcellos.project110.mapper.CategoryMapper;
import com.jeanbarcellos.project110.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Versao manual do CategoryService, equivalente a estrategia com anotacoes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryManualService {

    private static final String LOG_PREFIX = "[CATEGORY-MANUAL-SERVICE]";

    private static final String MSG_ERROR_CATEGORY_NOT_FOUND = "Category not found: %s";

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final CategoryCache categoryCache;

    /**
     * Equivalente manual de:
     * - @Cacheable(value = "categories", key = "'all'")
     */
    public List<CategoryResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        var cached = this.categoryCache.getAll();
        if (cached != null) {
            return cached;
        }

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entities = this.categoryRepository.findAll();

        var responseList = this.categoryMapper.toResponseList(entities);

        this.categoryCache.putAll(responseList);

        return responseList;
    }

    /**
     * Equivalente manual de:
     * - @Cacheable(value = "categories", key = "#id")
     */
    public CategoryResponse getById(Long id) {
        log.info("{} getById({})", LOG_PREFIX, id);

        var cached = this.categoryCache.getById(id);
        if (cached.isPresent()) {
            return cached.get();
        }

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entity = this.findByIdOrThrow(id);

        var response = this.categoryMapper.toResponse(entity);

        this.categoryCache.put(response);

        return response;
    }

    /**
     * Equivalente manual de:
     * - @CachePut(value = "categories", key = "#request.id")
     * - @CacheEvict(value = "categories", key = "'all'")
     */
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        log.info("{} create()", LOG_PREFIX);

        var entity = this.categoryMapper.toEntity(request);

        entity = this.categoryRepository.save(entity);

        var response = this.categoryMapper.toResponse(entity);

        this.categoryCache.put(response);
        this.categoryCache.evictAll();

        return response;
    }

    /**
     * Equivalente manual de:
     * - @CachePut(value = "categories", key = "#request.id")
     * - @CacheEvict(value = "categories", key = "'all'")
     */
    @Transactional
    public CategoryResponse update(CategoryRequest request) {
        log.info("{} update()", LOG_PREFIX);

        var entity = this.findByIdOrThrow(request.getId());

        this.categoryMapper.copy(entity, request);

        entity = this.categoryRepository.save(entity);

        var response = this.categoryMapper.toResponse(entity);

        this.categoryCache.put(response);
        this.categoryCache.evictAll();

        return response;
    }

    /**
     * Equivalente manual de:
     * - @Caching(evict = {
     *     @CacheEvict(value = "categories", key = "#id"),
     *     @CacheEvict(value = "categories", key = "'all'")
     *   })
     */
    @Transactional
    public void delete(Long id) {
        log.info("{} delete()", LOG_PREFIX);

        this.categoryRepository.deleteById(id);

        this.categoryCache.evictById(id);
        this.categoryCache.evictAll();
    }

    /**
     * Equivalente manual de:
     * - @CacheEvict(value = "categories", allEntries = true)
     */
    public void clearCache() {
        log.info("{} clearCache()", LOG_PREFIX);

        this.categoryCache.clearAllEntries();
    }

    private Category findByIdOrThrow(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_CATEGORY_NOT_FOUND, id)));
    }
}

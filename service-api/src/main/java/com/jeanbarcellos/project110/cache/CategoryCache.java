package com.jeanbarcellos.project110.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.cache.CachePort;
import com.jeanbarcellos.project110.dto.CategoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Infraestrutura manual de cache para categorias.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCache {

    private static final String LOG_PREFIX = "[CATEGORY-CACHE]";

    private static final String CACHE_NAME = "categories";
    private static final String CACHE_KEY_ALL = "all";

    private final CachePort cachePort;

    public List<CategoryResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        return this.cachePort.getList(CACHE_NAME, CACHE_KEY_ALL);
    }

    public void putAll(List<CategoryResponse> categories) {
        int size = categories == null ? 0 : categories.size();

        log.info("{} putAll(size={})", LOG_PREFIX, size);

        this.cachePort.put(CACHE_NAME, CACHE_KEY_ALL, categories);
    }

    public void evictAll() {
        log.info("{} evictAll()", LOG_PREFIX);

        this.cachePort.evict(CACHE_NAME, CACHE_KEY_ALL);
    }

    public Optional<CategoryResponse> getById(Long id) {
        log.info("{} getById({})", LOG_PREFIX, id);

        return this.cachePort.get(CACHE_NAME, id, CategoryResponse.class);
    }

    public void put(CategoryResponse category) {
        if (category == null || category.getId() == null) {
            return;
        }

        log.info("{} put({})", LOG_PREFIX, category.getId());

        this.cachePort.put(CACHE_NAME, category.getId(), category);
    }

    public void evictById(Long id) {
        log.info("{} evictById({})", LOG_PREFIX, id);

        this.cachePort.evict(CACHE_NAME, id);
    }

    public void clearAllEntries() {
        log.info("{} clearAllEntries()", LOG_PREFIX);

        this.cachePort.clear(CACHE_NAME);
    }
}

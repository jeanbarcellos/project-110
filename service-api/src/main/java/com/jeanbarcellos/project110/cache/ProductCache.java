package com.jeanbarcellos.project110.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.cache.CachePort;
import com.jeanbarcellos.project110.dto.ProductResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Infraestrutura manual de cache para produtos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCache {

    private static final String LOG_PREFIX = "[PRODUCT-CACHE]";

    private static final String CACHE_NAME = "products";
    private static final String CACHE_KEY_ALL = "all";

    private final CachePort cachePort;

    public List<ProductResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        return this.cachePort.getList(CACHE_NAME, CACHE_KEY_ALL);
    }

    public void putAll(List<ProductResponse> products) {
        int size = products == null ? 0 : products.size();

        log.info("{} putAll(size={})", LOG_PREFIX, size);

        this.cachePort.put(CACHE_NAME, CACHE_KEY_ALL, products);
    }

    public void evictAll() {
        log.info("{} evictAll()", LOG_PREFIX);

        this.cachePort.evict(CACHE_NAME, CACHE_KEY_ALL);
    }

    public Optional<ProductResponse> getById(Long id) {
        log.info("{} getById({})", LOG_PREFIX, id);

        return this.cachePort.get(CACHE_NAME, id, ProductResponse.class);
    }

    public void put(ProductResponse product) {
        if (product == null || product.getId() == null) {
            return;
        }

        log.info("{} put({})", LOG_PREFIX, product.getId());

        this.cachePort.put(CACHE_NAME, product.getId(), product);
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

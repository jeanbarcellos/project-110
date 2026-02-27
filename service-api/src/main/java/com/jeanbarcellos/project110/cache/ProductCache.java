package com.jeanbarcellos.project110.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

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

    private final CacheManager cacheManager;

    @SuppressWarnings("unchecked")
    public List<ProductResponse> getAll() {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return null;
            }

            log.info("{} getAll()", LOG_PREFIX);
            return cache.get(CACHE_KEY_ALL, List.class);
        } catch (Exception ex) {
            log.warn("{} Falha ao ler lista de produtos do cache.", LOG_PREFIX, ex);
            return null;
        }
    }

    public void putAll(List<ProductResponse> products) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} putAll(size={})", LOG_PREFIX, products == null ? 0 : products.size());
            cache.put(CACHE_KEY_ALL, products);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar lista de produtos no cache.", LOG_PREFIX, ex);
        }
    }

    public void evictAll() {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} evictAll()", LOG_PREFIX);
            cache.evict(CACHE_KEY_ALL);
        } catch (Exception ex) {
            log.warn("{} Falha ao invalidar chave '{}' do cache.", LOG_PREFIX, CACHE_KEY_ALL, ex);
        }
    }

    public Optional<ProductResponse> getById(Long id) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return Optional.empty();
            }

            log.info("{} getById({})", LOG_PREFIX, id);
            return Optional.ofNullable(cache.get(id, ProductResponse.class));
        } catch (Exception ex) {
            log.warn("{} Falha ao ler produto {} do cache.", LOG_PREFIX, id, ex);
            return Optional.empty();
        }
    }

    public void put(ProductResponse product) {
        try {
            if (product == null || product.getId() == null) {
                return;
            }

            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} put({})", LOG_PREFIX, product.getId());
            cache.put(product.getId(), product);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar produto no cache.", LOG_PREFIX, ex);
        }
    }

    public void evictById(Long id) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} evictById({})", LOG_PREFIX, id);
            cache.evict(id);
        } catch (Exception ex) {
            log.warn("{} Falha ao remover produto {} do cache.", LOG_PREFIX, id, ex);
        }
    }

    public void clearAllEntries() {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} clearAllEntries()", LOG_PREFIX);
            cache.clear();
        } catch (Exception ex) {
            log.warn("{} Falha ao limpar cache de produtos.", LOG_PREFIX, ex);
        }
    }

    private Cache getCacheOrNull() {
        try {
            return this.cacheManager.getCache(CACHE_NAME);
        } catch (Exception ex) {
            log.warn("{} Falha ao acessar cache '{}'.", LOG_PREFIX, CACHE_NAME, ex);
            return null;
        }
    }
}

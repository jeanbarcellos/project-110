package com.jeanbarcellos.project110.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

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

    private final CacheManager cacheManager;

    @SuppressWarnings("unchecked")
    public List<CategoryResponse> getAll() {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return null;
            }

            log.info("{} getAll()", LOG_PREFIX);
            return cache.get(CACHE_KEY_ALL, List.class);
        } catch (Exception ex) {
            log.warn("{} Falha ao ler lista de categorias do cache.", LOG_PREFIX, ex);
            return null;
        }
    }

    public void putAll(List<CategoryResponse> categories) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} putAll(size={})", LOG_PREFIX, categories == null ? 0 : categories.size());
            cache.put(CACHE_KEY_ALL, categories);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar lista de categorias no cache.", LOG_PREFIX, ex);
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

    public Optional<CategoryResponse> getById(Long id) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return Optional.empty();
            }

            log.info("{} getById({})", LOG_PREFIX, id);
            return Optional.ofNullable(cache.get(id, CategoryResponse.class));
        } catch (Exception ex) {
            log.warn("{} Falha ao ler categoria {} do cache.", LOG_PREFIX, id, ex);
            return Optional.empty();
        }
    }

    public void put(CategoryResponse category) {
        try {
            if (category == null || category.getId() == null) {
                return;
            }

            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} put({})", LOG_PREFIX, category.getId());
            cache.put(category.getId(), category);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar categoria no cache.", LOG_PREFIX, ex);
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
            log.warn("{} Falha ao remover categoria {} do cache.", LOG_PREFIX, id, ex);
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
            log.warn("{} Falha ao limpar cache de categorias.", LOG_PREFIX, ex);
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

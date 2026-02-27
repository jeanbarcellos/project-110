package com.jeanbarcellos.core.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter padrao da porta de cache usando Spring Cache.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheSpringAdapter implements CachePort {

    private static final String LOG_PREFIX = "[CACHE-SPRING-ADAPTER]";

    private final CacheManager cacheManager;

    @Override
    public <T> Optional<T> get(String cacheName, Object key, Class<T> valueType) {
        try {
            var cache = this.cacheManager.getCache(cacheName);

            if (cache == null) {
                return Optional.empty();
            }

            return Optional.ofNullable(cache.get(key, valueType));
        } catch (Exception ex) {
            log.warn("{} Falha ao ler cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String cacheName, Object key) {
        try {
            var cache = this.cacheManager.getCache(cacheName);

            if (cache == null) {
                return null;
            }

            return cache.get(key, List.class);
        } catch (Exception ex) {
            log.warn("{} Falha ao ler lista cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
            return null;
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        try {
            var cache = this.cacheManager.getCache(cacheName);

            if (cache == null) {
                return;
            }

            cache.put(key, value);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
        }
    }

    @Override
    public void evict(String cacheName, Object key) {
        try {
            var cache = this.cacheManager.getCache(cacheName);

            if (cache == null) {
                return;
            }

            cache.evict(key);
        } catch (Exception ex) {
            log.warn("{} Falha ao remover cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
        }
    }

    @Override
    public void clear(String cacheName) {
        try {
            var cache = this.cacheManager.getCache(cacheName);

            if (cache == null) {
                return;
            }

            cache.clear();
        } catch (Exception ex) {
            log.warn("{} Falha ao limpar cache='{}'.", LOG_PREFIX, cacheName, ex);
        }
    }
}

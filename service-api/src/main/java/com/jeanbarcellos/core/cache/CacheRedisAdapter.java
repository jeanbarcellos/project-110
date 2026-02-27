package com.jeanbarcellos.core.cache;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Adapter de cache baseado em RedisTemplate, independente de Spring Cache.
 */
@Slf4j
@Primary
@Component
public class CacheRedisAdapter implements CachePort {

    private static final String LOG_PREFIX = "[REDIS-CACHE-ADAPTER]";
    private static final String KEY_PREFIX = "manual-cache";
    private static final String SEPARATOR = "::";

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheRedisAdapter(
        @Qualifier("cachePortRedisTemplate")
        RedisTemplate<String, Object> redisTemplate)
    {
        this.redisTemplate = redisTemplate;

        log.info("getKeySerializer: " + redisTemplate.getKeySerializer());
        log.info("getHashKeySerializer: " + redisTemplate.getHashKeySerializer());
        log.info("getValueSerializer: " + redisTemplate.getValueSerializer());
        log.info("getHashValueSerializer: " + redisTemplate.getHashValueSerializer());
    }

    @Override
    public <T> Optional<T> get(String cacheName, Object key, Class<T> valueType) {
        String redisKey = this.buildKey(cacheName, key);

        try {
            Object rawValue = this.redisTemplate.opsForValue().get(redisKey);

            if (rawValue == null) {
                return Optional.empty();
            }

            if (!valueType.isInstance(rawValue)) {
                log.warn(
                        "{} Tipo invalido para cache='{}', key='{}'. Esperado='{}', atual='{}'.",
                        LOG_PREFIX,
                        cacheName,
                        key,
                        valueType.getName(),
                        rawValue.getClass().getName());

                return Optional.empty();
            }

            return Optional.of(valueType.cast(rawValue));
        } catch (Exception ex) {
            log.warn("{} Falha ao ler cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String cacheName, Object key) {
        String redisKey = this.buildKey(cacheName, key);

        try {
            Object rawValue = this.redisTemplate.opsForValue().get(redisKey);

            if (rawValue == null) {
                return null;
            }

            if (!(rawValue instanceof List<?>)) {
                log.warn(
                        "{} Valor nao e lista para cache='{}', key='{}'. Tipo atual='{}'.",
                        LOG_PREFIX,
                        cacheName,
                        key,
                        rawValue.getClass().getName());

                return null;
            }

            return (List<T>) rawValue;
        } catch (Exception ex) {
            log.warn("{} Falha ao ler lista cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
            return null;
        }
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        String redisKey = this.buildKey(cacheName, key);

        try {
            this.redisTemplate.opsForValue().set(redisKey, value);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
        }
    }

    @Override
    public void evict(String cacheName, Object key) {
        String redisKey = this.buildKey(cacheName, key);

        try {
            this.redisTemplate.delete(redisKey);
        } catch (Exception ex) {
            log.warn("{} Falha ao remover cache='{}', key='{}'.", LOG_PREFIX, cacheName, key, ex);
        }
    }

    @Override
    public void clear(String cacheName) {
        String pattern = this.buildPattern(cacheName);

        try {
            Set<String> keys = this.redisTemplate.keys(pattern);

            if (keys == null || keys.isEmpty()) {
                return;
            }

            this.redisTemplate.delete(keys);
        } catch (Exception ex) {
            log.warn("{} Falha ao limpar cache='{}'.", LOG_PREFIX, cacheName, ex);
        }
    }

    private String buildKey(String cacheName, Object key) {
        return KEY_PREFIX + SEPARATOR + cacheName + SEPARATOR + key;
    }

    private String buildPattern(String cacheName) {
        return KEY_PREFIX + SEPARATOR + cacheName + SEPARATOR + "*";
    }
}

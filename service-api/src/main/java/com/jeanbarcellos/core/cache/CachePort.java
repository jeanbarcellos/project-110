package com.jeanbarcellos.core.cache;

import java.util.List;
import java.util.Optional;

/**
 * Porta de acesso a cache para desacoplar a regra de dominio da tecnologia
 * concreta utilizada (Spring Cache, Redis, etc).
 */
public interface CachePort {

    /**
     * Recupera um valor tipado do cache.
     */
    <T> Optional<T> get(String cacheName, Object key, Class<T> valueType);

    /**
     * Recupera uma lista do cache.
     */
    <T> List<T> getList(String cacheName, Object key);

    /**
     * Armazena um valor no cache.
     */
    void put(String cacheName, Object key, Object value);

    /**
     * Remove uma chave especifica do cache.
     */
    void evict(String cacheName, Object key);

    /**
     * Limpa todas as entradas de um cache.
     */
    void clear(String cacheName);
}

package com.jeanbarcellos.project110.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import com.jeanbarcellos.project110.properties.AppConfigProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final AppConfigProperties appConfigProperties;

    /**
     * Aplica os TTLs definidos em {@code app-config.cache.*} no CacheManager
     * Redis usado pelo Spring Cache.
     */
    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var cacheConfig = this.appConfigProperties.getCache();

        // para evitar incompatibilidade de classloader com devtools + Redis cache.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig(classLoader)
                .entryTtl(cacheConfig.getDefault().getTtl())
                .disableCachingNullValues();

        return builder -> builder
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration(
                        cacheConfig.getCategories().getName(),
                        defaultConfig.entryTtl(cacheConfig.getCategories().getTtl()))
                .withCacheConfiguration(
                        cacheConfig.getProducts().getName(),
                        defaultConfig.entryTtl(cacheConfig.getProducts().getTtl()))
                .withCacheConfiguration(
                        cacheConfig.getPersons().getName(),
                        defaultConfig.entryTtl(cacheConfig.getPersons().getTtl()));
    }
}

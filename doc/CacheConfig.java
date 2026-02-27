package com.jeanbarcellos.project110.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class CacheConfig {

    private static final int CACHE_DEFAULT_TTL = 1;

    private static final String CACHE_CATEGORIES_NAME = "categories";
    private static final int CACHE_CATEGORIES_TTL = 24;

    private static final String CACHE_PRODUCTS_NAME = "products";
    private static final int CACHE_PRODUCTS_TTL = 16;

    private static final String CACHE_PERSONS_NAME = "persons";
    private static final int CACHE_PERSONS_TTL = 8;

    @Bean
    CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // Configuração padrão para todos os caches
        var defaultSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());

        var defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                // .serializeKeysWith(fromSerializer(new StringRedisSerializer())) // Serialização das Keys
                .serializeValuesWith(fromSerializer(defaultSerializer)) // Serialização dos valores
                .entryTtl(Duration.ofHours(CACHE_DEFAULT_TTL)); // TTL padrão de 1 hora

        // Configurações específicas para cada cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CACHE_CATEGORIES_NAME,
                defaultCacheConfig.entryTtl(Duration.ofHours(CACHE_CATEGORIES_TTL)));

        cacheConfigurations.put(CACHE_PRODUCTS_NAME,
                defaultCacheConfig.entryTtl(Duration.ofHours(CACHE_PRODUCTS_TTL)));

        cacheConfigurations.put(CACHE_PERSONS_NAME,
                defaultCacheConfig.entryTtl(Duration.ofHours(CACHE_PERSONS_TTL)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    SimpleCacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }

    // @Bean
    ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    // ObjectMapper para o Redis (com informações de tipo)
    // @Bean("redisObjectMapper")
    ObjectMapper redisObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Adiciona informações de tipo ao JSON
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        return objectMapper;
    }
}

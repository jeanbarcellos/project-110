package com.jeanbarcellos.project110.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.jeanbarcellos.project110.properties.AppConfigProperties;

/**
 * Configuracao de RedisTemplate dedicado ao CachePort.
 */
@Configuration
public class CacheRedisAdapterConfig {

    /**
     * ConnectionFactory dedicada ao CachePort, separada da conexao principal usada pelo Spring Cache.
     */
    @Bean(name = "cachePortRedisConnectionFactory", autowireCandidate = false)
    RedisConnectionFactory cachePortRedisConnectionFactory(AppConfigProperties appConfigProperties) {
        var redis = appConfigProperties.getCache().getRedis();

        var redisStandalone = new RedisStandaloneConfiguration();
        redisStandalone.setHostName(redis.getHost());
        redisStandalone.setPort(redis.getPort());
        redisStandalone.setDatabase(redis.getDatabase());

        if (!redis.getPassword().isBlank()) {
            redisStandalone.setPassword(redis.getPassword());
        }

        var clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(redis.getTimeout())
                .build();

        return new LettuceConnectionFactory(redisStandalone, clientConfig);
    }

    /**
     * RedisTemplate exclusivo do RedisCacheAdapter para evitar dependencia do template padrao criado pelo Spring Boot.
     */
    @Bean("cachePortRedisTemplate")
    RedisTemplate<String, Object> cachePortRedisTemplate(AppConfigProperties appConfigProperties) {
        var connectionFactory = this.cachePortRedisConnectionFactory(appConfigProperties);

        var keySerializer = new StringRedisSerializer();
        var valueSerializer = new GenericJackson2JsonRedisSerializer();

        var template = new RedisTemplate<String, Object>();

        template.setConnectionFactory(connectionFactory);

        // template.setKeySerializer(keySerializer);
        // template.setHashKeySerializer(keySerializer);

        // template.setValueSerializer(valueSerializer);
        // template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;
    }
}

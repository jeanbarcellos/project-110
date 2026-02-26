package com.jeanbarcellos.project110.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    // @Bean
    RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    // @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(redisConnectionFactory());
        return t;
    }
}
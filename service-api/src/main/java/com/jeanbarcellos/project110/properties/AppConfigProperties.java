package com.jeanbarcellos.project110.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Mapeia as propriedades customizadas de {@code app-config} do application.yml.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app-config")
public class AppConfigProperties {

    private String name = "project110";

    private String description = "Spring Boot + Cache";

    private String version = "0.0.1-SNAPSHOT";

    private Contact contact = new Contact();

    private Cache cache = new Cache();

    @Getter
    @Setter
    public static class Contact {
        private String name = null;
        private String url = null;
        private String email = null;
    }

    @Getter
    @Setter
    public static class Cache {

        /**
         * Campo de apoio para mapear a chave YAML "default".
         */
        private CacheDetails defaultCache = new CacheDetails("default", Duration.ofHours(1));

        private CacheDetails categories = new CacheDetails("categories", Duration.ofHours(24));

        private CacheDetails products = new CacheDetails("products", Duration.ofHours(16));

        private CacheDetails persons = new CacheDetails("persons", Duration.ofMinutes(10));

        public CacheDetails getDefault() {
            return defaultCache;
        }

        public void setDefault(CacheDetails cacheDetails) {
            this.defaultCache = cacheDetails;
        }
    }

    @Getter
    @Setter
    public static class CacheDetails {

        private String name;

        private Duration ttl;

        public CacheDetails() {
        }

        public CacheDetails(String name, Duration ttl) {
            this.name = name;
            this.ttl = ttl;
        }
    }
}

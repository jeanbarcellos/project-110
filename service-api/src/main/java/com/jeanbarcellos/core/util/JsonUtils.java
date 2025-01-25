package com.jeanbarcellos.core.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe utilitária para manipulação de JSON
 *
 * @author Jean Silva de Barcellos
 */
@Slf4j
public class JsonUtils {

    private static final String ERROR = "Erro nanipulação do JSON/Objeto";
    public static final String JSON_LIST_PREFIX = "[";

    private JsonUtils() {
    }

    private static ObjectMapper getObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static String toJson(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(ERROR, e);
        }
    }

    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return getObjectMapper().readValue(json, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(ERROR, e);
        }
    }

    public static boolean checkIsCollection(String str) {
        return str.trim().startsWith(JsonUtils.JSON_LIST_PREFIX);
    }

}
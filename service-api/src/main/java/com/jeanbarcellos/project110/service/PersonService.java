package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.core.util.ThreadUtils;
import com.jeanbarcellos.project110.cache.PersonCache;
import com.jeanbarcellos.project110.dto.PersonRequest;
import com.jeanbarcellos.project110.dto.PersonResponse;
import com.jeanbarcellos.project110.entity.Person;
import com.jeanbarcellos.project110.mapper.PersonMapper;
import com.jeanbarcellos.project110.repository.PersonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service de pessoas com regra de negocio separada da infraestrutura de cache.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final String LOG_PREFIX = "[PERSON-SERVICE]";

    private static final String MSG_ERROR_PERSON_NOT_FOUND = "Person not found: %s";

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PersonCache personCache;

    /**
     * Recupera todas as pessoas.
     *
     * - Primeiro tenta retornar a lista do cache.
     * - Em caso de cache vazio/indisponivel,
     * - Busca no banco e atualiza o cache.
     */
    public List<PersonResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        var cachedEntities = this.personCache.getAll();

        if (cachedEntities != null) {
            return cachedEntities;
        }

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        log.info("{} personRepository.findAll()", LOG_PREFIX);
        var entities = this.personRepository.findAll();

        var responseList = this.personMapper.toResponseList(entities);

        this.personCache.putAll(responseList);

        return responseList;
    }

    /**
     * Recupera uma pessoa pelo ID.
     *
     * - Usa cache manual com a chave baseada no ID.
     */
    public PersonResponse getById(Long id) {
        log.info("{} getById()", LOG_PREFIX);

        var cachedEntity = this.personCache.getById(id);

        if (cachedEntity.isPresent()) {
            return cachedEntity.get();
        }

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entity = this.findByIdOrThrow(id);

        var response = this.personMapper.toResponse(entity);

        this.personCache.put(response);

        return response;
    }

    /**
     * Cria uma nova pessoa.
     *
     * - Atualiza o cache da lista completa e insere a pessoa individualmente.
     */
    public PersonResponse create(PersonRequest request) {
        log.info("{} create()", LOG_PREFIX);

        var entity = this.personMapper.toEntity(request);

        entity = this.personRepository.save(entity);

        var response = this.personMapper.toResponse(entity);

        this.personCache.put(response);
        this.personCache.evictAll();

        return response;
    }

    /**
     * Atualiza uma pessoa existente.
     *
     * - Atualiza o cache da pessoa específica e da lista completa.
     */
    public PersonResponse update(PersonRequest request) {
        log.info("{} update()", LOG_PREFIX);

        var entity = this.findByIdOrThrow(request.getId());

        this.personMapper.copy(entity, request);

        entity = this.personRepository.save(entity);

        var response = this.personMapper.toResponse(entity);

        this.personCache.put(response);
        this.personCache.evictAll();

        return response;
    }

    /**
     * Exclui uma pessoa.
     *
     * - Atualiza o cache da lista completa e remove a pessoa específica do cache.
     */
    public void delete(Long id) {
        log.info("{} delete()", LOG_PREFIX);

        this.personRepository.deleteById(id);

        this.personCache.evictById(id);
        this.personCache.evictAll();
    }

    public void clearCache() {
        log.info("{} clearCache()", LOG_PREFIX);

        this.personCache.clearAllEntries();
    }

    private Person findByIdOrThrow(Long id) {
        log.info("{} personRepository.findById({})", LOG_PREFIX, id);
        return this.personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_PERSON_NOT_FOUND, id)));
    }

}

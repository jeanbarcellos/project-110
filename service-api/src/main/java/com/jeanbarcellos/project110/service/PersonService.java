package com.jeanbarcellos.project110.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.jeanbarcellos.core.util.ThreadUtils;
import com.jeanbarcellos.project110.dto.PersonRequest;
import com.jeanbarcellos.project110.dto.PersonResponse;
import com.jeanbarcellos.project110.entity.Person;
import com.jeanbarcellos.project110.mapper.PersonMapper;
import com.jeanbarcellos.project110.repository.PersonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Testar cache manual
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private static final String MSG_ERROR_PERSON_NOT_FOUND = "Person not found: %s";

    private static final String CACHE_NAME = "persons";
    private static final String CACHE_KEY_ALL = "all";

    private static final int DB_DELAY = 1000;

    private final CacheManager cacheManager;

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    /**
     * Recupera todas as pessoas.
     *
     * Usa cache manual com a chave 'all'.
     */
    public List<PersonResponse> getAll() {
        log.info("PersonService.getAll()");

        var entities = this.getAllPersonsFromCache();

        if (entities != null) {
            return personMapper.toResponseList(entities);
        }

        log.info("Query no banco de dados");
        ThreadUtils.delay(DB_DELAY);

        entities = this.personRepository.findAll();
        log.info("personRepository.findAll()");

        this.setPersonsToCache(entities);

        return this.personMapper.toResponseList(entities);
    }

    /**
     * Recupera uma pessoa pelo ID.
     *
     * Usa cache manual com a chave baseada no ID.
     */
    public PersonResponse getById(Long id) {
        var entity = getPersonFromCache(id);

        if (entity != null) {
            return this.personMapper.toResponse(entity);
        }

        log.info("Query no banco de dados");
        ThreadUtils.delay(3000);

        entity = this.findByIdOrThrow(id);

        this.addPersonToCache(entity);

        return this.personMapper.toResponse(entity);
    }

    /**
     * Cria uma nova pessoa.
     *
     * Atualiza o cache da lista completa e insere a pessoa individualmente.
     */
    public PersonResponse create(PersonRequest request) {
        var entity = this.personMapper.toEntity(request);

        entity = this.personRepository.save(entity);

        this.updateAllPersonsCache();
        this.addPersonToCache(entity);

        return this.personMapper.toResponse(entity);
    }

    /**
     * Atualiza uma pessoa existente.
     *
     * Atualiza o cache da pessoa específica e da lista completa.
     */
    public PersonResponse update(PersonRequest request) {
        var entity = this.findByIdOrThrow(request.getId());

        this.personMapper.copy(entity, request);

        entity = this.personRepository.save(entity);

        this.updateAllPersonsCache();
        this.addPersonToCache(entity);

        return this.personMapper.toResponse(entity);
    }

    /**
     * Exclui uma pessoa.
     *
     * Atualiza o cache da lista completa e remove a pessoa específica do cache.
     */
    public void delete(Long id) {
        personRepository.deleteById(id);

        this.updateAllPersonsCache();
        this.removePersonFromCache(id);
    }

    private Person findByIdOrThrow(Long id) {
        log.info("personRepository.findById({})", id);
        return this.personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_PERSON_NOT_FOUND, id)));
    }

    // ----

    /**
     * Atualiza o cache da lista completa de pessoas ('all').
     */
    private void updateAllPersonsCache() {
        log.info("updateAllPersonsCache()");
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            List<Person> entities = personRepository.findAll();
            log.info("personRepository.findAll()");
            cache.put(CACHE_KEY_ALL, entities);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Person> getAllPersonsFromCache() {
        List<Person> entities = new ArrayList<>();

        Cache cache = cacheManager.getCache(CACHE_NAME);

        if (cache != null) {
            entities = cache.get(CACHE_KEY_ALL, List.class);
            log.info("getAllPersonsFromCache()");
        }

        return entities;
    }

    private Person getPersonFromCache(Long id) {
        log.info("getPersonFromCache({})", id);

        Cache cache = cacheManager.getCache(CACHE_NAME);
        Person entity = null;

        if (cache != null) {
            entity = cache.get(id, Person.class);
        }

        return entity;
    }

    /**
     * Adiciona uma pessoa específica ao cache.
     */
    private void addPersonToCache(Person entity) {
        log.info("addPersonToCache({})", entity.getId());

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(entity.getId(), entity);
        }
    }

    /**
     * Remove uma pessoa específica do cache.
     */
    private void removePersonFromCache(Long id) {
        log.info("removePersonFromCache({})", id);

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.evict(id);
        }
    }

    private void setPersonsToCache(List<Person> entities) {
        log.info("setPersonsToCache(entities)");

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(CACHE_KEY_ALL, entities);
        }
    }
}
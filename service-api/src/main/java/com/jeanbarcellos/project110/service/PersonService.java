package com.jeanbarcellos.project110.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.jeanbarcellos.project110.dto.PersonRequest;
import com.jeanbarcellos.project110.dto.PersonResponse;
import com.jeanbarcellos.project110.entity.Person;
import com.jeanbarcellos.project110.mapper.PersonMapper;
import com.jeanbarcellos.project110.repository.PersonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Testar cache manual
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PersonService {

    private static final String MSG_ERROR_PERSON_NOT_FOUND = "Person not found: %s";

    private static final String CACHE_NAME = "persons";
    private static final String CACHE_KEY_ALL = "all";

    private final CacheManager cacheManager;

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    /**
     * Recupera todas as pessoas.
     *
     * Usa cache manual com a chave 'all'.
     */
    public List<PersonResponse> getAll() {
        var persons = getAllPersonsFromCache();

        if (persons != null) {
            return personMapper.toResponseList(persons);
        }

        doLongRunningTask();

        persons = this.personRepository.findAll();
        log.info("personRepository.findAll()");

        setPersonsToCache(persons);

        return this.personMapper.toResponseList(persons);
    }

    /**
     * Recupera uma pessoa pelo ID.
     *
     * Usa cache manual com a chave baseada no ID.
     */
    public PersonResponse getById(Long id) {
        var person = getPersonFromCache(id);

        if (person != null) {
            return this.personMapper.toResponse(person);
        }

        doLongRunningTask();

        person = this.findByIdOrThrow(id);

        addPersonToCache(person);

        return this.personMapper.toResponse(person);
    }

    /**
     * Cria uma nova pessoa.
     *
     * Atualiza o cache da lista completa e insere a pessoa individualmente.
     */
    public PersonResponse create(PersonRequest request) {
        var person = this.personMapper.toEntity(request);

        person = this.personRepository.save(person);

        updateAllPersonsCache();
        addPersonToCache(person);

        return this.personMapper.toResponse(person);
    }

    /**
     * Atualiza uma pessoa existente.
     *
     * Atualiza o cache da pessoa específica e da lista completa.
     */
    public PersonResponse update(PersonRequest request) {
        var person = this.findByIdOrThrow(request.getId());

        this.personMapper.copy(person, request);

        person = this.personRepository.save(person);

        updateAllPersonsCache();
        addPersonToCache(person);

        return this.personMapper.toResponse(person);
    }

    /**
     * Exclui uma pessoa.
     *
     * Atualiza o cache da lista completa e remove a pessoa específica do cache.
     */
    public void delete(Long id) {
        personRepository.deleteById(id);

        updateAllPersonsCache();
        removePersonFromCache(id);
    }

    private Person findByIdOrThrow(Long id) {
        log.info("personRepository.findById({})", id);
        return this.personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_PERSON_NOT_FOUND, id)));
    }

    private void doLongRunningTask() {
        log.info("Query no banco de dados");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ----

    /**
     * Atualiza o cache da lista completa de pessoas ('all').
     */
    private void updateAllPersonsCache() {
        log.info("updateAllPersonsCache()");
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            List<Person> persons = personRepository.findAll();
            log.info("personRepository.findAll()");
            cache.put(CACHE_KEY_ALL, persons);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Person> getAllPersonsFromCache() {
        List<Person> persons = new ArrayList<>();

        Cache cache = cacheManager.getCache(CACHE_NAME);

        if (cache != null) {
            persons = cache.get(CACHE_KEY_ALL, List.class);
            log.info("getAllPersonsFromCache()");
        }

        return persons;
    }

    private Person getPersonFromCache(Long id) {
        log.info("getPersonFromCache({})", id);

        Cache cache = cacheManager.getCache(CACHE_NAME);
        Person person = null;

        if (cache != null) {
            person = cache.get(id, Person.class);
        }

        return person;
    }

    /**
     * Adiciona uma pessoa específica ao cache.
     */
    private void addPersonToCache(Person person) {
        log.info("addPersonToCache({})", person.getId());

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(person.getId(), person);
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

    private void setPersonsToCache(List<Person> persons) {
        log.info("setPersonsToCache(persons)");

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(CACHE_KEY_ALL, persons);
        }
    }
}
package com.jeanbarcellos.project110.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.jeanbarcellos.project110.entity.Person;
import com.jeanbarcellos.project110.repository.PersonRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersonService {

    private static final String CACHE_NAME = "persons";
    private static final String CACHE_KEY_ALL = "all";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CacheManager cacheManager;

    /**
     * Recupera todas as pessoas.
     *
     * Usa cache manual com a chave 'all'.
     */
    public List<Person> getAllPersons() {
        List<Person> persons = getAllPersonsFromCache();

        if (persons != null) {
            return persons;
        }

        persons = personRepository.findAll();
        log.info("personRepository.findAll()");

        setPersonsToCache(persons);

        return persons;
    }

    /**
     * Recupera uma pessoa pelo ID.
     *
     * Usa cache manual com a chave baseada no ID.
     */
    public Person getPersonById(Long id) {
        Person person = getPersonFromCache(id);

        if (person != null) {
            return person;
        }

        if (person == null) {
            log.info("personRepository.findById({})", id);
            person = personRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Person not found"));

            addPersonToCache(person);
        }

        return person;
    }

    /**
     * Cria uma nova pessoa.
     *
     * Atualiza o cache da lista completa e insere a pessoa individualmente.
     */
    public Person createPerson(Person person) {
        Person createdPerson = personRepository.save(person);

        updateAllPersonsCache();
        addPersonToCache(createdPerson);

        return createdPerson;
    }

    /**
     * Atualiza uma pessoa existente.
     *
     * Atualiza o cache da pessoa específica e da lista completa.
     */
    public Person updatePerson(Long id, Person person) {
        Person existingPerson = getPersonById(id);

        existingPerson.setName(person.getName());
        existingPerson.setBirthDate(person.getBirthDate());

        Person updatedPerson = personRepository.save(existingPerson);

        updateAllPersonsCache();
        addPersonToCache(updatedPerson);

        return updatedPerson;
    }

    /**
     * Exclui uma pessoa.
     *
     * Atualiza o cache da lista completa e remove a pessoa específica do cache.
     */
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) {
            throw new RuntimeException("Person not found");
        }
        personRepository.deleteById(id);

        updateAllPersonsCache();
        removePersonFromCache(id);
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

    // ----


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
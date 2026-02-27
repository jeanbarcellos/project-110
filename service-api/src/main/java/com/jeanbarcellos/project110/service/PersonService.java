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

    private static final String MSG_ERROR_PERSON_NOT_FOUND = "Person not found: %s";

    private static final int DB_DELAY = 1000;

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PersonCache personCache;

    /**
     * Recupera todas as pessoas.
     *
     * Primeiro tenta retornar a lista do cache. Em caso de cache
     * vazio/indisponivel,
     * busca no banco e atualiza o cache.
     */
    public List<PersonResponse> getAll() {
        log.info("PersonService.getAll()");

        var cachedPersons = this.personCache.getAll();

        if (cachedPersons != null) {
            return cachedPersons;
        }

        log.info("Query no banco de dados");
        ThreadUtils.delay(DB_DELAY);

        log.info("personRepository.findAll()");
        var entities = this.personRepository.findAll();

        var responseList = this.personMapper.toResponseList(entities);

        this.personCache.putAll(responseList);

        return responseList;
    }

    /**
     * Recupera uma pessoa pelo ID.
     *
     * Usa cache manual com a chave baseada no ID.
     */
    public PersonResponse getById(Long id) {
        var cachedPerson = this.personCache.getById(id);

        if (cachedPerson.isPresent()) {
            return cachedPerson.get();
        }

        log.info("Query no banco de dados");
        ThreadUtils.delay(DB_DELAY);

        var entity = this.findByIdOrThrow(id);

        var response = this.personMapper.toResponse(entity);

        this.personCache.put(response);

        return response;
    }

    /**
     * Cria uma nova pessoa.
     *
     * Atualiza o cache da lista completa e insere a pessoa individualmente.
     */
    public PersonResponse create(PersonRequest request) {
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
     * Atualiza o cache da pessoa específica e da lista completa.
     */
    public PersonResponse update(PersonRequest request) {
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
     * Atualiza o cache da lista completa e remove a pessoa específica do cache.
     */
    public void delete(Long id) {
        this.personRepository.deleteById(id);

        this.personCache.evictById(id);
        this.personCache.evictAll();
    }

    private Person findByIdOrThrow(Long id) {
        log.info("personRepository.findById({})", id);
        return this.personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_PERSON_NOT_FOUND, id)));
    }

}

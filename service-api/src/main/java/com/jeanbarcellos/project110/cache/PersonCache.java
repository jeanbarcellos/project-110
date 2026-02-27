package com.jeanbarcellos.project110.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jeanbarcellos.core.cache.CachePort;
import com.jeanbarcellos.project110.dto.PersonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente responsavel por toda a infraestrutura de cache de
 * {@link PersonResponse}.
 *
 * Esta classe e defensiva: nenhuma operacao de cache deve interromper o fluxo
 * principal da aplicacao. Por isso, excecoes sao tratadas internamente e apenas
 * registradas em log.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonCache {

    private static final String LOG_PREFIX = "[PERSON-CACHE]";

    private static final String CACHE_NAME = "persons";
    private static final String CACHE_KEY_ALL = "all";

    private final CachePort cachePort;

    /**
     * Recupera do cache a lista completa de pessoas (chave {@code all}).
     *
     * @return lista em cache ou {@code null} quando nao houver valor/caso de erro.
     */
    public List<PersonResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        return this.cachePort.getList(CACHE_NAME, CACHE_KEY_ALL);
    }

    /**
     * Armazena no cache a lista completa de pessoas.
     *
     * @param persons lista que deve ficar associada a chave {@code all}.
     */
    public void putAll(List<PersonResponse> persons) {
        int size = persons == null ? 0 : persons.size();

        log.info("{} putAll(size={})", LOG_PREFIX, size);

        this.cachePort.put(CACHE_NAME, CACHE_KEY_ALL, persons);
    }

    /**
     * Invalida a chave {@code all}, forcando recarga da listagem na proxima
     * consulta.
     */
    public void evictAll() {
        log.info("{} evictAll()", LOG_PREFIX);

        this.cachePort.evict(CACHE_NAME, CACHE_KEY_ALL);
    }

    /**
     * Recupera uma pessoa do cache usando o ID como chave.
     *
     * @param id identificador da pessoa.
     * @return {@link Optional} com pessoa em cache; vazio quando nao houver valor
     *         ou em caso de erro.
     */
    public Optional<PersonResponse> getById(Long id) {
        log.info("{} getById({})", LOG_PREFIX, id);

        return this.cachePort.get(CACHE_NAME, id, PersonResponse.class);
    }

    /**
     * Armazena/atualiza uma pessoa no cache usando o ID como chave.
     *
     * @param person dto de resposta persistido.
     */
    public void put(PersonResponse person) {
        if (person == null || person.getId() == null) {
            return;
        }

        log.info("{} put({})", LOG_PREFIX, person.getId());

        this.cachePort.put(CACHE_NAME, person.getId(), person);
    }

    /**
     * Remove do cache uma pessoa especifica pelo ID.
     *
     * @param id identificador da pessoa que deve ser removida.
     */
    public void evictById(Long id) {
        log.info("{} evictById({})", LOG_PREFIX, id);

        this.cachePort.evict(CACHE_NAME, id);
    }

    /**
     * Limpa todas as entradas do cache de pessoas.
     */
    public void clearAllEntries() {
        log.info("{} clearAllEntries()", LOG_PREFIX);

        this.cachePort.clear(CACHE_NAME);
    }
}

package com.jeanbarcellos.project110.cache;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

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

    /**
     * TTL padrao do cache de pessoas: 10 minutos.
     */
    public static final Duration TTL = Duration.ofMinutes(10);

    private final CacheManager cacheManager;

    /**
     * Recupera do cache a lista completa de pessoas (chave {@code all}).
     *
     * @return lista em cache ou {@code null} quando nao houver valor/caso de erro.
     */
    @SuppressWarnings("unchecked")
    public List<PersonResponse> getAll() {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return null;
            }

            log.info("{} getAll()", LOG_PREFIX);

            return cache.get(CACHE_KEY_ALL, List.class);
        } catch (Exception ex) {
            log.warn("{} Falha ao ler lista de pessoas do cache.", LOG_PREFIX, ex);
            return null;
        }
    }

    /**
     * Armazena no cache a lista completa de pessoas.
     *
     * @param persons lista que deve ficar associada a chave {@code all}.
     */
    public void putAll(List<PersonResponse> persons) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            int size = persons == null ? 0 : persons.size();

            log.info("{} putAll(size={})", LOG_PREFIX, size);

            cache.put(CACHE_KEY_ALL, persons);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar lista de pessoas no cache.", LOG_PREFIX, ex);
        }
    }

    /**
     * Invalida a chave {@code all}, forcando recarga da listagem na proxima
     * consulta.
     */
    public void evictAll() {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} evictAll()", LOG_PREFIX);

            cache.evict(CACHE_KEY_ALL);
        } catch (Exception ex) {
            log.warn("{} Falha ao invalidar chave '{}' do cache.", LOG_PREFIX, CACHE_KEY_ALL, ex);
        }
    }

    /**
     * Recupera uma pessoa do cache usando o ID como chave.
     *
     * @param id identificador da pessoa.
     * @return {@link Optional} com pessoa em cache; vazio quando nao houver valor
     *         ou em caso de erro.
     */
    public Optional<PersonResponse> getById(Long id) {
        try {
            var cache = this.getCacheOrNull();

            if (cache == null) {
                return Optional.empty();
            }

            log.info("{} getById({})", LOG_PREFIX, id);

            return Optional.ofNullable(cache.get(id, PersonResponse.class));
        } catch (Exception ex) {
            log.warn("{} Falha ao ler pessoa {} do cache.", LOG_PREFIX, id, ex);
            return Optional.empty();
        }
    }

    /**
     * Armazena/atualiza uma pessoa no cache usando o ID como chave.
     *
     * @param person dto de resposta persistido.
     */
    public void put(PersonResponse person) {
        try {
            if (person == null || person.getId() == null) {
                return;
            }

            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} put({})", LOG_PREFIX, person.getId());
            cache.put(person.getId(), person);
        } catch (Exception ex) {
            log.warn("{} Falha ao gravar pessoa no cache.", LOG_PREFIX, ex);
        }
    }

    /**
     * Remove do cache uma pessoa especifica pelo ID.
     *
     * @param id identificador da pessoa que deve ser removida.
     */
    public void evictById(Long id) {
        try {
            var cache = this.getCacheOrNull();
            if (cache == null) {
                return;
            }

            log.info("{} evictById({})", LOG_PREFIX, id);

            cache.evict(id);
        } catch (Exception ex) {
            log.warn("{} Falha ao remover pessoa {} do cache.", LOG_PREFIX, id, ex);
        }
    }

    /**
     * Recupera a instancia do cache configurado para pessoas.
     *
     * @return cache de pessoas ou {@code null} quando indisponivel.
     */
    private Cache getCacheOrNull() {
        try {
            return this.cacheManager.getCache(CACHE_NAME);
        } catch (Exception ex) {
            log.warn("{} Falha ao acessar cache '{}'.", LOG_PREFIX, CACHE_NAME, ex);
            return null;
        }
    }
}

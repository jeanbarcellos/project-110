package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.util.ThreadUtils;
import com.jeanbarcellos.project110.dto.ProductRequest;
import com.jeanbarcellos.project110.dto.ProductResponse;
import com.jeanbarcellos.project110.entity.Product;
import com.jeanbarcellos.project110.mapper.ProductMapper;
import com.jeanbarcellos.project110.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Pattern Lazy Loading
 *
 * Adicionar ao cache quando solicidao
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String MSG_ERROR_PERSON_NOT_FOUND = "Product not found: %s";

    private static final String CACHE_NAME = "products";
    private static final String CACHE_KEY_ALL = "'all'";

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    /**
     * Recupera todos os produtos do banco de dados.
     *
     * - Usa cache para armazenar a lista completa de produtos com a chave 'all'.
     * - O cache só é preenchido quando este método é chamado pela primeira vez.
     * - Se o cache for inválido, os dados serão recarregados do banco.
     */
    @Cacheable(value = CACHE_NAME, key = CACHE_KEY_ALL)
    public List<ProductResponse> getCacheKeyAll() {
        log.info("ProductService.getAll()");

        log.info("Query no banco de dados");
        ThreadUtils.delay(3000);

        var categories = this.productRepository.findAll();

        return this.productMapper.toResponseList(categories);
    }

    /**
     * Recupera um produto específico pelo ID.
     *
     * - Usa cache para armazenar cada produto individualmente com a chave baseada no ID.
     * - O cache só é preenchido na primeira chamada deste método para um ID específico.
     */
    @Cacheable(value = CACHE_NAME, key = "#id")
    public ProductResponse getById(Long id) {
        log.info("ProductService.getById()");

        log.info("Query no banco de dados");
        ThreadUtils.delay(3000);

        var product = findByIdOrThrow(id);

        return this.productMapper.toResponse(product);
    }

    /**
     * Cria um novo produto no banco de dados.
     *
     * - Remove o cache da lista completa ('all') para garantir que ela seja recarregada na próxima consulta.
     */
    @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ALL)
    @Transactional
    public ProductResponse create(ProductRequest request) {
        var product = this.productMapper.toEntity(request);

        product = this.productRepository.save(product);

        return this.productMapper.toResponse(product);
    }

    /**
     * Atualiza os dados de um produto existente.
     *
     * - Remove o cache do produto específico.
     * - Remove o cache da lista completa ('all') para garantir que os dados estejam atualizados na próxima consulta.
     */
    @Caching(evict = {
        @CacheEvict(value = CACHE_NAME, key = "#result.id"),
        @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ALL) })
    @Transactional
    public ProductResponse update(ProductRequest request) {
        var product = this.findByIdOrThrow(request.getId());

        this.productMapper.copy(product, request);

        product = this.productRepository.save(product);

        return this.productMapper.toResponse(product);
    }

    /**
     * Exclui um produto do banco de dados.
     *
     * - Remove o cache do produto específico.
     * - Remove o cache da lista completa ('all') para garantir que os dados estejam atualizados na próxima consulta.
     */
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#id"),
            @CacheEvict(value = CACHE_NAME, key = CACHE_KEY_ALL) })
    @Transactional
    public void delete(Long id) {
        this.productRepository.deleteById(id);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void clearCache() {
    }

    private Product findByIdOrThrow(Long id) {
        return this.productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_PERSON_NOT_FOUND, id)));
    }

}
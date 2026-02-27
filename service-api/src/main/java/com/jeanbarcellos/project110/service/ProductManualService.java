package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeanbarcellos.core.util.ThreadUtils;
import com.jeanbarcellos.project110.cache.ProductCache;
import com.jeanbarcellos.project110.dto.ProductRequest;
import com.jeanbarcellos.project110.dto.ProductResponse;
import com.jeanbarcellos.project110.entity.Product;
import com.jeanbarcellos.project110.mapper.ProductMapper;
import com.jeanbarcellos.project110.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Versao manual do ProductService, equivalente a estrategia com anotacoes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductManualService {

    private static final String LOG_PREFIX = "[PRODUCT-MANUAL-SERVICE]";

    private static final String MSG_ERROR_PRODUCT_NOT_FOUND = "Product not found: %s";

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ProductCache productCache;

    /**
     * Equivalente manual de:
     * - @Cacheable(value = "products", key = "'all'")
     */
    public List<ProductResponse> getAll() {
        log.info("{} getAll()", LOG_PREFIX);

        var cached = this.productCache.getAll();
        if (cached != null) {
            return cached;
        }

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entities = this.productRepository.findAll();

        var responseList = this.productMapper.toResponseList(entities);

        this.productCache.putAll(responseList);

        return responseList;
    }

    /**
     * Equivalente manual de:
     * - @Cacheable(value = "products", key = "#id")
     */
    public ProductResponse getById(Long id) {
        log.info("{} getById({})", LOG_PREFIX, id);

        var cached = this.productCache.getById(id);
        if (cached.isPresent()) {
            return cached.get();
        }

        log.info("{} Query no banco de dados", LOG_PREFIX);
        ThreadUtils.delay();

        var entity = this.findByIdOrThrow(id);

        var response = this.productMapper.toResponse(entity);

        this.productCache.put(response);

        return response;
    }

    /**
     * Equivalente manual de:
     * - @CacheEvict(value = "products", key = "'all'")
     */
    @Transactional
    public ProductResponse create(ProductRequest request) {
        log.info("{} create()", LOG_PREFIX);

        var entity = this.productMapper.toEntity(request);

        entity = this.productRepository.save(entity);

        var response = this.productMapper.toResponse(entity);

        this.productCache.evictAll();

        return response;
    }

    /**
     * Equivalente manual de:
     * - @Caching(evict = {
     *     @CacheEvict(value = "products", key = "#request.id"),
     *     @CacheEvict(value = "products", key = "'all'")
     *   })
     */
    @Transactional
    public ProductResponse update(ProductRequest request) {
        log.info("{} update()", LOG_PREFIX);

        var entity = this.findByIdOrThrow(request.getId());

        this.productMapper.copy(entity, request);

        entity = this.productRepository.save(entity);

        var response = this.productMapper.toResponse(entity);

        this.productCache.evictById(response.getId());
        this.productCache.evictAll();

        return response;
    }

    /**
     * Equivalente manual de:
     * - @Caching(evict = {
     *     @CacheEvict(value = "products", key = "#id"),
     *     @CacheEvict(value = "products", key = "'all'")
     *   })
     */
    @Transactional
    public void delete(Long id) {
        log.info("{} delete()", LOG_PREFIX);

        this.productRepository.deleteById(id);

        this.productCache.evictById(id);
        this.productCache.evictAll();
    }

    /**
     * Equivalente manual de:
     * - @CacheEvict(value = "products", allEntries = true)
     */
    public void clearCache() {
        log.info("{} clearCache()", LOG_PREFIX);

        this.productCache.clearAllEntries();
    }

    private Product findByIdOrThrow(Long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(MSG_ERROR_PRODUCT_NOT_FOUND, id)));
    }
}

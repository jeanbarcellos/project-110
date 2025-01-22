package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jeanbarcellos.project110.entity.Product;
import com.jeanbarcellos.project110.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Pattern Lazy Loading
 *
 * Adicionar ao cache quando solicidao
 */
@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Recupera todos os produtos do banco de dados.
     *
     * - Usa cache para armazenar a lista completa de produtos com a chave 'all'.
     * - O cache só é preenchido quando este método é chamado pela primeira vez.
     * - Se o cache for inválido, os dados serão recarregados do banco.
     */
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        doLongRunningTask();

        return this.productRepository.findAll();
    }

    /**
     * Recupera um produto específico pelo ID.
     *
     * - Usa cache para armazenar cada produto individualmente com a chave baseada no ID.
     * - O cache só é preenchido na primeira chamada deste método para um ID específico.
     */
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        doLongRunningTask();

        return this.productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    /**
     * Cria um novo produto no banco de dados.
     *
     * - Remove o cache da lista completa ('all') para garantir que ela seja recarregada na próxima consulta.
     */
    @CacheEvict(value = "products", key = "'all'")
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Atualiza os dados de um produto existente.
     *
     * - Remove o cache do produto específico.
     * - Remove o cache da lista completa ('all') para garantir que os dados estejam atualizados na próxima consulta.
     */
    @Caching(evict = {
        @CacheEvict(value = "products", key = "#result.id"),
        @CacheEvict(value = "products", key = "'all'") })
    public Product updateProduct(Long id, Product product) {
        Product entity = getProductById(id);

        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());

        return productRepository.save(entity);
    }

    /**
     * Exclui um produto do banco de dados.
     *
     * - Remove o cache do produto específico.
     * - Remove o cache da lista completa ('all') para garantir que os dados estejam atualizados na próxima consulta.
     */
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products", key = "'all'") })
    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
    }

    private void doLongRunningTask() {
        log.info("Query no banco de dados");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
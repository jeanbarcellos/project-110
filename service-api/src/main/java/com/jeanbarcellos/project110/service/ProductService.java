package com.jeanbarcellos.project110.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jeanbarcellos.project110.entity.Product;
import com.jeanbarcellos.project110.repository.ProductRepository;

/**
 * Pattern Lazy Loading
 *
 * * Adicionar ao cache quando solicidao
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public void deleteProduct(Long id) {
        this.productRepository.deleteById(id);
    }

}
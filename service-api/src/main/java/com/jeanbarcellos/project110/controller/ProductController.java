package com.jeanbarcellos.project110.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project110.entity.Product;
import com.jeanbarcellos.project110.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Manage products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return this.productService.getProductById(id);
    }

    @GetMapping
    public List<Product> getAll() {
        return this.productService.getAllProducts();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.productService.deleteProduct(id);
    }
}
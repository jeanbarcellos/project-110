package com.jeanbarcellos.project110.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jeanbarcellos.project110.dto.ProductRequest;
import com.jeanbarcellos.project110.dto.ProductResponse;
import com.jeanbarcellos.project110.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Manage products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Listar todas os produtos")
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(this.productService.getCacheKeyAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter produto pelo ID")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Criar um produto")
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(this.productService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar um produto")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(this.productService.update(request.setId(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Apagar um produto")
    public void delete(@PathVariable Long id) {
        this.productService.delete(id);
        ResponseEntity.noContent();
    }
}
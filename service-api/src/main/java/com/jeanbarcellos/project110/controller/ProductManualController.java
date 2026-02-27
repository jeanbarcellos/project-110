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
import com.jeanbarcellos.project110.service.ProductManualService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manual/products")
@Tag(name = "Products Manual", description = "Manage products with manual cache")
public class ProductManualController {

    private final ProductManualService productManualService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos (cache manual)")
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(this.productManualService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter produto pelo ID (cache manual)")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productManualService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Criar um produto (cache manual)")
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productManualService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar um produto (cache manual)")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(this.productManualService.update(request.setId(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Apagar um produto (cache manual)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.productManualService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cache")
    @Operation(summary = "Limpar cache manual de produtos")
    public ResponseEntity<Void> clearCache() {
        this.productManualService.clearCache();
        return ResponseEntity.noContent().build();
    }
}

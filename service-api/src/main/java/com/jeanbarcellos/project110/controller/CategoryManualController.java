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

import com.jeanbarcellos.project110.dto.CategoryRequest;
import com.jeanbarcellos.project110.dto.CategoryResponse;
import com.jeanbarcellos.project110.service.CategoryManualService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manual/categories")
@Tag(name = "Categories Manual", description = "Manage categories with manual cache")
public class CategoryManualController {

    private final CategoryManualService categoryManualService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias (cache manual)")
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(this.categoryManualService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter categoria pelo ID (cache manual)")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.categoryManualService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Criar uma categoria (cache manual)")
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.categoryManualService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar uma categoria (cache manual)")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(this.categoryManualService.update(request.setId(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Apagar uma categoria (cache manual)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.categoryManualService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cache")
    @Operation(summary = "Limpar cache manual de categorias")
    public ResponseEntity<Void> clearCache() {
        this.categoryManualService.clearCache();
        return ResponseEntity.noContent().build();
    }

}

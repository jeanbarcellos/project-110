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

import com.jeanbarcellos.project110.dto.PersonRequest;
import com.jeanbarcellos.project110.dto.PersonResponse;
import com.jeanbarcellos.project110.service.PersonService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persons")
@Tag(name = "Persons", description = "Manage Persons")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAll() {
        return ResponseEntity.ok(this.personService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.personService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> create(@RequestBody PersonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(this.personService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable Long id, @RequestBody PersonRequest request) {
        return ResponseEntity.ok(this.personService.update(request.setId(id)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.personService.delete(id);
        ResponseEntity.noContent();
    }
}
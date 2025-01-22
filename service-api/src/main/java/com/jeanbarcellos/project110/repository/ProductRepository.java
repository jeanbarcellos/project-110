package com.jeanbarcellos.project110.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanbarcellos.project110.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
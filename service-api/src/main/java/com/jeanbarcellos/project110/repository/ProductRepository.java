package com.jeanbarcellos.project110.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeanbarcellos.project110.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
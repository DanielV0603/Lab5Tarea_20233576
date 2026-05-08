package com.example.lab5.repository;

import com.example.lab5.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByNombre(String nombre);
}

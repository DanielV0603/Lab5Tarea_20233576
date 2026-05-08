package com.example.lab5.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El mensaje debe tener máximo 100 caracteres")
    @Column(name = "name",nullable = false, length = 100, unique = true)
    private String nombre;

    @NotBlank(message = "El precio no puede estar vacío")
    @Column(name = "price",nullable = false)
    private double precio;

    @NotBlank(message = "El stock no puede estar vacío")
    @Column(nullable = false)
    private int stock;
}

package com.example.lab5.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El mensaje debe tener máximo 100 caracteres")
    @Column(name = "name",nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El documento es requerido")
    @Column(name = "document",nullable = false, length = 11, unique = true)
    private String documento;

    @NotBlank(message = "El tipo es requerido")
    @Column(name = "document_type",nullable = false, length = 10)
    private String documentoTipo;
}

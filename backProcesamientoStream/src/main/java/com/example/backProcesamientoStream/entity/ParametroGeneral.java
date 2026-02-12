package com.example.backProcesamientoStream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parametro_general")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametroGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_parametro", nullable = false)
    private String nombreParametro;

    @Column(name = "valor", nullable = false)
    private String valor;

    @Column(name = "descripcion")
    private String descripcion; // Explica qué representa el valor

    @Column(name = "estado", nullable = false)
    private String estado = "ACTIVO"; // ACTIVO / INACTIVO para habilitar scheduler
}



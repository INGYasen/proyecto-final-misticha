package com.raymisupreme.mscatalogo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "descuento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescuentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    private String descripcion;

    private BigDecimal porcentaje;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @Builder.Default
    private Boolean activo = true;
}

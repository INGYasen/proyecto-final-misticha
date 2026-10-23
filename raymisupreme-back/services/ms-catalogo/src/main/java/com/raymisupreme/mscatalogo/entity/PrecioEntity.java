package com.raymisupreme.mscatalogo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "precio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrecioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long prendaId;

    private String nombre;

    private BigDecimal precioBase;

    private BigDecimal precioFinal;

    @Builder.Default
    private Boolean activo = true;

    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;

    @ManyToOne
    @JoinColumn(name = "descuento_id")
    private DescuentoEntity descuento;
}

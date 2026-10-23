package com.raymisupreme.msacopio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lote_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoteCompraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_artesano", nullable = false)
    private ArtesanoEntity artesano;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "precio_total_pagado", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioTotalPagado;

    @Column(name = "cantidad_total_recibida", nullable = false)
    private Integer cantidadTotalRecibida;

    @Column(name = "descripcion_general", length = 255)
    private String descripcionGeneral;

    @Column(name = "estado_lote", length = 50, nullable = false)
    private String estadoLote;
}
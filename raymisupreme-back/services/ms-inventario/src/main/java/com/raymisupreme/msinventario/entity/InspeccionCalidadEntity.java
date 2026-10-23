package com.raymisupreme.msinventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "inspeccion_calidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspeccionCalidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inspeccion")
    private Long idInspeccionCalidad;

    @Column(name = "fecha_inspeccion")
    private LocalDate fechaInspeccion;

    @Column(name = "resultado", length = 80)
    private String resultado;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_prenda", nullable = false)
    private PrendaEntity prenda;
}
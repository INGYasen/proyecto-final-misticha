package com.raymisupreme.msinventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspeccionCalidadResponse {

    private Long idInspeccionCalidad;
    private LocalDate fechaInspeccion;
    private String resultado;
    private String observaciones;

    private Long idPrenda;
}
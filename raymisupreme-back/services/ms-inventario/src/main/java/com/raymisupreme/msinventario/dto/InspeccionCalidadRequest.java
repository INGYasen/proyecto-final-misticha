package com.raymisupreme.msinventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspeccionCalidadRequest {

    private LocalDate fechaInspeccion;

    private String resultado;

    private String observaciones;

    @NotNull
    private Long idPrenda;
}
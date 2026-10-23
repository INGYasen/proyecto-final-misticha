package com.raymisupreme.mscatalogo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DescuentoRequest {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    private String descripcion;

    @NotNull(message = "El porcentaje es obligatorio")
    @Positive(message = "El porcentaje debe ser mayor a cero")
    @DecimalMax(value = "99", message = "El porcentaje no puede superar 99")
    private BigDecimal porcentaje;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotEmpty(message = "Selecciona al menos un producto del catalogo")
    private List<Long> prendaIds;
}

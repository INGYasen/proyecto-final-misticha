package com.raymisupreme.mscarrito.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoRequest {

    @NotNull
    private Long prendaId;

    @NotBlank
    private String nombrePrenda;

    @NotNull
    @Positive
    private BigDecimal precio;

    @NotNull
    @Min(1)
    private Integer cantidad;
}

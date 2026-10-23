package com.raymisupreme.mscarrito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarritoResponse {

    private Long id;
    private Long prendaId;
    private String nombrePrenda;
    private BigDecimal precio;
    private Integer cantidad;
    private BigDecimal subtotal;
}

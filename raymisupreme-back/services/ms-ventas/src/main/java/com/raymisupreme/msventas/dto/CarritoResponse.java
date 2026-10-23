package com.raymisupreme.msventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {

    private Long id;
    private Long clienteId;
    private String estado;
    private List<ItemCarritoResponse> items;
    private BigDecimal totalCarrito;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}

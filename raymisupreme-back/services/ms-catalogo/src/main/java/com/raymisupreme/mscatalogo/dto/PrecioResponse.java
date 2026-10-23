package com.raymisupreme.mscatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrecioResponse {
    private Long id;
    private Long prendaId;
    private String nombre;
    private PrendaInventarioResponse prenda;
    private BigDecimal precioBase;
    private BigDecimal precioFinal;
    private Boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private DescuentoResponse descuento;
}

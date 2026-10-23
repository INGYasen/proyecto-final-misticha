package com.raymisupreme.mscarrito.dto;

import com.raymisupreme.mscarrito.client.dto.ClienteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponse {

    private Long id;
    private Long clienteId;
    private ClienteResponse cliente;
    private String estado;
    private List<ItemCarritoResponse> items;
    private BigDecimal totalCarrito;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}

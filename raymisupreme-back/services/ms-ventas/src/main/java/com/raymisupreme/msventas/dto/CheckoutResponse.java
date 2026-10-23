package com.raymisupreme.msventas.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private Long idPedido;
    private Long idCliente;
    private String estadoPedido;
    private BigDecimal totalPagar;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mensaje;
}

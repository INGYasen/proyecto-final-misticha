package com.raymisupreme.msventas.service.impl;

import com.raymisupreme.msventas.dto.DetallePedidoRequest;
import com.raymisupreme.msventas.entity.DetallePedidoEntity;
import com.raymisupreme.msventas.repository.DetallePedidoRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DetallePedidoServiceImplTest {

    private final DetallePedidoRepository detallePedidoRepository = mock(DetallePedidoRepository.class);
    private final DetallePedidoServiceImpl service = new DetallePedidoServiceImpl(detallePedidoRepository);

    @Test
    void crearUsaPrecioUnitarioDelRequest() {
        when(detallePedidoRepository.save(any())).thenAnswer(invocation -> {
            DetallePedidoEntity entity = invocation.getArgument(0);
            entity.setIdDetalle(1L);
            return entity;
        });

        DetallePedidoRequest request = new DetallePedidoRequest(100L, 200L, 1, new BigDecimal("80.00"));

        var response = service.crear(request);

        assertThat(response.getPrecioUnitario()).isEqualByComparingTo("80.00");
    }
}

package com.raymisupreme.msventas.service.impl;

import com.raymisupreme.msventas.dto.ConfirmarPedidoRequest;
import com.raymisupreme.msventas.dto.CarritoResponse;
import com.raymisupreme.msventas.dto.ItemCarritoResponse;
import com.raymisupreme.msventas.dto.PagoResponse;
import com.raymisupreme.msventas.dto.StockResponse;
import com.raymisupreme.msventas.entity.ClienteEntity;
import com.raymisupreme.msventas.entity.DetallePedidoEntity;
import com.raymisupreme.msventas.entity.PedidoEntity;
import com.raymisupreme.msventas.repository.ClienteRepository;
import com.raymisupreme.msventas.repository.DetallePedidoRepository;
import com.raymisupreme.msventas.event.PedidoEventoPublisher;
import com.raymisupreme.msventas.repository.PedidoRepository;
import com.raymisupreme.msventas.service.CarritoIntegrationService;
import com.raymisupreme.msventas.service.InventarioIntegrationService;
import com.raymisupreme.msventas.service.PagosIntegrationService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class PedidoServiceImplTest {

    private final PedidoRepository pedidoRepository = mock(PedidoRepository.class);
    private final DetallePedidoRepository detallePedidoRepository = mock(DetallePedidoRepository.class);
    private final ClienteRepository clienteRepository = mock(ClienteRepository.class);
    private final CarritoIntegrationService carritoIntegrationService = mock(CarritoIntegrationService.class);
    private final InventarioIntegrationService inventarioIntegrationService = mock(InventarioIntegrationService.class);
    private final PagosIntegrationService pagosIntegrationService = mock(PagosIntegrationService.class);
    private final PedidoEventoPublisher pedidoEventoPublisher = mock(PedidoEventoPublisher.class);
    private final PedidoServiceImpl service = new PedidoServiceImpl(
            pedidoRepository,
            detallePedidoRepository,
            clienteRepository,
            carritoIntegrationService,
            inventarioIntegrationService,
            pagosIntegrationService,
            pedidoEventoPublisher);

    @Test
    void confirmarPedidoPagadoProcesaPagoDescuentaStockYLimpiaCarrito() {
        PedidoEntity pedido = pedidoPendiente();
        DetallePedidoEntity detalle = detallePedido(pedido);
        PagoResponse pago = new PagoResponse();
        pago.setEstado("EXITOSO");

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pagosIntegrationService.procesarPago(any())).thenReturn(pago);
        when(detallePedidoRepository.findByPedido_IdPedido(100L)).thenReturn(List.of(detalle));
        when(inventarioIntegrationService.obtenerStock(200L))
                .thenReturn(StockResponse.builder().idPrenda(200L).stock(10).build());
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        ConfirmarPedidoRequest request = new ConfirmarPedidoRequest();
        request.setMetodoPago("TARJETA");

        var response = service.confirmarPedido(100L, request);

        assertThat(response.getEstadoPedido()).isEqualTo("PAGADO");
        verify(pagosIntegrationService).procesarPago(any());
        verify(inventarioIntegrationService).descontarStock(200L, 2);
    }

    @Test
    void checkoutCreaPedidoPendienteSinProcesarPago() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setIdCliente(2L);

        ItemCarritoResponse item = ItemCarritoResponse.builder()
                .prendaId(4L)
                .cantidad(2)
                .precio(new BigDecimal("239.92"))
                .subtotal(new BigDecimal("479.84"))
                .build();
        CarritoResponse carrito = CarritoResponse.builder()
                .clienteId(2L)
                .items(List.of(item))
                .totalCarrito(new BigDecimal("479.84"))
                .build();

        when(carritoIntegrationService.obtenerCarrito(2L)).thenReturn(carrito);
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(cliente));
        when(inventarioIntegrationService.obtenerStock(4L))
                .thenReturn(StockResponse.builder().idPrenda(4L).stock(5).build());
        when(pedidoRepository.save(any(PedidoEntity.class))).thenAnswer(invocation -> {
            PedidoEntity pedido = invocation.getArgument(0);
            pedido.setIdPedido(101L);
            return pedido;
        });

        var response = service.checkout(2L);

        assertThat(response.getIdPedido()).isEqualTo(101L);
        assertThat(response.getEstadoPedido()).isEqualTo("PENDIENTE");
        assertThat(response.getMensaje()).contains("Pendiente de pago");
        verify(detallePedidoRepository).save(any(DetallePedidoEntity.class));
        verify(carritoIntegrationService).limpiarCarrito(2L);
        verify(pagosIntegrationService, never()).procesarPago(any());
        verify(inventarioIntegrationService, never()).descontarStock(any(), any());
    }

    @Test
    void cancelarPedidoPendienteNoProcesaPago() {
        PedidoEntity pedido = pedidoPendiente();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        var response = service.cancelarPedido(100L);

        assertThat(response.getEstadoPedido()).isEqualTo("CANCELADO");
        verify(pagosIntegrationService, never()).procesarPago(any());
    }

    private PedidoEntity pedidoPendiente() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setIdCliente(300L);
        cliente.setNombres("Cliente");
        cliente.setEmail("cliente@test.com");

        PedidoEntity pedido = new PedidoEntity();
        pedido.setIdPedido(100L);
        pedido.setCliente(cliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstadoPedido("Pendiente de Pago");
        pedido.setTotalPagar(new BigDecimal("120.00"));
        return pedido;
    }

    private DetallePedidoEntity detallePedido(PedidoEntity pedido) {
        DetallePedidoEntity detalle = new DetallePedidoEntity();
        detalle.setIdDetalle(1L);
        detalle.setPedido(pedido);
        detalle.setIdPrenda(200L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("60.00"));
        return detalle;
    }
}

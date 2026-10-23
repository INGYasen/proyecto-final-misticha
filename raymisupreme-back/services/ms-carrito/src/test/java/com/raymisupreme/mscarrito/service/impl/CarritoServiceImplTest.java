package com.raymisupreme.mscarrito.service.impl;

import com.raymisupreme.mscarrito.client.CatalogoClient;
import com.raymisupreme.mscarrito.client.VentasClient;
import com.raymisupreme.mscarrito.client.dto.ClienteResponse;
import com.raymisupreme.mscarrito.client.dto.PrendaCatalogoResponse;
import com.raymisupreme.mscarrito.client.dto.ProductoCatalogoResponse;
import com.raymisupreme.mscarrito.dto.CarritoResponse;
import com.raymisupreme.mscarrito.entity.CarritoEntity;
import com.raymisupreme.mscarrito.mapper.CarritoMapper;
import com.raymisupreme.mscarrito.mapper.ItemCarritoMapper;
import com.raymisupreme.mscarrito.repository.CarritoRepository;
import com.raymisupreme.mscarrito.repository.ItemCarritoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CarritoServiceImplTest {

    private final CarritoRepository carritoRepository = mock(CarritoRepository.class);
    private final ItemCarritoRepository itemCarritoRepository = mock(ItemCarritoRepository.class);
    private final CarritoMapper carritoMapper = mock(CarritoMapper.class);
    private final ItemCarritoMapper itemCarritoMapper = mock(ItemCarritoMapper.class);
    private final CatalogoClient catalogoClient = mock(CatalogoClient.class);
    private final VentasClient ventasClient = mock(VentasClient.class);
    private final CarritoServiceImpl service = new CarritoServiceImpl(
            carritoRepository,
            itemCarritoRepository,
            carritoMapper,
            itemCarritoMapper,
            catalogoClient,
            ventasClient
    );

    @Test
    void obtenerCarritoCreaCarritoActivoSiNoExiste() {
        CarritoEntity carritoCreado = CarritoEntity.builder()
                .id(1L)
                .clienteId(1L)
                .estado("ACTIVO")
                .build();

        CarritoResponse responseEsperado = CarritoResponse.builder()
                .id(1L)
                .clienteId(1L)
                .estado("ACTIVO")
                .build();

        when(carritoRepository.findByClienteIdAndEstado(1L, "ACTIVO")).thenReturn(Optional.empty());
        when(carritoRepository.save(any(CarritoEntity.class))).thenReturn(carritoCreado);
        when(itemCarritoRepository.findByCarrito_Id(1L)).thenReturn(List.of());
        when(carritoMapper.toResponse(carritoCreado)).thenReturn(responseEsperado);
        when(ventasClient.obtenerCliente(1L)).thenReturn(ClienteResponse.builder()
                .idCliente(1L)
                .nombres("Lucia Quispe")
                .email("lucia@example.com")
                .pais("Peru")
                .ciudad("Cusco")
                .direccionEnvio("Av. Sol 123")
                .build());

        CarritoResponse response = service.obtenerCarrito(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getClienteId()).isEqualTo(1L);
        assertThat(response.getEstado()).isEqualTo("ACTIVO");
        assertThat(response.getCliente().getNombres()).isEqualTo("Lucia Quispe");
        assertThat(response.getCliente().getEmail()).isEqualTo("lucia@example.com");
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotalCarrito()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(carritoRepository).save(any(CarritoEntity.class));
    }

    @Test
    void validarProductoDisponibleUsaStockDePrendaAnidada() {
        ProductoCatalogoResponse producto = productoConStock(3L, 2);
        when(catalogoClient.obtenerProducto(3L)).thenReturn(producto);
        ReflectionTestUtils.setField(service, "self", service);

        ProductoCatalogoResponse resultado = service.validarProductoDisponible(3L, 2);

        assertThat(resultado).isSameAs(producto);
    }

    @Test
    void validarProductoDisponibleDevuelveConflictoCuandoFaltaStock() {
        when(catalogoClient.obtenerProducto(3L)).thenReturn(productoConStock(3L, 1));
        ReflectionTestUtils.setField(service, "self", service);

        assertThatThrownBy(() -> service.validarProductoDisponible(3L, 2))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT")
                .hasMessageContaining("Stock insuficiente");
    }

    private ProductoCatalogoResponse productoConStock(Long prendaId, int stock) {
        PrendaCatalogoResponse prenda = new PrendaCatalogoResponse();
        prenda.setIdPrenda(prendaId);
        prenda.setStock(stock);

        ProductoCatalogoResponse producto = new ProductoCatalogoResponse();
        producto.setIdPrenda(prendaId);
        producto.setPrenda(prenda);
        return producto;
    }
}

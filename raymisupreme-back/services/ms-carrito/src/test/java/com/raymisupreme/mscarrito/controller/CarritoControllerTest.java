package com.raymisupreme.mscarrito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raymisupreme.mscarrito.dto.CarritoResponse;
import com.raymisupreme.mscarrito.dto.ItemCarritoRequest;
import com.raymisupreme.mscarrito.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarritoController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarritoService carritoService;

    @Test
    void exponeRutasVersionadasDelCarrito() throws Exception {
        CarritoResponse response = CarritoResponse.builder()
                .id(1L)
                .clienteId(1L)
                .estado("ACTIVO")
                .items(List.of())
                .totalCarrito(BigDecimal.ZERO)
                .build();

        when(carritoService.obtenerCarrito(1L)).thenReturn(response);
        when(carritoService.agregarItem(eq(1L), any(ItemCarritoRequest.class))).thenReturn(response);
        when(carritoService.actualizarCantidad(1L, 10L, 2)).thenReturn(response);
        when(carritoService.actualizarCantidad(1L, 10L, 0)).thenReturn(response);

        ItemCarritoRequest request = new ItemCarritoRequest(
                1L,
                "Chompa Alpaca Premium Beige",
                new BigDecimal("269.91"),
                1
        );

        mockMvc.perform(get("/api/v1/carrito/1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/carrito/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/carrito/1/items/10")
                        .param("cantidad", "2"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/carrito/1/items/10"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/carrito/1"))
                .andExpect(status().isNoContent());

        verify(carritoService).vaciarCarrito(1L);
    }
}

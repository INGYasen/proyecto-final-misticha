package com.raymisupreme.msinventario.service.impl;

import com.raymisupreme.msinventario.client.dto.LoteCompraResponse;
import com.raymisupreme.msinventario.dto.PrendaDetalleCompletoResponse;
import com.raymisupreme.msinventario.dto.PrendaRequest;
import com.raymisupreme.msinventario.dto.PrendaResponse;
import com.raymisupreme.msinventario.entity.CategoriaEntity;
import com.raymisupreme.msinventario.entity.PrendaEntity;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;
import com.raymisupreme.msinventario.repository.PrendaRepository;
import com.raymisupreme.msinventario.repository.CategoriaRepository;
import com.raymisupreme.msinventario.service.AcopioIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrendaServiceImplTest {

    private final PrendaRepository prendaRepository = mock(PrendaRepository.class);
    private final CategoriaRepository categoriaRepository = mock(CategoriaRepository.class);
    private final AcopioIntegrationService acopioIntegrationService = mock(AcopioIntegrationService.class);
    private final PrendaServiceImpl service = new PrendaServiceImpl(prendaRepository, categoriaRepository, acopioIntegrationService);

    @Test
    void createDevuelveNombreDeCategoriaReal() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setIdCategoria(2L);
        categoria.setNombre("Chompas");

        PrendaRequest request = new PrendaRequest();
        request.setIdLote(1L);
        request.setIdCategoria(2L);
        request.setPublico(PublicoObjetivo.VARON);
        request.setCodigoUnico("CHOM-VAR-001");
        request.setNombrePrenda("Chompa andina Cusco");
        request.setStock(10);

        when(acopioIntegrationService.obtenerLote(1L)).thenReturn(LoteCompraResponse.builder().idLote(1L).build());
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria));
        when(prendaRepository.save(org.mockito.ArgumentMatchers.any(PrendaEntity.class)))
                .thenAnswer(invocation -> {
                    PrendaEntity saved = invocation.getArgument(0);
                    saved.setIdPrenda(1L);
                    return saved;
                });

        PrendaResponse response = service.create(request);

        assertThat(response.getIdCategoria()).isEqualTo(2L);
        assertThat(response.getNombreCategoria()).isEqualTo("Chompas");
        assertThat(response.getPublico()).isEqualTo(PublicoObjetivo.VARON);
    }

    @Test
    void getDetalleCompletoIncluyeDetalleDelLoteCuandoAcopioResponde() {
        PrendaEntity prenda = prendaConStock(70);
        prenda.setIdLote(4L);
        prenda.setCodigoUnico("PCH-CHOM-ALP-001");

        LoteCompraResponse lote = LoteCompraResponse.builder()
                .idLote(4L)
                .idArtesano(4L)
                .nombreArtesano("Lucio Hancco Ticona")
                .fechaCompra(LocalDate.of(2026, 6, 9))
                .estadoLote("APROBADO")
                .build();

        when(prendaRepository.findById(1L)).thenReturn(Optional.of(prenda));
        when(acopioIntegrationService.obtenerLote(4L)).thenReturn(lote);

        PrendaDetalleCompletoResponse response = service.getDetalleCompleto(1L);

        assertThat(response.getIdPrenda()).isEqualTo(1L);
        assertThat(response.getCodigoUnico()).isEqualTo("PCH-CHOM-ALP-001");
        assertThat(response.getTalla()).isEqualTo("M");
        assertThat(response.getColor()).isEqualTo("Beige");
        assertThat(response.getMaterial()).isEqualTo("Alpaca");
        assertThat(response.getStock()).isEqualTo(70);
        assertThat(response.getCategoria().getIdCategoria()).isEqualTo(1L);
        assertThat(response.getCategoria().getNombre()).isEqualTo("Chompas");
        assertThat(response.getPublico()).isEqualTo(PublicoObjetivo.VARON);
        assertThat(response.getLote()).isEqualTo(lote);
        assertThat(response.getLote().getIdArtesano()).isEqualTo(4L);
        assertThat(response.getMensaje()).isNull();
    }

    @Test
    void getDetalleCompletoDevuelveFallbackCuandoAcopioNoDisponible() {
        PrendaEntity prenda = prendaConStock(70);
        prenda.setIdLote(4L);
        prenda.setCodigoUnico("PCH-CHOM-ALP-001");

        when(prendaRepository.findById(1L)).thenReturn(Optional.of(prenda));
        when(acopioIntegrationService.obtenerLote(4L)).thenReturn(null);

        PrendaDetalleCompletoResponse response = service.getDetalleCompleto(1L);

        assertThat(response.getIdPrenda()).isEqualTo(1L);
        assertThat(response.getCodigoUnico()).isEqualTo("PCH-CHOM-ALP-001");
        assertThat(response.getTalla()).isEqualTo("M");
        assertThat(response.getColor()).isEqualTo("Beige");
        assertThat(response.getMaterial()).isEqualTo("Alpaca");
        assertThat(response.getStock()).isEqualTo(70);
        assertThat(response.getCategoria().getIdCategoria()).isEqualTo(1L);
        assertThat(response.getCategoria().getNombre()).isEqualTo("Chompas");
        assertThat(response.getPublico()).isEqualTo(PublicoObjetivo.VARON);
        assertThat(response.getLote()).isNull();
        assertThat(response.getMensaje()).isEqualTo("ms-acopio no disponible");
    }

    @Test
    void descontarStockReduceStockCuandoHayCantidadSuficiente() {
        PrendaEntity prenda = prendaConStock(10);
        when(prendaRepository.findById(1L)).thenReturn(Optional.of(prenda));
        when(prendaRepository.save(prenda)).thenReturn(prenda);

        service.descontarStock(1L, 3);

        assertThat(prenda.getStock()).isEqualTo(7);
        verify(prendaRepository).save(prenda);
    }

    @Test
    void descontarStockRechazaStockInsuficiente() {
        PrendaEntity prenda = prendaConStock(2);
        when(prendaRepository.findById(1L)).thenReturn(Optional.of(prenda));

        assertThatThrownBy(() -> service.descontarStock(1L, 3))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    private PrendaEntity prendaConStock(Integer stock) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Chompas");

        PrendaEntity prenda = new PrendaEntity();
        prenda.setIdPrenda(1L);
        prenda.setIdLote(10L);
        prenda.setCategoria(categoria);
        prenda.setCodigoUnico("SKU-001");
        prenda.setPublico(PublicoObjetivo.VARON);
        prenda.setTalla("M");
        prenda.setColor("Beige");
        prenda.setMaterial("Alpaca");
        prenda.setStock(stock);
        return prenda;
    }
}

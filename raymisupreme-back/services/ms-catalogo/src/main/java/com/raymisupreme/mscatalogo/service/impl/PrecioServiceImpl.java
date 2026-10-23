package com.raymisupreme.mscatalogo.service.impl;

import com.raymisupreme.mscatalogo.client.InventarioClient;
import com.raymisupreme.mscatalogo.dto.PrecioRequest;
import com.raymisupreme.mscatalogo.dto.PrecioResponse;
import com.raymisupreme.mscatalogo.dto.PrendaInventarioResponse;
import com.raymisupreme.mscatalogo.entity.DescuentoEntity;
import com.raymisupreme.mscatalogo.entity.PrecioEntity;
import com.raymisupreme.mscatalogo.mapper.PrecioMapper;
import com.raymisupreme.mscatalogo.repository.DescuentoRepository;
import com.raymisupreme.mscatalogo.repository.PrecioRepository;
import com.raymisupreme.mscatalogo.service.PrecioService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrecioServiceImpl implements PrecioService {

    private final PrecioRepository precioRepository;
    private final DescuentoRepository descuentoRepository;
    private final PrecioMapper precioMapper;
    private final InventarioClient inventarioClient;

    public PrecioServiceImpl(PrecioRepository precioRepository,
                             DescuentoRepository descuentoRepository,
                             PrecioMapper precioMapper,
                             InventarioClient inventarioClient) {
        this.precioRepository = precioRepository;
        this.descuentoRepository = descuentoRepository;
        this.precioMapper = precioMapper;
        this.inventarioClient = inventarioClient;
    }

    @Override
    public List<PrecioResponse> listarActivos() {
        return precioRepository.findAll().stream()
                .filter(PrecioEntity::getActivo)
                .map(this::toResponseConPrenda)
                .toList();
    }

    @Override
    public PrecioResponse obtenerPorPrendaId(Long prendaId) {
        PrecioEntity entity = precioRepository.findByPrendaIdAndActivoTrue(prendaId)
                .orElseThrow(() -> new RuntimeException("Precio no encontrado o inactivo para la prenda ID: " + prendaId));
        return toResponseConPrenda(entity);
    }

    @Override
    public PrecioResponse crear(PrecioRequest request) {
        PrendaInventarioResponse prenda = obtenerPrendaObligatoria(request.getPrendaId());
        PrecioEntity entity = precioMapper.toEntity(request);
        entity.setNombre(resolverNombrePrenda(prenda));
        entity.setActivo(true);
        entity.setCreadoEn(LocalDateTime.now());
        entity.setActualizadoEn(LocalDateTime.now());
        calcularPrecioFinalYAsignarDescuento(request, entity);
        return toResponseConPrenda(precioRepository.save(entity), prenda);
    }
// JALA PRENDAS A MS-CATALOGO 
    @Override
    public PrecioResponse actualizar(Long id, PrecioRequest request) {
        PrendaInventarioResponse prenda = obtenerPrendaObligatoria(request.getPrendaId());
        PrecioEntity existing = precioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Precio no encontrado con ID: " + id));

        existing.setPrendaId(request.getPrendaId());
        existing.setNombre(resolverNombrePrenda(prenda));
        existing.setPrecioBase(request.getPrecioBase());
        existing.setActualizadoEn(LocalDateTime.now());
        calcularPrecioFinalYAsignarDescuento(request, existing);

        return toResponseConPrenda(precioRepository.save(existing), prenda);
    }

    @Override
    public void eliminar(Long id) {
        PrecioEntity existing = precioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Precio no encontrado con ID: " + id));
        existing.setActivo(false);
        existing.setActualizadoEn(LocalDateTime.now());
        precioRepository.save(existing);
    }

    private PrecioResponse toResponseConPrenda(PrecioEntity entity) {
        PrendaInventarioResponse prenda = obtenerPrendaSilenciosa(entity.getPrendaId());
        return toResponseConPrenda(entity, prenda);
    }

    private PrecioResponse toResponseConPrenda(PrecioEntity entity, PrendaInventarioResponse prenda) {
        PrecioResponse response = precioMapper.toResponse(entity);
        response.setPrenda(prenda);
        if (prenda != null) {
            response.setNombre(resolverNombrePrenda(prenda));
        }
        return response;
    }

    private PrendaInventarioResponse obtenerPrendaObligatoria(Long prendaId) {
        try {
            PrendaInventarioResponse prenda = inventarioClient.obtenerPrenda(prendaId);
            if (prenda == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La prenda " + prendaId + " no existe en ms-inventario.");
            }
            return prenda;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo consultar la prenda " + prendaId + " en ms-inventario.", ex);
        }
    }

    private PrendaInventarioResponse obtenerPrendaSilenciosa(Long prendaId) {
        try {
            return inventarioClient.obtenerPrenda(prendaId);
        } catch (Exception ex) {
            return null;
        }
    }

    private String resolverNombrePrenda(PrendaInventarioResponse prenda) {
        if (prenda.getNombrePrenda() != null && !prenda.getNombrePrenda().isBlank()) {
            return prenda.getNombrePrenda();
        }
        return prenda.getCodigoUnico();
    }

    private void calcularPrecioFinalYAsignarDescuento(PrecioRequest request, PrecioEntity entity) {
        if (request.getDescuentoId() == null) {
            entity.setDescuento(null);
            entity.setPrecioFinal(request.getPrecioBase());
            return;
        }

        DescuentoEntity descuento = descuentoRepository.findById(request.getDescuentoId())
                .orElseThrow(() -> new RuntimeException("Descuento no encontrado con ID: " + request.getDescuentoId()));
        entity.setDescuento(descuento);

        BigDecimal porcentaje = descuento.getPorcentaje().divide(new BigDecimal("100"));
        BigDecimal descuentoMonto = request.getPrecioBase().multiply(porcentaje);
        entity.setPrecioFinal(request.getPrecioBase().subtract(descuentoMonto).setScale(2, RoundingMode.HALF_UP));
    }
}

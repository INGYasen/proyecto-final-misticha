package com.raymisupreme.mscatalogo.service.impl;

import com.raymisupreme.mscatalogo.dto.DescuentoRequest;
import com.raymisupreme.mscatalogo.dto.DescuentoResponse;
import com.raymisupreme.mscatalogo.entity.DescuentoEntity;
import com.raymisupreme.mscatalogo.entity.PrecioEntity;
import com.raymisupreme.mscatalogo.mapper.DescuentoMapper;
import com.raymisupreme.mscatalogo.repository.DescuentoRepository;
import com.raymisupreme.mscatalogo.repository.ItemCatalogoRepository;
import com.raymisupreme.mscatalogo.repository.PrecioRepository;
import com.raymisupreme.mscatalogo.service.DescuentoService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DescuentoServiceImpl implements DescuentoService {

    private final DescuentoRepository descuentoRepository;
    private final PrecioRepository precioRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final DescuentoMapper descuentoMapper;

    public DescuentoServiceImpl(DescuentoRepository descuentoRepository,
                                PrecioRepository precioRepository,
                                ItemCatalogoRepository itemCatalogoRepository,
                                DescuentoMapper descuentoMapper) {
        this.descuentoRepository = descuentoRepository;
        this.precioRepository = precioRepository;
        this.itemCatalogoRepository = itemCatalogoRepository;
        this.descuentoMapper = descuentoMapper;
    }

    @Override
    public List<DescuentoResponse> listarTodos() {
        return descuentoRepository.findAll().stream()
                .map(this::responder)
                .toList();
    }

    @Override
    public DescuentoResponse crear(DescuentoRequest request) {
        validar(request);
        DescuentoEntity entity = descuentoMapper.toEntity(request);
        entity.setActivo(true);
        DescuentoEntity guardado = descuentoRepository.save(entity);
        asignarPrendas(guardado, request.getPrendaIds());
        return responder(guardado);
    }

    @Override
    public DescuentoResponse actualizar(Long id, DescuentoRequest request) {
        validar(request);
        DescuentoEntity existing = descuentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Descuento no encontrado"));
        existing.setCodigo(request.getCodigo());
        existing.setDescripcion(request.getDescripcion());
        existing.setPorcentaje(request.getPorcentaje());
        existing.setFechaInicio(request.getFechaInicio());
        existing.setFechaFin(request.getFechaFin());
        existing.setActivo(true);
        DescuentoEntity guardado = descuentoRepository.save(existing);
        asignarPrendas(guardado, request.getPrendaIds());
        return responder(guardado);
    }

    @Override
    public void eliminar(Long id) {
        DescuentoEntity entity = descuentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Descuento no encontrado"));
        entity.setActivo(false);
        descuentoRepository.save(entity);
        quitarDePrecios(id);
    }

    private void validar(DescuentoRequest request) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha final no puede ser anterior a la inicial");
        }
    }

    private void asignarPrendas(DescuentoEntity descuento, List<Long> prendaIds) {
        LinkedHashSet<Long> elegidos = new LinkedHashSet<>(prendaIds);
        for (PrecioEntity precio : precioRepository.findByDescuento_IdAndActivoTrue(descuento.getId())) {
            if (!elegidos.contains(precio.getPrendaId())) {
                restaurarPrecio(precio);
            }
        }
        for (Long prendaId : elegidos) {
            if (itemCatalogoRepository.findByIdPrendaAndActivoTrue(prendaId).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El producto " + prendaId + " no está publicado en el catálogo");
            }
            PrecioEntity precio = precioRepository.findByPrendaIdAndActivoTrue(prendaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El producto " + prendaId + " todavía no tiene precio"));
            precio.setDescuento(descuento);
            precio.setPrecioFinal(aplicar(precio.getPrecioBase(), descuento.getPorcentaje()));
            precio.setActualizadoEn(LocalDateTime.now());
            precioRepository.save(precio);
        }
    }

    private void quitarDePrecios(Long descuentoId) {
        for (PrecioEntity precio : precioRepository.findByDescuento_IdAndActivoTrue(descuentoId)) {
            restaurarPrecio(precio);
        }
    }

    private void restaurarPrecio(PrecioEntity precio) {
        precio.setDescuento(null);
        precio.setPrecioFinal(precio.getPrecioBase());
        precio.setActualizadoEn(LocalDateTime.now());
        precioRepository.save(precio);
    }

    private BigDecimal aplicar(BigDecimal base, BigDecimal porcentaje) {
        BigDecimal rebaja = base.multiply(porcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = base.subtract(rebaja).setScale(2, RoundingMode.HALF_UP);
        if (finalPrice.compareTo(new BigDecimal("0.01")) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El descuento deja el precio en cero");
        }
        return finalPrice;
    }

    private DescuentoResponse responder(DescuentoEntity entity) {
        DescuentoResponse response = descuentoMapper.toResponse(entity);
        List<PrecioEntity> precios = precioRepository.findByDescuento_IdAndActivoTrue(entity.getId());
        response.setPrendaIds(precios.stream().map(PrecioEntity::getPrendaId).toList());
        response.setProductos(precios.stream().map(precio ->
                precio.getNombre() == null || precio.getNombre().isBlank()
                        ? "Producto " + precio.getPrendaId()
                        : precio.getNombre()).toList());
        return response;
    }
}

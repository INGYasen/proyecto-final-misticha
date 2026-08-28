package pe.edu.upeu.orden.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.entity.OrdenDetalle;
import pe.edu.upeu.orden.exception.DuplicateResourceException;
import pe.edu.upeu.orden.exception.ResourceNotFoundException;
import pe.edu.upeu.orden.mapper.OrdenMapper;
import pe.edu.upeu.orden.repository.OrdenDetalleRepository;
import pe.edu.upeu.orden.repository.OrdenRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenDetalleRepository ordenDetalleRepository;
    private final OrdenMapper ordenMapper;

    public List<OrdenResponse> listar() {
        return ordenRepository.findAll().stream()
                .map(ordenMapper::toResponse)
                .toList();
    }

    public OrdenResponse obtener(Long id) {
        return ordenMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        if (ordenRepository.existsByCodigo(request.getCodigo())) {
            throw new DuplicateResourceException("Ya existe una orden con codigo: " + request.getCodigo());
        }
        Orden orden = ordenMapper.toEntity(request);
        orden.setFecha(LocalDateTime.now());
        orden.setTotal(BigDecimal.ZERO);
        return ordenMapper.toResponse(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResponse actualizar(Long id, OrdenRequest request) {
        Orden orden = buscarOFallar(id);
        if (!orden.getCodigo().equals(request.getCodigo())
                && ordenRepository.existsByCodigo(request.getCodigo())) {
            throw new DuplicateResourceException("Ya existe una orden con codigo: " + request.getCodigo());
        }
        orden.setCodigo(request.getCodigo());
        orden.setCliente(request.getCliente());
        orden.setEstado(request.getEstado());
        return ordenMapper.toResponse(ordenRepository.save(orden));
    }

    @Transactional
    public void eliminar(Long id) {
        Orden orden = buscarOFallar(id);
        ordenDetalleRepository.deleteByOrdenId(orden.getId());
        ordenRepository.delete(orden);
    }

    /** Recalcula el total de la orden sumando los subtotales de sus detalles. */
    @Transactional
    public void recalcularTotal(Long ordenId) {
        Orden orden = buscarOFallar(ordenId);
        BigDecimal total = ordenDetalleRepository.findByOrdenId(ordenId).stream()
                .map(OrdenDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orden.setTotal(total);
        ordenRepository.save(orden);
    }

    public Orden buscarOFallar(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada: " + id));
    }
}

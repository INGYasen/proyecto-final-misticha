package pe.edu.upeu.orden.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.orden.dto.OrdenDetalleRequest;
import pe.edu.upeu.orden.dto.OrdenDetalleResponse;
import pe.edu.upeu.orden.entity.Orden;
import pe.edu.upeu.orden.entity.OrdenDetalle;
import pe.edu.upeu.orden.exception.ResourceNotFoundException;
import pe.edu.upeu.orden.mapper.OrdenDetalleMapper;
import pe.edu.upeu.orden.repository.OrdenDetalleRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenDetalleService {

    private final OrdenDetalleRepository ordenDetalleRepository;
    private final OrdenDetalleMapper ordenDetalleMapper;
    private final OrdenService ordenService;

    @Transactional(readOnly = true)
    public List<OrdenDetalleResponse> listar(Long ordenId) {
        List<OrdenDetalle> detalles = (ordenId == null)
                ? ordenDetalleRepository.findAll()
                : ordenDetalleRepository.findByOrdenId(ordenId);
        return detalles.stream()
                .map(ordenDetalleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdenDetalleResponse obtener(Long id) {
        return ordenDetalleMapper.toResponse(buscarOFallar(id));
    }

    @Transactional
    public OrdenDetalleResponse crear(OrdenDetalleRequest request) {
        Orden orden = ordenService.buscarOFallar(request.getOrdenId());
        OrdenDetalle detalle = ordenDetalleMapper.toEntity(request);
        detalle.setOrden(orden);
        detalle.setSubtotal(calcularSubtotal(request));
        OrdenDetalle guardado = ordenDetalleRepository.save(detalle);
        ordenService.recalcularTotal(orden.getId());
        return ordenDetalleMapper.toResponse(guardado);
    }

    @Transactional
    public OrdenDetalleResponse actualizar(Long id, OrdenDetalleRequest request) {
        OrdenDetalle detalle = buscarOFallar(id);
        Orden orden = ordenService.buscarOFallar(request.getOrdenId());
        detalle.setOrden(orden);
        detalle.setIdProducto(request.getIdProducto());
        detalle.setDescripcion(request.getDescripcion());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        detalle.setSubtotal(calcularSubtotal(request));
        OrdenDetalle guardado = ordenDetalleRepository.save(detalle);
        ordenService.recalcularTotal(orden.getId());
        return ordenDetalleMapper.toResponse(guardado);
    }

    @Transactional
    public void eliminar(Long id) {
        OrdenDetalle detalle = buscarOFallar(id);
        Long ordenId = detalle.getOrden().getId();
        ordenDetalleRepository.delete(detalle);
        ordenService.recalcularTotal(ordenId);
    }

    private BigDecimal calcularSubtotal(OrdenDetalleRequest request) {
        return request.getPrecioUnitario().multiply(BigDecimal.valueOf(request.getCantidad()));
    }

    private OrdenDetalle buscarOFallar(Long id) {
        return ordenDetalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de orden no encontrado: " + id));
    }
}

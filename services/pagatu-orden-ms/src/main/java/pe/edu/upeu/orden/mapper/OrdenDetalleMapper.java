package pe.edu.upeu.orden.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.orden.dto.OrdenDetalleRequest;
import pe.edu.upeu.orden.dto.OrdenDetalleResponse;
import pe.edu.upeu.orden.entity.OrdenDetalle;

@Component
public class OrdenDetalleMapper {

    public OrdenDetalle toEntity(OrdenDetalleRequest request) {
        return OrdenDetalle.builder()
                .idProducto(request.getIdProducto())
                .descripcion(request.getDescripcion())
                .cantidad(request.getCantidad())
                .precioUnitario(request.getPrecioUnitario())
                .build();
    }

    public OrdenDetalleResponse toResponse(OrdenDetalle detalle) {
        return OrdenDetalleResponse.builder()
                .id(detalle.getId())
                .ordenId(detalle.getOrden().getId())
                .idProducto(detalle.getIdProducto())
                .descripcion(detalle.getDescripcion())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}

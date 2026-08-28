package pe.edu.upeu.orden.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.orden.dto.OrdenRequest;
import pe.edu.upeu.orden.dto.OrdenResponse;
import pe.edu.upeu.orden.entity.Orden;

@Component
public class OrdenMapper {

    public Orden toEntity(OrdenRequest request) {
        return Orden.builder()
                .codigo(request.getCodigo())
                .cliente(request.getCliente())
                .estado(request.getEstado())
                .build();
    }

    public OrdenResponse toResponse(Orden orden) {
        return OrdenResponse.builder()
                .id(orden.getId())
                .codigo(orden.getCodigo())
                .cliente(orden.getCliente())
                .fecha(orden.getFecha())
                .estado(orden.getEstado())
                .total(orden.getTotal())
                .build();
    }
}

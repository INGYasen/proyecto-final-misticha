package pe.edu.upeu.notificacion.mapper;

import pe.edu.upeu.notificacion.dto.AvisoRequest;
import pe.edu.upeu.notificacion.dto.AvisoResponse;
import pe.edu.upeu.notificacion.entity.Aviso;
import org.springframework.stereotype.Component;

@Component
public class AvisoMapper {

    public Aviso toEntity(AvisoRequest request) {
        return Aviso.builder()
                .canalId(request.getCanalId())
                .destinatario(request.getDestinatario())
                .asunto(request.getAsunto())
                .mensaje(request.getMensaje())
                .estado(request.getEstado())
                .build();
    }

    public AvisoResponse toResponse(Aviso entity) {
        return AvisoResponse.builder()
                .id(entity.getId())
                .canalId(entity.getCanalId())
                .destinatario(entity.getDestinatario())
                .asunto(entity.getAsunto())
                .mensaje(entity.getMensaje())
                .estado(entity.getEstado())
                .build();
    }
}

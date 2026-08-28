package pe.edu.upeu.orden.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.orden.entity.OrdenDetalle;

import java.util.List;

public interface OrdenDetalleRepository extends JpaRepository<OrdenDetalle, Long> {

    List<OrdenDetalle> findByOrdenId(Long ordenId);

    void deleteByOrdenId(Long ordenId);
}

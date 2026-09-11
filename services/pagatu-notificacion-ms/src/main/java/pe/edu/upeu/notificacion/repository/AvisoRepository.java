package pe.edu.upeu.notificacion.repository;

import pe.edu.upeu.notificacion.entity.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {
}

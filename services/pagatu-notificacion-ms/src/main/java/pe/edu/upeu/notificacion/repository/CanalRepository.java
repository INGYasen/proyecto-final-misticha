package pe.edu.upeu.notificacion.repository;

import pe.edu.upeu.notificacion.entity.Canal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanalRepository extends JpaRepository<Canal, Long> {
    boolean existsByCodigo(String value);
}

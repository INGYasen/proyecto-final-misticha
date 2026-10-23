package com.raymisupreme.msinventario.repository;

import com.raymisupreme.msinventario.entity.PrendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.raymisupreme.msinventario.entity.PublicoObjetivo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrendaRepository extends JpaRepository<PrendaEntity, Long> {

    List<PrendaEntity> findByIdLote(Long idLote);

    @Query("""
            SELECT p FROM PrendaEntity p
            WHERE (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria)
              AND (:publico IS NULL OR p.publico = :publico)
            """)
    List<PrendaEntity> findByFiltros(@Param("idCategoria") Long idCategoria,
                                     @Param("publico") PublicoObjetivo publico);
}

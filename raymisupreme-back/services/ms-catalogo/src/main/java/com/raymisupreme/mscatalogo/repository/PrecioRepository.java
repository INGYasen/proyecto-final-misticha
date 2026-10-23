package com.raymisupreme.mscatalogo.repository;

import com.raymisupreme.mscatalogo.entity.PrecioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PrecioRepository extends JpaRepository<PrecioEntity, Long> {
    Optional<PrecioEntity> findByPrendaIdAndActivoTrue(Long prendaId);

    List<PrecioEntity> findByActivoTrue();

    List<PrecioEntity> findByDescuento_IdAndActivoTrue(Long descuentoId);
}

package com.raymisupreme.mscatalogo.repository;

import com.raymisupreme.mscatalogo.entity.DescuentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DescuentoRepository extends JpaRepository<DescuentoEntity, Long> {
    Optional<DescuentoEntity> findByCodigoAndActivoTrue(String codigo);

    List<DescuentoEntity> findByActivoTrue();
}

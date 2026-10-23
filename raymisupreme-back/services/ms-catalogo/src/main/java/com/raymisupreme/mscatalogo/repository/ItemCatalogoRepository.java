package com.raymisupreme.mscatalogo.repository;

import com.raymisupreme.mscatalogo.entity.ItemCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCatalogoRepository extends JpaRepository<ItemCatalogoEntity, Long> {

    List<ItemCatalogoEntity> findByActivoTrue();

    Optional<ItemCatalogoEntity> findByIdPrendaAndActivoTrue(Long idPrenda);

    boolean existsByIdPrenda(Long idPrenda);
}

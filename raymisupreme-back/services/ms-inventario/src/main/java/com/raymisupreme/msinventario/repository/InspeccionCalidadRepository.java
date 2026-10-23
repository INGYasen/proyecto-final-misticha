package com.raymisupreme.msinventario.repository;

import com.raymisupreme.msinventario.entity.InspeccionCalidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspeccionCalidadRepository extends JpaRepository<InspeccionCalidadEntity, Long> {

    List<InspeccionCalidadEntity> findByPrenda_IdPrenda(Long idPrenda);
}
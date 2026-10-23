package com.raymisupreme.mscatalogo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_catalogo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCatalogoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    @Column(name = "id_prenda", nullable = false, unique = true)
    private Long idPrenda;

    @Column(name = "url_foto", nullable = false, length = 500)
    private String urlFoto;

    @Column(name = "descripcion_publica", length = 500)
    private String descripcionPublica;

    @Column(nullable = false)
    private Boolean destacado = false;

    @Column(nullable = false)
    private Boolean activo = true;
}

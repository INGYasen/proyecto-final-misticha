package com.raymisupreme.msinventario.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prenda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrendaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenda")
    private Long idPrenda;

    @Column(name = "id_lote", nullable = false)
    private Long idLote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaEntity categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "publico", nullable = false, length = 20)
    private PublicoObjetivo publico = PublicoObjetivo.UNISEX;

    // --- AQUÍ ESTÁN LOS CAMPOS CORREGIDOS SEGÚN TU SQL ---
    @Column(name = "codigo_unico", nullable = false, unique = true, length = 50)
    private String codigoUnico;

    @Column(name = "nombre_prenda", nullable = false, length = 200)
    private String nombrePrenda;

    @Column(name = "talla", length = 20)
    private String talla;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "material", length = 100)
    private String material;

    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "url_foto", length = 500)
    private String urlFoto;

    @OneToMany(mappedBy = "prenda", fetch = FetchType.LAZY)
    private List<InspeccionCalidadEntity> inspecciones = new ArrayList<>();
}

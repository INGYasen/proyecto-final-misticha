package com.raymisupreme.msacopio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artesano")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtesanoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArtesano;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, length = 15)
    private String dni;

    @Column(name = "comunidad_o_zona", length = 100)
    private String comunidadOZona;

    @Column(length = 20)
    private String telefono;
}
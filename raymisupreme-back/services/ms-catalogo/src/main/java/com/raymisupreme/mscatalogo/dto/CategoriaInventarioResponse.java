package com.raymisupreme.mscatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaInventarioResponse {
    private Long idCategoria;
    private String nombre;
    private String descripcion;
}

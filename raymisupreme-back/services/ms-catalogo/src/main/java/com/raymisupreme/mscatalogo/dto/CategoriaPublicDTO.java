package com.raymisupreme.mscatalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaPublicDTO {

    private Long id;
    private String nombre;
    private String slug;
    private Boolean destacada;
}

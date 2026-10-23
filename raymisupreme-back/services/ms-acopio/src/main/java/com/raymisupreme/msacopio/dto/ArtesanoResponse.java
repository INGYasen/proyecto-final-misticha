package com.raymisupreme.msacopio.dto;

import lombok.Data;

@Data
public class ArtesanoResponse {
    private Long idArtesano;
    private String nombres;
    private String apellidos;
    private String dni;
    private String comunidadOZona;
    private String telefono;
}
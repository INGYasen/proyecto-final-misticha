package com.raymisupreme.msventas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException ex) {
        String mensaje = ex.getMessage() == null ? "Solicitud no valida" : ex.getMessage();
        HttpStatus estado = mensaje.toLowerCase().contains("no encontrado")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.CONFLICT;
        return ResponseEntity.status(estado).body(Map.of("error", mensaje));
    }

    @ExceptionHandler(MicroserviceUnavailableException.class)
    public ResponseEntity<Map<String, String>> microservicioCaido(MicroserviceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "mensaje", "El microservicio requerido no responde. El resto de la tienda sigue disponible."
                ));
    }
}

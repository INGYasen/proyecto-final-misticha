package com.raymisupreme.mspagos.controller;

import com.raymisupreme.mspagos.dto.MercadoPagoPaymentResponse;
import com.raymisupreme.mspagos.exception.MercadoPagoPaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PagoController.class)
public class MercadoPagoExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoExceptionHandler.class);

    @ExceptionHandler(MercadoPagoPaymentException.class)
    ResponseEntity<MercadoPagoPaymentResponse> mercadoPagoError(MercadoPagoPaymentException ex) {
        log.error("Mercado Pago rechazo la solicitud. status={}, response={}", ex.getStatusCode(), ex.getApiError(), ex);
        return ResponseEntity.status(ex.getStatusCode()).body(new MercadoPagoPaymentResponse(
                null, "error", null, ex.getMessage(), ex.getApiError()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<MercadoPagoPaymentResponse> validationError(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst().orElse("Payload invalido");
        log.warn("Payload de pago invalido: {}", detail);
        return ResponseEntity.badRequest().body(new MercadoPagoPaymentResponse(
                null, "error", null, "Datos de pago incompletos", detail));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<MercadoPagoPaymentResponse> businessError(IllegalArgumentException ex) {
        log.warn("Solicitud de pago rechazada: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new MercadoPagoPaymentResponse(
                null, "error", null, ex.getMessage(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<MercadoPagoPaymentResponse> unexpectedError(Exception ex) {
        log.error("Error procesando pago", ex);
        return ResponseEntity.internalServerError().body(new MercadoPagoPaymentResponse(
                null, "error", null, "Error interno procesando el pago", ex.getMessage()));
    }
}

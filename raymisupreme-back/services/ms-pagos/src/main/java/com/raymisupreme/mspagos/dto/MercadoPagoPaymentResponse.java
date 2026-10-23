package com.raymisupreme.mspagos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MercadoPagoPaymentResponse(
        String paymentId,
        String status,
        @JsonProperty("status_detail") String statusDetail,
        String message,
        String error
) {
}

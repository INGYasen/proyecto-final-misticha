package com.raymisupreme.mspagos.dto;

public record StripeSessionResponse(String sessionId, String paymentStatus, String status,
        Long amountTotal, String currency, String paymentIntent, String ventaId) {}

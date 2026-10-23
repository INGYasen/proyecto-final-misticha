package com.raymisupreme.mspagos.dto;

import jakarta.validation.constraints.NotNull;

public record StripeCheckoutRequest(@NotNull Long ventaId) {}

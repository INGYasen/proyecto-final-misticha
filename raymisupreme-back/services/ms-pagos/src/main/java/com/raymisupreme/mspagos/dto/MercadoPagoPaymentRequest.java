package com.raymisupreme.mspagos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class MercadoPagoPaymentRequest {
    @NotNull
    private Long pedidoId;
    @NotBlank
    private String token;
    @NotNull @DecimalMin(value = "0.01")
    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;
    @NotBlank
    @JsonProperty("payment_method_id")
    private String paymentMethodId;
    @JsonProperty("issuer_id")
    private String issuerId;
    @JsonProperty("device_id")
    private String deviceId;
    @NotNull @Min(1)
    private Integer installments;
    @NotNull @Valid
    private Payer payer;

    @Data
    public static class Payer {
        @NotBlank @Email
        private String email;
        @Valid
        private Identification identification;
    }

    @Data
    public static class Identification {
        private String type;
        private String number;
    }
}

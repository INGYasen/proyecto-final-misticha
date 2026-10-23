package com.raymisupreme.mspagos.exception;

public class MercadoPagoPaymentException extends RuntimeException {
    private final int statusCode;
    private final String apiError;

    public MercadoPagoPaymentException(int statusCode, String message, String apiError, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.apiError = apiError;
    }

    public int getStatusCode() { return statusCode; }
    public String getApiError() { return apiError; }
}

package com.raymisupreme.mspagos.service.impl;

import com.raymisupreme.mspagos.client.VentasClient;
import com.raymisupreme.mspagos.dto.StripeCheckoutResponse;
import com.raymisupreme.mspagos.dto.StripeSessionResponse;
import com.raymisupreme.mspagos.dto.ventas.DetallePedidoVentasResponse;
import com.raymisupreme.mspagos.dto.ventas.PedidoVentasResponse;
import com.raymisupreme.mspagos.entity.PagoEntity;
import com.raymisupreme.mspagos.repository.PagoRepository;
import com.raymisupreme.mspagos.service.StripeCheckoutService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StripeCheckoutServiceImpl implements StripeCheckoutService {
    private final VentasClient ventasClient;
    private final PagoRepository pagoRepository;
    private final String secretKey;
    private final String currency;
    private final String successUrl;
    private final String cancelUrl;
    private final String internalSecret;

    public StripeCheckoutServiceImpl(VentasClient ventasClient, PagoRepository pagoRepository,
            @Value("${stripe.secret-key}") String secretKey,
            @Value("${stripe.currency}") String currency,
            @Value("${stripe.success-url}") String successUrl,
            @Value("${stripe.cancel-url}") String cancelUrl,
            @Value("${internal.payment-secret}") String internalSecret) {
        this.ventasClient = ventasClient;
        this.pagoRepository = pagoRepository;
        this.secretKey = secretKey;
        this.currency = currency.toLowerCase();
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.internalSecret = internalSecret;
    }

    @Override
    @Transactional
    public StripeCheckoutResponse crearSesion(Long ventaId) {
        PedidoVentasResponse pedido = ventasClient.obtenerPedido(ventaId);
        List<DetallePedidoVentasResponse> detalles = ventasClient.obtenerDetalles(ventaId);
        if (detalles == null || detalles.isEmpty()) throw new IllegalArgumentException("La venta no contiene productos");

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setClientReferenceId(String.valueOf(ventaId))
                .putMetadata("ventaId", String.valueOf(ventaId))
                .putMetadata("proveedor", "STRIPE")
                .putMetadata("proyecto", "RAYMI SUPRÊME");
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedidoVentasResponse detalle : detalles) {
            if (detalle.getPrecioUnitario() == null || detalle.getCantidad() == null || detalle.getCantidad() <= 0)
                throw new IllegalArgumentException("Producto invalido en la venta");
            long cents = detalle.getPrecioUnitario().movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
            total = total.add(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            builder.addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(detalle.getCantidad().longValue())
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency)
                            .setUnitAmount(cents)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Prenda Raymi Suprême #" + detalle.getIdPrenda()).build())
                            .build()).build());
        }
        try {
            Stripe.apiKey = secretKey;
            Session session = Session.create(builder.build());
            LocalDateTime now = LocalDateTime.now();
            pagoRepository.save(PagoEntity.builder()
                    .pedidoId(ventaId).clienteId(pedido.getIdCliente()).monto(total)
                    .metodoPago("STRIPE_CHECKOUT").estado("PENDIENTE")
                    .moneda(currency.toUpperCase()).proveedorPago("STRIPE")
                    .stripeSessionId(session.getId()).stripePaymentIntentId(session.getPaymentIntent())
                    .fechaPago(now).creadoEn(now).actualizadoEn(now).build());
            return new StripeCheckoutResponse(session.getId(), session.getUrl());
        } catch (StripeException ex) {
            throw new IllegalStateException("Stripe no pudo crear la sesion: " + ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public StripeSessionResponse consultarSesion(String sessionId) {
        try {
            Stripe.apiKey = secretKey;
            Session session = Session.retrieve(sessionId);
            PagoEntity pago = pagoRepository.findByStripeSessionId(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Sesion Stripe no registrada"));
            if ("paid".equalsIgnoreCase(session.getPaymentStatus()) && !"APROBADO".equals(pago.getEstado())) {
                pago.setEstado("APROBADO");
                pago.setStripePaymentIntentId(session.getPaymentIntent());
                pago.setFechaConfirmacion(LocalDateTime.now());
                pago.setActualizadoEn(LocalDateTime.now());
                ventasClient.confirmarPago(pago.getPedidoId(), internalSecret);
                pagoRepository.save(pago);
            }
            return new StripeSessionResponse(session.getId(), session.getPaymentStatus(), session.getStatus(),
                    session.getAmountTotal(), session.getCurrency(), session.getPaymentIntent(),
                    session.getMetadata().getOrDefault("ventaId", String.valueOf(pago.getPedidoId())));
        } catch (StripeException ex) {
            throw new IllegalStateException("Stripe no pudo consultar la sesion: " + ex.getMessage(), ex);
        }
    }
}

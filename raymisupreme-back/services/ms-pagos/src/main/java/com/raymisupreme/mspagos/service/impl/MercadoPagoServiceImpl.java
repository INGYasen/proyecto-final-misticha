package com.raymisupreme.mspagos.service.impl;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentAdditionalInfoRequest;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentItemRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import com.raymisupreme.mspagos.client.VentasClient;
import com.raymisupreme.mspagos.dto.PagoResponse;
import com.raymisupreme.mspagos.dto.MercadoPagoPreferenciaResponse;
import com.raymisupreme.mspagos.dto.MercadoPagoPaymentRequest;
import com.raymisupreme.mspagos.dto.MercadoPagoPaymentResponse;
import com.raymisupreme.mspagos.dto.ventas.DetallePedidoVentasResponse;
import com.raymisupreme.mspagos.dto.ventas.PedidoVentasResponse;
import com.raymisupreme.mspagos.entity.PagoEntity;
import com.raymisupreme.mspagos.exception.MercadoPagoPaymentException;
import com.raymisupreme.mspagos.repository.PagoRepository;
import com.raymisupreme.mspagos.service.MercadoPagoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.time.ZoneId;
import java.util.Locale;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoServiceImpl.class);
    private static final String MONEDA = "PEN";

    private final VentasClient ventasClient;
    private final PagoRepository pagoRepository;
    private final String accessToken;
    private final String notificationUrl;
    private final String webhookSecret;
    private final String internalPaymentSecret;

    public MercadoPagoServiceImpl(VentasClient ventasClient,
                                  PagoRepository pagoRepository,
                                  @Value("${mercadopago.access-token}") String accessToken,
                                  @Value("${mercadopago.notification-url}") String notificationUrl,
                                  @Value("${mercadopago.webhook-secret:}") String webhookSecret,
                                  @Value("${internal.payment-secret}") String internalPaymentSecret) {
        this.ventasClient = ventasClient;
        this.pagoRepository = pagoRepository;
        this.accessToken = accessToken;
        this.notificationUrl = notificationUrl;
        this.webhookSecret = webhookSecret;
        this.internalPaymentSecret = internalPaymentSecret;
    }

    @Override
    public boolean firmaWebhookValida(String dataId, String requestId, String signature) {
        if (webhookSecret == null || webhookSecret.isBlank() || signature == null || requestId == null || dataId == null) {
            log.warn("Webhook de Mercado Pago rechazado: falta firma o secreto");
            return false;
        }
        String ts = null;
        String v1 = null;
        for (String parte : signature.split(",")) {
            String[] par = parte.split("=", 2);
            if (par.length != 2) continue;
            if ("ts".equals(par[0].trim())) ts = par[1].trim();
            if ("v1".equals(par[0].trim())) v1 = par[1].trim();
        }
        if (ts == null || v1 == null) return false;
        String id = dataId.matches("(?i)[a-z0-9]+") ? dataId.toLowerCase(Locale.ROOT) : dataId;
        String manifiesto = "id:" + id + ";request-id:" + requestId + ";ts:" + ts + ";";
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] calculado = mac.doFinal(manifiesto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(calculado.length * 2);
            for (byte b : calculado) hex.append(String.format("%02x", b));
            return MessageDigest.isEqual(hex.toString().getBytes(StandardCharsets.UTF_8), v1.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            log.warn("No se pudo verificar la firma del webhook de Mercado Pago");
            return false;
        }
    }

    private PaymentPayerRequest pagador(MercadoPagoPaymentRequest request) {
        var payer = PaymentPayerRequest.builder().email(request.getPayer().getEmail());
        var identificacion = request.getPayer().getIdentification();
        if (identificacion != null
                && identificacion.getType() != null && !identificacion.getType().isBlank()
                && identificacion.getNumber() != null && !identificacion.getNumber().isBlank()) {
            payer.identification(IdentificationRequest.builder()
                    .type(identificacion.getType())
                    .number(identificacion.getNumber())
                    .build());
        }
        return payer.build();
    }

    @Override
    @Transactional
    public MercadoPagoPaymentResponse crearPago(MercadoPagoPaymentRequest request) {
        var pagoExistente = pagoRepository.findTopByPedidoIdAndEstadoInOrderByIdDesc(
                request.getPedidoId(), List.of("APROBADO", "CONFIRMADO"));
        if (pagoExistente.isPresent()) {
            PagoEntity pago = pagoExistente.get();
            log.info("Pedido {} ya tiene pago aprobado {}; no se crea otro cobro",
                    request.getPedidoId(), pago.getMercadoPagoPaymentId());
            return new MercadoPagoPaymentResponse(pago.getMercadoPagoPaymentId(), "approved",
                    pago.getStatusDetail(), "El pedido ya tiene un pago aprobado", null);
        }
        PedidoVentasResponse pedido = ventasClient.obtenerPedido(request.getPedidoId());
        validarPropietario(pedido);
        List<DetallePedidoVentasResponse> detalles = ventasClient.obtenerDetalles(request.getPedidoId());
        BigDecimal total = calcularTotal(detalles).setScale(2, java.math.RoundingMode.HALF_UP);
        if (request.getTransactionAmount().compareTo(total) != 0) {
            log.warn("Monto del Brick {} difiere del total backend {} para pedido {}; se usara el backend",
                    request.getTransactionAmount(), total, request.getPedidoId());
        }
        String issuerId = request.getIssuerId();
        if (issuerId != null && issuerId.isBlank()) issuerId = null;
        int cuotas = "yape".equalsIgnoreCase(request.getPaymentMethodId()) ? 1 : request.getInstallments();

        List<PaymentItemRequest> items = detalles.stream()
                .map(detalle -> PaymentItemRequest.builder()
                        .id(String.valueOf(detalle.getIdPrenda()))
                        .title("Prenda Raymi Supreme " + detalle.getIdPrenda())
                        .description("Pedido " + request.getPedidoId())
                        .quantity(detalle.getCantidad())
                        .unitPrice(detalle.getPrecioUnitario())
                        .build())
                .toList();
        var paymentBuilder = PaymentCreateRequest.builder()
                .transactionAmount(total)
                .token(request.getToken())
                .description("Pedido Raymi Supreme " + request.getPedidoId())
                .statementDescriptor("RAYMISUPREME")
                .installments(cuotas)
                .paymentMethodId(request.getPaymentMethodId())
                .issuerId(issuerId)
                .additionalInfo(PaymentAdditionalInfoRequest.builder().items(items).build())
                .payer(pagador(request))
                .externalReference(String.valueOf(request.getPedidoId()));
        String notificacion = urlNotificacion();
        if (notificacion != null) paymentBuilder.notificationUrl(notificacion);
        PaymentCreateRequest paymentRequest = paymentBuilder.build();
        // El mismo token reintentado conserva la clave; un nuevo intento del Brick genera otro token.
        String idempotencyKey = UUID.nameUUIDFromBytes(
                ("raymisupreme-payment-" + request.getPedidoId() + ":" + request.getToken())
                        .getBytes(StandardCharsets.UTF_8)).toString();
        Map<String, String> headers = new java.util.HashMap<>();
        headers.put("X-Idempotency-Key", idempotencyKey);
        if (request.getDeviceId() != null && !request.getDeviceId().isBlank()) {
            headers.put("X-meli-session-id", request.getDeviceId());
        }
        MPRequestOptions options = MPRequestOptions.builder()
                .customHeaders(headers)
                .build();

        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            Payment payment = new PaymentClient().create(paymentRequest, options);
            String estado = mapearEstadoInicial(payment.getStatus());
            LocalDateTime ahora = LocalDateTime.now();
            PagoEntity pago = PagoEntity.builder()
                    .pedidoId(request.getPedidoId())
                    .clienteId(pedido.getIdCliente())
                    .monto(total)
                    .metodoPago(payment.getPaymentMethodId())
                    .estado(estado)
                    .referenciaPago(String.valueOf(payment.getId()))
                    .mercadoPagoPaymentId(String.valueOf(payment.getId()))
                    .statusDetail(payment.getStatusDetail())
                    .moneda(MONEDA)
                    .proveedorPago("MERCADO_PAGO")
                    .fechaPago(ahora)
                    .fechaConfirmacion("CONFIRMADO".equals(estado) && payment.getDateApproved() != null
                            ? payment.getDateApproved().atZoneSameInstant(ZoneId.of("America/Lima")).toLocalDateTime()
                            : null)
                    .creadoEn(ahora)
                    .actualizadoEn(ahora)
                    .build();
            pagoRepository.save(pago);
            if ("approved".equalsIgnoreCase(payment.getStatus())) {
                pago.setEstado("CONFIRMADO");
                pago.setFechaConfirmacion(payment.getDateApproved() == null
                        ? ahora
                        : payment.getDateApproved().atZoneSameInstant(ZoneId.of("America/Lima")).toLocalDateTime());
                pagoRepository.save(pago);
                try {
                    ventasClient.confirmarPago(request.getPedidoId(), internalPaymentSecret);
                } catch (RuntimeException ex) {
                    log.error("Mercado Pago aprobo el pago {} del pedido {}, pero la confirmacion local fallo",
                            payment.getId(), request.getPedidoId(), ex);
                }
            }
            log.info("Pago Brick creado. paymentId={}, pedidoId={}, status={}",
                    payment.getId(), request.getPedidoId(), payment.getStatus());
            String message = switch (payment.getStatus() == null ? "" : payment.getStatus().toLowerCase(Locale.ROOT)) {
                case "approved" -> "Pago aprobado; esperando confirmacion del webhook";
                case "rejected" -> "Pago rechazado: " + payment.getStatusDetail();
                case "pending", "in_process" -> "Pago pendiente: " + payment.getStatusDetail();
                default -> "Resultado recibido de Mercado Pago: " + payment.getStatus();
            };
            return new MercadoPagoPaymentResponse(String.valueOf(payment.getId()), payment.getStatus(),
                    payment.getStatusDetail(), message, null);
        } catch (com.mercadopago.exceptions.MPApiException ex) {
            String apiError = ex.getApiResponse() == null ? ex.getMessage() : ex.getApiResponse().getContent();
            throw new MercadoPagoPaymentException(
                    ex.getStatusCode() > 0 ? ex.getStatusCode() : 502,
                    "Mercado Pago no pudo crear el pago", apiError, ex);
        } catch (MPException ex) {
            throw new MercadoPagoPaymentException(502,
                    "No se pudo comunicar con Mercado Pago", ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public MercadoPagoPreferenciaResponse crearPreferencia(Long pedidoId) {
        PedidoVentasResponse pedido = ventasClient.obtenerPedido(pedidoId);
        validarPropietario(pedido);

        List<DetallePedidoVentasResponse> detalles = ventasClient.obtenerDetalles(pedidoId);
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido no contiene items");
        }

        List<PreferenceItemRequest> items = detalles.stream().map(detalle -> {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0
                    || detalle.getPrecioUnitario() == null
                    || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El pedido contiene un item invalido");
            }
            return PreferenceItemRequest.builder()
                    .id(String.valueOf(detalle.getIdPrenda()))
                    .title("Prenda Raymi Suprême #" + detalle.getIdPrenda())
                    .quantity(detalle.getCantidad())
                    .currencyId(MONEDA)
                    .unitPrice(detalle.getPrecioUnitario())
                    .build();
        }).toList();

        BigDecimal total = detalles.stream()
                .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost:4200/checkout/success")
                .failure("http://localhost:4200/checkout/failure")
                .pending("http://localhost:4200/checkout/pending")
                .build();
        var preferenceBuilder = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .externalReference(String.valueOf(pedidoId));
        String notificacion = urlNotificacion();
        if (notificacion != null) preferenceBuilder.notificationUrl(notificacion);
        PreferenceRequest preferenceRequest = preferenceBuilder.build();

        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            Preference preference = new PreferenceClient().create(preferenceRequest);
            LocalDateTime ahora = LocalDateTime.now();
            pagoRepository.save(PagoEntity.builder()
                    .pedidoId(pedidoId)
                    .clienteId(pedido.getIdCliente())
                    .monto(total)
                    .metodoPago("MERCADO_PAGO")
                    .estado("PENDIENTE")
                    .referenciaPago(preference.getId())
                    .preferenceId(preference.getId())
                    .moneda(MONEDA)
                    .proveedorPago("MERCADO_PAGO")
                    .fechaPago(ahora)
                    .creadoEn(ahora)
                    .actualizadoEn(ahora)
                    .build());
            String initPoint = accessToken.startsWith("TEST-")
                    ? preference.getSandboxInitPoint()
                    : preference.getInitPoint();
            return new MercadoPagoPreferenciaResponse(preference.getId(), initPoint);
        } catch (MPException | com.mercadopago.exceptions.MPApiException ex) {
            throw new IllegalStateException("No se pudo crear la preferencia de Mercado Pago", ex);
        }
    }

    @Override
    @Transactional
    public void procesarWebhook(String type, String topic, String id, Map<String, Object> payload) {
        log.info("Webhook de Mercado Pago recibido. type={}, topic={}, id={}", type, topic, id);
        String action = valorTexto(payload.get("action"));
        String notificationType = primerNoVacio(type, topic, valorTexto(payload.get("type")));
        boolean esPago = "payment".equalsIgnoreCase(notificationType)
                || (action != null && (action.equalsIgnoreCase("payment.created")
                || action.equalsIgnoreCase("payment.updated")));
        if (!esPago) {
            log.info("Webhook ignorado: no corresponde a una notificacion de payment");
            return;
        }

        String paymentId = obtenerPaymentId(id, payload);
        if (paymentId == null) {
            log.warn("Webhook payment sin paymentId; se responde OK sin procesar");
            return;
        }
        log.info("PaymentId recibido: {}", paymentId);

        Payment payment = consultarPago(paymentId);
        String externalReference = payment.getExternalReference();
        Long pedidoId = parseLong(externalReference);
        String preferenceId = obtenerPreferenceId(payload);
        PagoEntity pago = buscarPago(paymentId, pedidoId, preferenceId);

        log.info("Pago consultado en Mercado Pago. paymentId={}, status={}, statusDetail={}, pedidoId={}",
                payment.getId(), payment.getStatus(), payment.getStatusDetail(), pedidoId);

        if ("CONFIRMADO".equals(pago.getEstado())) {
            validarImporteYMoneda(pago, payment);
            ventasClient.confirmarPago(pago.getPedidoId(), internalPaymentSecret);
            log.info("Pago {} ya estaba CONFIRMADO; webhook idempotente", pago.getId());
            return;
        }

        validarImporteYMoneda(pago, payment);
        String estado = mapearEstado(payment.getStatus());
        pago.setMercadoPagoPaymentId(String.valueOf(payment.getId()));
        pago.setEstado(estado);
        pago.setStatusDetail(payment.getStatusDetail());
        pago.setMetodoPago(payment.getPaymentMethodId() == null ? "MERCADO_PAGO" : payment.getPaymentMethodId());
        pago.setActualizadoEn(LocalDateTime.now());
        if ("CONFIRMADO".equals(estado)) {
            pago.setFechaConfirmacion(payment.getDateApproved() == null
                    ? LocalDateTime.now()
                    : payment.getDateApproved().atZoneSameInstant(ZoneId.of("America/Lima")).toLocalDateTime());
            ventasClient.confirmarPago(pago.getPedidoId(), internalPaymentSecret);
            log.info("Pedido {} confirmado por pago aprobado", pago.getPedidoId());
        } else {
            log.info("Pedido {} no confirmado porque el pago esta en estado {}", pago.getPedidoId(), estado);
        }
        pagoRepository.save(pago);
        log.info("Pago local {} actualizado a {}", pago.getId(), estado);
    }

    private String urlNotificacion() {
        if (notificationUrl != null && notificationUrl.startsWith("https://")) {
            return notificationUrl;
        }
        log.info("Notificacion de Mercado Pago omitida: la URL local no es publica");
        return null;
    }

    private Payment consultarPago(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            return new PaymentClient().get(Long.valueOf(paymentId));
        } catch (MPException | com.mercadopago.exceptions.MPApiException | NumberFormatException ex) {
            throw new IllegalStateException("No se pudo consultar el pago en Mercado Pago: " + paymentId, ex);
        }
    }

    private PagoEntity buscarPago(String paymentId, Long pedidoId, String preferenceId) {
        var pagoPorPaymentId = pagoRepository.findWithLockByMercadoPagoPaymentId(paymentId);
        if (pagoPorPaymentId.isPresent()) {
            return pagoPorPaymentId.get();
        }
        if (pedidoId != null) {
            return pagoRepository.findWithLockByPedidoId(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe pago para pedido " + pedidoId));
        }
        if (preferenceId != null) {
            return pagoRepository.findByPreferenceId(preferenceId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe pago para preferencia " + preferenceId));
        }
        return pagoRepository.findByMercadoPagoPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("No se pudo relacionar el pago " + paymentId));
    }

    private void validarImporteYMoneda(PagoEntity pago, Payment payment) {
        if (payment.getTransactionAmount() == null
                || pago.getMonto().compareTo(payment.getTransactionAmount()) != 0
                || payment.getCurrencyId() == null
                || !pago.getMoneda().equalsIgnoreCase(payment.getCurrencyId())) {
            throw new IllegalStateException("Importe o moneda de Mercado Pago no coincide con la preferencia local");
        }
    }

    private String mapearEstado(String status) {
        if (status == null) return "PENDIENTE";
        return switch (status.toLowerCase(Locale.ROOT)) {
            case "approved" -> "CONFIRMADO";
            case "rejected" -> "RECHAZADO";
            case "cancelled" -> "CANCELADO";
            case "refunded" -> "REEMBOLSADO";
            case "authorized" -> "AUTORIZADO";
            case "pending", "in_process" -> "PENDIENTE";
            default -> "PENDIENTE";
        };
    }

    private String mapearEstadoInicial(String status) {
        if (status == null) return "PENDIENTE";
        return switch (status.toLowerCase(Locale.ROOT)) {
            case "approved" -> "APROBADO";
            case "rejected" -> "RECHAZADO";
            case "pending", "in_process" -> "PENDIENTE";
            default -> mapearEstado(status);
        };
    }

    private BigDecimal calcularTotal(List<DetallePedidoVentasResponse> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido no contiene items");
        }
        return detalles.stream().map(detalle -> {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0
                    || detalle.getPrecioUnitario() == null
                    || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El pedido contiene un item invalido");
            }
            return detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @SuppressWarnings("unchecked")
    private String obtenerPaymentId(String queryId, Map<String, Object> payload) {
        Object data = payload.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            String dataId = valorTexto(dataMap.get("id"));
            if (dataId != null) return dataId;
        }
        return primerNoVacio(queryId, valorTexto(payload.get("id")));
    }

    private String obtenerPreferenceId(Map<String, Object> payload) {
        String direct = primerNoVacio(valorTexto(payload.get("preference_id")), valorTexto(payload.get("preferenceId")));
        Object data = payload.get("data");
        if (direct == null && data instanceof Map<?, ?> dataMap) {
            direct = primerNoVacio(valorTexto(dataMap.get("preference_id")), valorTexto(dataMap.get("preferenceId")));
        }
        return direct;
    }

    private String valorTexto(Object value) {
        if (value == null) return null;
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private String primerNoVacio(String... values) {
        for (String value : values) if (value != null && !value.isBlank()) return value;
        return null;
    }

    private Long parseLong(String value) {
        try { return value == null || value.isBlank() ? null : Long.valueOf(value); }
        catch (NumberFormatException ex) { return null; }
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponse consultarPagoPropio(Long pedidoId) {
        PedidoVentasResponse pedido = ventasClient.obtenerPedido(pedidoId);
        validarPropietario(pedido);
        PagoEntity pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        PagoResponse response = new PagoResponse();
        response.setId(pago.getId());
        response.setPedidoId(pago.getPedidoId());
        response.setClienteId(pago.getClienteId());
        response.setMonto(pago.getMonto());
        response.setMetodoPago(pago.getMetodoPago());
        response.setEstado(pago.getEstado());
        response.setStatusDetail(pago.getStatusDetail());
        response.setReferenciaPago(pago.getReferenciaPago());
        response.setFechaPago(pago.getFechaPago());
        return response;
    }

    private void validarPropietario(PedidoVentasResponse pedido) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usuarioId = principal instanceof Jwt jwt ? jwt.getSubject() : null;
        String propietarioId = pedido.getCliente() == null ? null : pedido.getCliente().getKeycloakUserId();
        boolean admin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> "ROLE_SUPER_ADMIN".equals(a.getAuthority()));
        if (!admin && (usuarioId == null || !usuarioId.equals(propietarioId))) {
            throw new AccessDeniedException("El pedido no pertenece al usuario autenticado");
        }
    }
}

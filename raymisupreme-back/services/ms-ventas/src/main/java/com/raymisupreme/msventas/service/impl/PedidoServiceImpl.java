package com.raymisupreme.msventas.service.impl;

import com.raymisupreme.msventas.dto.CarritoResponse;
import com.raymisupreme.msventas.dto.CheckoutResponse;
import com.raymisupreme.msventas.dto.ConfirmarPedidoRequest;
import com.raymisupreme.msventas.dto.ItemCarritoResponse;
import com.raymisupreme.msventas.dto.PagoRequest;
import com.raymisupreme.msventas.dto.PagoResponse;
import com.raymisupreme.msventas.dto.PedidoRequest;
import com.raymisupreme.msventas.dto.PedidoResponse;
import com.raymisupreme.msventas.dto.StockResponse;
import com.raymisupreme.msventas.entity.ClienteEntity;
import com.raymisupreme.msventas.entity.PedidoEntity;
import com.raymisupreme.msventas.entity.DetallePedidoEntity;
import com.raymisupreme.msventas.event.PedidoEventoPublisher;
import com.raymisupreme.msventas.exception.MicroserviceUnavailableException;
import com.raymisupreme.msventas.mapper.PedidoMapper;
import com.raymisupreme.msventas.repository.ClienteRepository;
import com.raymisupreme.msventas.repository.PedidoRepository;
import com.raymisupreme.msventas.repository.DetallePedidoRepository;
import com.raymisupreme.msventas.service.CarritoIntegrationService;
import com.raymisupreme.msventas.service.InventarioIntegrationService;
import com.raymisupreme.msventas.service.PagosIntegrationService;
import com.raymisupreme.msventas.service.PedidoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PAGADO = "PAGADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";
    private static final String PAGO_EXITOSO = "EXITOSO";

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ClienteRepository clienteRepository;
    private final CarritoIntegrationService carritoIntegrationService;
    private final InventarioIntegrationService inventarioIntegrationService;
    private final PagosIntegrationService pagosIntegrationService;
    private final PedidoEventoPublisher pedidoEventoPublisher;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             DetallePedidoRepository detallePedidoRepository,
                             ClienteRepository clienteRepository,
                             CarritoIntegrationService carritoIntegrationService,
                             InventarioIntegrationService inventarioIntegrationService,
                             PagosIntegrationService pagosIntegrationService,
                             PedidoEventoPublisher pedidoEventoPublisher) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.clienteRepository = clienteRepository;
        this.carritoIntegrationService = carritoIntegrationService;
        this.inventarioIntegrationService = inventarioIntegrationService;
        this.pagosIntegrationService = pagosIntegrationService;
        this.pedidoEventoPublisher = pedidoEventoPublisher;
    }

    @Override
    public PedidoResponse crear(PedidoRequest request) {
        PedidoEntity entity = PedidoMapper.toEntity(request);
        PedidoEntity guardado = pedidoRepository.save(entity);
        PedidoResponse respuesta = PedidoMapper.toResponse(guardado);
        pedidoEventoPublisher.pedidoCreado(respuesta);
        return respuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Long idPedido) {
        PedidoEntity entity = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));
        return PedidoMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepository.findAll().stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPorCliente(Long idCliente) {
        return pedidoRepository.findByCliente_IdCliente(idCliente).stream()
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> listarDelUsuario(String keycloakUserId, String email) {
        ClienteEntity cliente = clienteDe(keycloakUserId, email);
        return pedidoRepository.findByCliente_IdCliente(cliente.getIdCliente()).stream()
                .sorted(Comparator.comparing(PedidoEntity::getFechaPedido).reversed())
                .map(PedidoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void verificarPropietario(Long idPedido, String keycloakUserId, String email) {
        ClienteEntity cliente = clienteDe(keycloakUserId, email);
        PedidoEntity pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
        Long dueno = pedido.getCliente().getIdCliente();
        if (!dueno.equals(cliente.getIdCliente())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este pedido no pertenece a tu cuenta");
        }
    }

    private ClienteEntity clienteDe(String keycloakUserId, String email) {
        Optional<ClienteEntity> porUsuario = keycloakUserId == null
                ? Optional.empty()
                : clienteRepository.findByKeycloakUserId(keycloakUserId);
        return porUsuario
                .or(() -> email == null || email.isBlank()
                        ? Optional.empty()
                        : clienteRepository.findByEmailIgnoreCase(email))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay un perfil de cliente para esta cuenta"));
    }

    @Override
    public PedidoResponse actualizar(Long idPedido, PedidoRequest request) {
        PedidoEntity entity = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));
        PedidoMapper.copyToEntity(request, entity);
        PedidoEntity actualizado = pedidoRepository.save(entity);
        return PedidoMapper.toResponse(actualizado);
    }

    @Override
    public PedidoResponse confirmarPedido(Long idPedido, ConfirmarPedidoRequest request) {
        PedidoEntity pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));

        if (!esPedidoPendiente(pedido.getEstadoPedido())) {
            throw new IllegalArgumentException("El pedido no está pendiente de pago: " + idPedido);
        }

        Long idCliente = pedido.getCliente() != null ? pedido.getCliente().getIdCliente() : null;
        if (idCliente == null) {
            pedido.setEstadoPedido(ESTADO_CANCELADO);
            return PedidoMapper.toResponse(pedidoRepository.save(pedido));
        }

        PagoRequest pagoRequest = new PagoRequest();
        pagoRequest.setPedidoId(idPedido);
        pagoRequest.setClienteId(idCliente);
        pagoRequest.setMonto(pedido.getTotalPagar());
        pagoRequest.setMetodoPago(request.getMetodoPago());

        List<DetallePedidoEntity> detalles = detallePedidoRepository.findByPedido_IdPedido(idPedido);
        validarStockDetalles(detalles);

        PagoResponse pagoResponse = pagosIntegrationService.procesarPago(pagoRequest);
        if (PAGO_EXITOSO.equalsIgnoreCase(pagoResponse.getEstado())) {
            pedido.setEstadoPedido(ESTADO_PAGADO);

            for (DetallePedidoEntity detalle : detalles) {
                inventarioIntegrationService.descontarStock(detalle.getIdPrenda(), detalle.getCantidad());
            }
        } else {
            pedido.setEstadoPedido(ESTADO_CANCELADO);
        }

        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponse confirmarPagoMercadoPago(Long idPedido) {
        PedidoEntity pedido = pedidoRepository.findWithLockByIdPedido(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + idPedido));
        if (ESTADO_PAGADO.equalsIgnoreCase(pedido.getEstadoPedido())
                || "CONFIRMADO".equalsIgnoreCase(pedido.getEstadoPedido())) {
            log.info("Pedido {} ya estaba {}; confirmacion idempotente", idPedido, pedido.getEstadoPedido());
            return PedidoMapper.toResponse(pedido);
        }
        if (!esPedidoPendiente(pedido.getEstadoPedido())) {
            throw new IllegalArgumentException("El pedido no esta pendiente: " + idPedido);
        }
        List<DetallePedidoEntity> detalles = detallePedidoRepository.findByPedido_IdPedido(idPedido);
        validarStockDetalles(detalles);
        for (DetallePedidoEntity detalle : detalles) {
            inventarioIntegrationService.descontarStock(detalle.getIdPrenda(), detalle.getCantidad());
        }
        pedido.setEstadoPedido(ESTADO_PAGADO);
        log.info("Pedido {} marcado PAGADO por webhook de Mercado Pago", idPedido);
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponse cancelarPedido(Long idPedido) {
        PedidoEntity pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido no encontrado: " + idPedido));
        if (!esPedidoPendiente(pedido.getEstadoPedido())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede cancelar un pedido pendiente");
        }
        pedido.setEstadoPedido(ESTADO_CANCELADO);
        return PedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    // Convierte el carrito en pedido pendiente. El cobro se realiza al confirmar el pedido.
    @Override
    public CheckoutResponse checkout(Long idCliente) {
        try {
            CarritoResponse carrito = carritoIntegrationService.obtenerCarrito(idCliente);
            validarCarrito(carrito);
            validarStock(carrito.getItems());

            PedidoEntity pedido = crearPedidoPendiente(idCliente, carrito);
            crearDetallesPedido(pedido, carrito.getItems());
            carritoIntegrationService.limpiarCarrito(idCliente);
            return toCheckoutResponse(pedido, "Pedido creado. Pendiente de pago.");
        } catch (MicroserviceUnavailableException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex);
        }
    }

    @Override
    public void eliminar(Long idPedido) {
        if (!pedidoRepository.existsById(idPedido)) {
            throw new IllegalArgumentException("Pedido no encontrado: " + idPedido);
        }
        pedidoRepository.deleteById(idPedido);
    }

    private void validarCarrito(CarritoResponse carrito) {
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito esta vacio");
        }
    }

    private void validarStock(List<ItemCarritoResponse> items) {
        for (ItemCarritoResponse item : items) {
            if (item.getPrendaId() == null || item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item de carrito invalido");
            }
            StockResponse stock = inventarioIntegrationService.obtenerStock(item.getPrendaId());
            int stockActual = stock != null && stock.getStock() != null ? stock.getStock() : 0;
            if (stockActual < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para la prenda " + item.getPrendaId());
            }
        }
    }

    private void validarStockDetalles(List<DetallePedidoEntity> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido no tiene detalles");
        }
        for (DetallePedidoEntity detalle : detalles) {
            StockResponse stock = inventarioIntegrationService.obtenerStock(detalle.getIdPrenda());
            int stockActual = stock != null && stock.getStock() != null ? stock.getStock() : 0;
            if (stockActual < detalle.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para la prenda " + detalle.getIdPrenda());
            }
        }
    }

    private PedidoEntity crearPedidoPendiente(Long idCliente, CarritoResponse carrito) {
        ClienteEntity cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente no encontrado: " + idCliente));

        PedidoEntity pedido = new PedidoEntity();
        pedido.setCliente(cliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstadoPedido(ESTADO_PENDIENTE);
        pedido.setTotalPagar(resolverTotalCarrito(carrito));
        return pedidoRepository.save(pedido);
    }

    private void crearDetallesPedido(PedidoEntity pedido, List<ItemCarritoResponse> items) {
        for (ItemCarritoResponse item : items) {
            DetallePedidoEntity detalle = new DetallePedidoEntity();
            detalle.setPedido(pedido);
            detalle.setIdPrenda(item.getPrendaId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio() != null ? item.getPrecio() : BigDecimal.ZERO);
            detallePedidoRepository.save(detalle);
        }
    }

    private BigDecimal resolverTotalCarrito(CarritoResponse carrito) {
        if (carrito.getTotalCarrito() != null) {
            return carrito.getTotalCarrito();
        }
        return carrito.getItems().stream()
                .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CheckoutResponse toCheckoutResponse(PedidoEntity pedido, String mensaje) {
        return CheckoutResponse.builder()
                .idPedido(pedido.getIdPedido())
                .idCliente(pedido.getCliente() != null ? pedido.getCliente().getIdCliente() : null)
                .estadoPedido(pedido.getEstadoPedido())
                .totalPagar(pedido.getTotalPagar())
                .mensaje(mensaje)
                .build();
    }

    private boolean esPedidoPendiente(String estado) {
        return estado != null
                && (ESTADO_PENDIENTE.equalsIgnoreCase(estado)
                || "Pendiente de Pago".equalsIgnoreCase(estado));
    }
}

package com.raymisupreme.mscarrito.service.impl;

import com.raymisupreme.mscarrito.client.CatalogoClient;
import com.raymisupreme.mscarrito.client.VentasClient;
import com.raymisupreme.mscarrito.client.dto.ClienteResponse;
import com.raymisupreme.mscarrito.client.dto.ProductoCatalogoResponse;
import com.raymisupreme.mscarrito.dto.CarritoResponse;
import com.raymisupreme.mscarrito.dto.ItemCarritoRequest;
import com.raymisupreme.mscarrito.dto.ItemCarritoResponse;
import com.raymisupreme.mscarrito.entity.CarritoEntity;
import com.raymisupreme.mscarrito.entity.ItemCarritoEntity;
import com.raymisupreme.mscarrito.mapper.CarritoMapper;
import com.raymisupreme.mscarrito.mapper.ItemCarritoMapper;
import com.raymisupreme.mscarrito.repository.CarritoRepository;
import com.raymisupreme.mscarrito.repository.ItemCarritoRepository;
import com.raymisupreme.mscarrito.service.CarritoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_CONVERTIDO = "CONVERTIDO";

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CarritoMapper carritoMapper;
    private final ItemCarritoMapper itemCarritoMapper;
    private final CatalogoClient catalogoClient;
    private final VentasClient ventasClient;

    @Autowired
    @Lazy
    private CarritoServiceImpl self;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              ItemCarritoRepository itemCarritoRepository,
                              CarritoMapper carritoMapper,
                              ItemCarritoMapper itemCarritoMapper,
                              CatalogoClient catalogoClient,
                              VentasClient ventasClient) {
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.carritoMapper = carritoMapper;
        this.itemCarritoMapper = itemCarritoMapper;
        this.catalogoClient = catalogoClient;
        this.ventasClient = ventasClient;
    }

    @Override
    @Transactional
    public CarritoResponse obtenerCarrito(Long clienteId) {
        CarritoEntity carrito = carritoRepository.findByClienteIdAndEstado(clienteId, ESTADO_ACTIVO)
                .orElseGet(() -> carritoRepository.save(crearCarrito(clienteId, LocalDateTime.now())));
        return construirRespuesta(carrito);
    }

    @Override
    @Transactional
    public CarritoResponse agregarItem(Long clienteId, ItemCarritoRequest request) {
        ProductoCatalogoResponse producto = self.validarProductoDisponible(request.getPrendaId(), request.getCantidad());
        String nombrePrenda = producto.getNombre() != null ? producto.getNombre() : request.getNombrePrenda();
        BigDecimal precio = producto.getPrecioFinal() != null ? producto.getPrecioFinal() : request.getPrecio();

        LocalDateTime ahora = LocalDateTime.now();
        CarritoEntity carrito = carritoRepository.findByClienteIdAndEstado(clienteId, ESTADO_ACTIVO)
                .orElseGet(() -> crearCarrito(clienteId, ahora));

        ItemCarritoEntity item = itemCarritoRepository
                .findByCarrito_IdAndPrendaId(carrito.getId(), request.getPrendaId())
                .orElse(null);

        if (item != null) {
            int nuevaCantidad = item.getCantidad() + request.getCantidad();
            validarStockDisponible(producto, nuevaCantidad);
            item.setCantidad(nuevaCantidad);
            item.setSubtotal(calcularSubtotal(item.getPrecio(), nuevaCantidad));
        } else {
            item = ItemCarritoEntity.builder()
                    .carrito(carrito)
                    .prendaId(request.getPrendaId())
                    .nombrePrenda(nombrePrenda)
                    .precio(precio)
                    .cantidad(request.getCantidad())
                    .subtotal(calcularSubtotal(precio, request.getCantidad()))
                    .agregadoEn(ahora)
                    .build();
            carrito.getItems().add(item);
        }

        carrito.setActualizadoEn(ahora);
        CarritoEntity guardado = carritoRepository.save(carrito);
        return construirRespuesta(guardado);
    }

    public ProductoCatalogoResponse validarProductoDisponible(Long prendaId, Integer cantidad) {
        ProductoCatalogoResponse producto = self.consultarProductoCatalogo(prendaId);
        if (producto == null || producto.getPrenda() == null) {
            if (producto != null && producto.getMensajeInventario() != null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        producto.getMensajeInventario());
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Producto no disponible para venta");
        }
        validarStockDisponible(producto, cantidad);
        return producto;
    }

    @CircuitBreaker(name = "catalogoService", fallbackMethod = "consultarProductoCatalogoFallback")
    public ProductoCatalogoResponse consultarProductoCatalogo(Long prendaId) {
        return catalogoClient.obtenerProducto(prendaId);
    }

    private void validarStockDisponible(ProductoCatalogoResponse producto, Integer cantidad) {
        Integer stockPrenda = producto.getPrenda() != null ? producto.getPrenda().getStock() : null;
        int stock = stockPrenda != null ? stockPrenda : 0;
        if (stock < cantidad) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para agregar al carrito");
        }
    }

    public ProductoCatalogoResponse consultarProductoCatalogoFallback(Long prendaId, Throwable ex) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "ms-catalogo no disponible: ms-carrito no puede validar el producto.",
                ex);
    }

    @Override
    @Transactional
    public CarritoResponse actualizarCantidad(Long clienteId, Long itemId, Integer cantidad) {
        CarritoEntity carrito = obtenerCarritoActivo(clienteId);

        ItemCarritoEntity item = itemCarritoRepository.findByIdAndCarrito_Id(itemId, carrito.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item no encontrado en el carrito del cliente"));

        if (cantidad == 0) {
            carrito.getItems().remove(item);
            itemCarritoRepository.delete(item);
        } else {
            item.setCantidad(cantidad);
            item.setSubtotal(calcularSubtotal(item.getPrecio(), cantidad));
        }

        carrito.setActualizadoEn(LocalDateTime.now());
        CarritoEntity guardado = carritoRepository.save(carrito);
        return construirRespuesta(guardado);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long clienteId) {
        CarritoEntity carrito = obtenerCarritoActivo(clienteId);
        itemCarritoRepository.deleteByCarrito_Id(carrito.getId());
        carritoRepository.delete(carrito);
    }

    @Override
    @Transactional
    public CarritoResponse convertirCarrito(Long clienteId) {
        CarritoEntity carrito = obtenerCarritoActivo(clienteId);
        carrito.setEstado(ESTADO_CONVERTIDO);
        carrito.setActualizadoEn(LocalDateTime.now());
        CarritoEntity guardado = carritoRepository.save(carrito);
        return construirRespuesta(guardado);
    }

    private CarritoEntity crearCarrito(Long clienteId, LocalDateTime ahora) {
        return CarritoEntity.builder()
                .clienteId(clienteId)
                .estado(ESTADO_ACTIVO)
                .creadoEn(ahora)
                .actualizadoEn(ahora)
                .build();
    }

    private CarritoEntity obtenerCarritoActivo(Long clienteId) {
        return carritoRepository.findByClienteIdAndEstado(clienteId, ESTADO_ACTIVO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe carrito activo para el cliente: " + clienteId));
    }

    private BigDecimal calcularSubtotal(BigDecimal precio, int cantidad) {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    private CarritoResponse construirRespuesta(CarritoEntity carrito) {
        List<ItemCarritoEntity> items = itemCarritoRepository.findByCarrito_Id(carrito.getId());
        CarritoResponse response = carritoMapper.toResponse(carrito);
        response.setCliente(obtenerCliente(carrito.getClienteId()));
        List<ItemCarritoResponse> itemResponses = items.stream()
                .map(itemCarritoMapper::toResponse)
                .toList();
        response.setItems(itemResponses);
        response.setTotalCarrito(items.stream()
                .map(ItemCarritoEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return response;
    }

    private ClienteResponse obtenerCliente(Long clienteId) {
        try {
            return ventasClient.obtenerCliente(clienteId);
        } catch (Exception ex) {
            return null;
        }
    }
}

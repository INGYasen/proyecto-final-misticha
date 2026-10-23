package com.raymisupreme.mscatalogo.service.impl;

import com.raymisupreme.mscatalogo.client.InventarioClient;
import com.raymisupreme.mscatalogo.dto.CategoriaPublicDTO;
import com.raymisupreme.mscatalogo.dto.HomePublicDTO;
import com.raymisupreme.mscatalogo.dto.ProductoCatalogoResponse;
import com.raymisupreme.mscatalogo.dto.ProductoPublicDTO;
import com.raymisupreme.mscatalogo.dto.PromocionPublicDTO;
import com.raymisupreme.mscatalogo.dto.PrendaInventarioResponse;
import com.raymisupreme.mscatalogo.dto.CategoriaInventarioResponse;
import com.raymisupreme.mscatalogo.entity.DescuentoEntity;
import com.raymisupreme.mscatalogo.entity.ItemCatalogoEntity;
import com.raymisupreme.mscatalogo.entity.PrecioEntity;
import com.raymisupreme.mscatalogo.repository.DescuentoRepository;
import com.raymisupreme.mscatalogo.repository.ItemCatalogoRepository;
import com.raymisupreme.mscatalogo.repository.PrecioRepository;
import com.raymisupreme.mscatalogo.service.CatalogoPublicService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.text.Normalizer;
import java.util.Locale;

@Service
public class CatalogoPublicServiceImpl implements CatalogoPublicService {

    private static final Logger log = LoggerFactory.getLogger(CatalogoPublicServiceImpl.class);

    private final ItemCatalogoRepository itemCatalogoRepository;
    private final PrecioRepository precioRepository;
    private final DescuentoRepository descuentoRepository;
    private final InventarioClient inventarioClient;
    private final CatalogoPublicServiceImpl self;
    private volatile List<PrendaInventarioResponse> ultimoInventario = List.of();

    public CatalogoPublicServiceImpl(ItemCatalogoRepository itemCatalogoRepository,
                                     PrecioRepository precioRepository,
                                     DescuentoRepository descuentoRepository,
                                     InventarioClient inventarioClient,
                                     @Lazy CatalogoPublicServiceImpl self) {
        this.itemCatalogoRepository = itemCatalogoRepository;
        this.precioRepository = precioRepository;
        this.descuentoRepository = descuentoRepository;
        this.inventarioClient = inventarioClient;
        this.self = self;
    }

    @Override
    public HomePublicDTO obtenerHome() {
        List<ProductoPublicDTO> productos = listarProductos(null, null);
        return HomePublicDTO.builder()
                .bannerPrincipal("/assets/catalogo/banner-principal.jpg")
                .titulo("RaymiSupreme")
                .subtitulo("Moda artesanal con presencia contemporanea")
                .textoCampania("Coleccion seleccionada de prendas andinas para una tienda elegante y atemporal.")
                .categoriasDestacadas(listarCategorias().stream()
                        .filter(CategoriaPublicDTO::getDestacada)
                        .toList())
                .productosDestacados(productos.stream()
                        .filter(ProductoPublicDTO::getDestacado)
                        .limit(8)
                        .toList())
                .promocionesActivas(listarPromociones())
                .build();
    }

    @Override
    public List<ProductoPublicDTO> listarProductos(Long idCategoria, String publico) {
        Map<Long, PrendaInventarioResponse> prendasPorId = self.obtenerPrendasInventario().stream()
                .collect(java.util.stream.Collectors.toMap(
                        PrendaInventarioResponse::getIdPrenda,
                        prenda -> prenda,
                        (primera, duplicada) -> primera));
        return itemCatalogoRepository.findByActivoTrue().stream()
                .map(item -> toProductoPublico(item, prendasPorId.get(item.getIdPrenda())))
                .filter(Objects::nonNull)
                .filter(producto -> idCategoria == null || (producto.getCategoria() != null
                        && idCategoria.equals(producto.getCategoria().getId())))
                .filter(producto -> filtrarPorTexto(producto.getPublico(), publico))
                .sorted(Comparator.comparing(ProductoPublicDTO::getDestacado, Comparator.reverseOrder())
                        .thenComparing(ProductoPublicDTO::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    @Override
    public List<ProductoCatalogoResponse> listarProductosCatalogo(Long idCategoria, String publico, String material, String color, String talla) {
        return itemCatalogoRepository.findByActivoTrue().stream()
                .map(this::toProductoCatalogo)
                .filter(producto -> filtrarPorCategoria(producto, idCategoria))
                .filter(producto -> filtrarPorTexto(
                        producto.getPrenda() != null ? producto.getPrenda().getPublico() : null, publico))
                .filter(producto -> filtrarPorTexto(
                        producto.getPrenda() != null ? producto.getPrenda().getMaterial() : null, material))
                .filter(producto -> filtrarPorTexto(
                        producto.getPrenda() != null ? producto.getPrenda().getColor() : null, color))
                .filter(producto -> filtrarPorTexto(
                        producto.getPrenda() != null ? producto.getPrenda().getTalla() : null, talla))
                .sorted(Comparator.comparing(ProductoCatalogoResponse::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    @Override
    public ProductoCatalogoResponse obtenerProductoCatalogo(Long idPrenda) {
        ItemCatalogoEntity item = itemCatalogoRepository.findByIdPrendaAndActivoTrue(idPrenda)
                .orElseThrow(() -> new RuntimeException("Producto no publicado con ID: " + idPrenda));
        ProductoCatalogoResponse producto = toProductoCatalogo(item);
        if (producto == null) {
            throw new RuntimeException("Producto no disponible para venta con ID: " + idPrenda);
        }
        return producto;
    }

    @Override
    public ProductoPublicDTO obtenerProducto(Long id) {
        ItemCatalogoEntity item = itemCatalogoRepository.findByIdPrendaAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Producto no publicado con ID: " + id));
        ProductoPublicDTO producto = toProductoPublico(item);
        if (producto == null) {
            throw new RuntimeException("Producto no disponible para catalogo publico con ID: " + id);
        }
        return producto;
    }

    @Override
    public List<CategoriaPublicDTO> listarCategorias() {
        return self.obtenerPrendasInventario().stream()
                .filter(prenda -> prenda.getIdCategoria() != null)
                .collect(java.util.stream.Collectors.toMap(
                        PrendaInventarioResponse::getIdCategoria,
                        prenda -> prenda,
                        (primera, duplicada) -> primera))
                .values().stream()
                .map(prenda -> CategoriaPublicDTO.builder()
                        .id(prenda.getIdCategoria())
                        .nombre(prenda.getNombreCategoria() == null ? "Coleccion" : prenda.getNombreCategoria())
                        .slug(toSlug(prenda.getNombreCategoria()))
                        .destacada(true)
                        .build())
                .toList();
    }

    @Override
    public List<PromocionPublicDTO> listarPromociones() {
        LocalDate hoy = LocalDate.now();
        return descuentoRepository.findByActivoTrue().stream()
                .filter(descuento -> !hoy.isBefore(descuento.getFechaInicio()) && !hoy.isAfter(descuento.getFechaFin()))
                .map(this::toPromocionPublica)
                .toList();
    }

    private PromocionPublicDTO toPromocionPublica(DescuentoEntity descuento) {
        List<ProductoPublicDTO> productosAsociados = precioRepository.findByDescuento_IdAndActivoTrue(descuento.getId())
                .stream()
                .map(PrecioEntity::getPrendaId)
                .map(this::obtenerProductoSilencioso)
                .filter(Objects::nonNull)
                .toList();

        return PromocionPublicDTO.builder()
                .id(descuento.getId())
                .nombre(resolverNombrePromocion(descuento))
                .porcentajeDescuento(descuento.getPorcentaje())
                .fechaInicio(descuento.getFechaInicio())
                .fechaFin(descuento.getFechaFin())
                .productosAsociados(productosAsociados)
                .build();
    }

    private ProductoPublicDTO obtenerProductoSilencioso(Long idPrenda) {
        try {
            return obtenerProducto(idPrenda);
        } catch (Exception ex) {
            log.debug("Producto asociado a promocion no disponible: {}", idPrenda);
            return null;
        }
    }

    private ProductoPublicDTO toProductoPublico(ItemCatalogoEntity item) {
        try {
            PrendaInventarioResponse prenda = self.obtenerPrenda(item.getIdPrenda());

            return toProductoPublico(item, prenda);
        } catch (Exception ex) {
            log.warn("No se pudo armar producto publico {}: {}", item.getIdPrenda(), ex.getMessage());
            return null;
        }
    }

    private ProductoPublicDTO toProductoPublico(ItemCatalogoEntity item, PrendaInventarioResponse prenda) {
        try {
        if (prenda == null) {
            PrecioEntity precio = precioRepository.findByPrendaIdAndActivoTrue(item.getIdPrenda())
                    .orElse(null);
            return ProductoPublicDTO.builder()
                    .id(item.getIdPrenda())
                    .nombre(resolverNombreProducto(null, precio))
                    .descripcion(item.getDescripcionPublica())
                    .precio(precio != null ? precio.getPrecioFinal() : null)
                    .precioBase(precio != null ? precio.getPrecioBase() : null)
                    .imagen(fotoDeProducto(null, item.getUrlFoto()))
                    .disponible(false)
                    .destacado(Boolean.TRUE.equals(item.getDestacado()))
                    .build();
        }

        PrecioEntity precio = precioRepository.findByPrendaIdAndActivoTrue(item.getIdPrenda())
                    .orElse(null);

            return ProductoPublicDTO.builder()
                    .id(item.getIdPrenda())
                    .nombre(resolverNombreProducto(prenda, precio))
                    .descripcion(item.getDescripcionPublica())
                    .precio(precio != null ? precio.getPrecioFinal() : null)
                    .precioBase(precio != null ? precio.getPrecioBase() : null)
                    .imagen(fotoDeProducto(prenda, item.getUrlFoto()))
                    .categoria(resolverCategoria(
                            prenda != null ? prenda.getIdCategoria() : null,
                            prenda != null ? prenda.getNombreCategoria() : null))
                    .publico(prenda != null ? prenda.getPublico() : null)
                    .talla(prenda != null ? prenda.getTalla() : null)
                    .color(prenda != null ? prenda.getColor() : null)
                    .material(prenda != null ? prenda.getMaterial() : null)
                    .stockDisponible(prenda != null && prenda.getStock() != null ? prenda.getStock() : 0)
                    .disponible(prenda != null && prenda.getStock() != null && prenda.getStock() > 0)
                    .destacado(Boolean.TRUE.equals(item.getDestacado()))
                    .build();
        } catch (Exception ex) {
            log.warn("No se pudo armar producto publico {}: {}", item.getIdPrenda(), ex.getMessage());
            return null;
        }
    }

    private ProductoCatalogoResponse toProductoCatalogo(ItemCatalogoEntity item) {
        PrendaInventarioResponse prenda = self.obtenerPrenda(item.getIdPrenda());
        PrecioEntity precio = precioRepository.findByPrendaIdAndActivoTrue(item.getIdPrenda())
                .orElse(null);

        BigDecimal precioVenta = precio != null ? precio.getPrecioBase() : null;
        BigDecimal precioFinal = precio != null ? precio.getPrecioFinal() : null;
        BigDecimal descuento = precio != null && precio.getDescuento() != null
                ? precio.getDescuento().getPorcentaje()
                : null;

        boolean inventarioDisponible = prenda != null;
        return ProductoCatalogoResponse.builder()
                .idPrenda(item.getIdPrenda())
                .nombre(resolverNombreProducto(prenda, precio))
                .prenda(prenda)
                .descripcion(item.getDescripcionPublica())
                .imagenUrl(fotoDeProducto(prenda, item.getUrlFoto()))
                .precioVenta(precioVenta)
                .descuento(descuento)
                .precioFinal(precioFinal)
                .mensajeInventario(inventarioDisponible ? null : "ms-inventario no disponible")
                .build();
    }

    private CategoriaPublicDTO resolverCategoria(Long idCategoria, String nombreCategoria) {
        if (nombreCategoria != null && !nombreCategoria.isBlank()) {
            return CategoriaPublicDTO.builder()
                    .id(idCategoria)
                    .nombre(nombreCategoria)
                    .slug(toSlug(nombreCategoria))
                    .destacada(true)
                    .build();
        }
        Map<Long, CategoriaPublicDTO> categoriasPorId = listarCategorias().stream()
                .collect(java.util.stream.Collectors.toMap(CategoriaPublicDTO::getId, categoria -> categoria));
        return categoriasPorId.getOrDefault(idCategoria,
                CategoriaPublicDTO.builder()
                        .id(idCategoria)
                        .nombre("Coleccion")
                        .slug("coleccion")
                        .destacada(false)
                        .build());
    }

    private String fotoDeProducto(PrendaInventarioResponse prenda, String fotoCatalogo) {
        String inventario = prenda == null ? null : prenda.getUrlFoto();
        if (esFotoDePrenda(inventario)) return inventario;
        if (esFotoDePrenda(fotoCatalogo)) return fotoCatalogo;
        return null;
    }

    private boolean esFotoDePrenda(String url) {
        if (url == null || url.isBlank()) return false;
        return !url.toLowerCase(java.util.Locale.ROOT).contains("carrusel");
    }

    private String resolverNombreProducto(PrendaInventarioResponse prenda, PrecioEntity precio) {
        if (prenda != null && prenda.getNombrePrenda() != null && !prenda.getNombrePrenda().isBlank()) {
            return prenda.getNombrePrenda();
        }
        if (precio != null && precio.getNombre() != null && !precio.getNombre().isBlank()) {
            return precio.getNombre();
        }
        return "Producto Raymi Suprême";
    }

    private String resolverNombrePromocion(DescuentoEntity descuento) {
        if (descuento.getDescripcion() != null && !descuento.getDescripcion().isBlank()) {
            return descuento.getDescripcion();
        }
        return descuento.getCodigo();
    }

    @CircuitBreaker(name = "ms-inventario", fallbackMethod = "obtenerPrendaFallback")
    public PrendaInventarioResponse obtenerPrenda(Long id) {
        return inventarioClient.obtenerPrenda(id);
    }

    public PrendaInventarioResponse obtenerPrendaFallback(Long id, Throwable ex) {
        log.warn("ms-inventario no disponible para producto publico {}: {}", id, ex.getMessage());
        return ultimoInventario.stream()
                .filter(prenda -> id.equals(prenda.getIdPrenda()))
                .findFirst()
                .orElse(null);
    }

    @CircuitBreaker(name = "ms-inventario", fallbackMethod = "obtenerPrendasInventarioFallback")
    public List<PrendaInventarioResponse> obtenerPrendasInventario() {
        List<PrendaInventarioResponse> prendas = inventarioClient.listarPrendasPublicas();
        if (prendas != null && !prendas.isEmpty()) {
            ultimoInventario = List.copyOf(prendas);
        }
        return prendas == null ? List.of() : prendas;
    }

    public List<PrendaInventarioResponse> obtenerPrendasInventarioFallback(Throwable ex) {
        log.warn("ms-inventario no disponible al construir catalogo publico: {}", ex.getMessage());
        return ultimoInventario;
    }

    @CircuitBreaker(name = "ms-inventario", fallbackMethod = "obtenerCategoriasInventarioFallback")
    public List<CategoriaInventarioResponse> obtenerCategoriasInventario() {
        return inventarioClient.listarCategorias();
    }

    public List<CategoriaInventarioResponse> obtenerCategoriasInventarioFallback(Throwable ex) {
        log.warn("ms-inventario no disponible al consultar categorias: {}", ex.getMessage());
        return List.of();
    }

    private String toSlug(String value) {
        if (value == null) return "categoria";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private boolean filtrarPorCategoria(ProductoCatalogoResponse producto, Long idCategoria) {
        if (idCategoria == null) {
            return true;
        }
        return producto.getPrenda() != null
                && idCategoria.equals(producto.getPrenda().getIdCategoria());
    }

    private boolean filtrarPorTexto(String valorProducto, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }
        return valorProducto != null && valorProducto.equalsIgnoreCase(filtro.trim());
    }
}

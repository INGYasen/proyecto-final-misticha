package com.raymisupreme.mscatalogo.controller;

import com.raymisupreme.mscatalogo.dto.ProductoPublicDTO;
import com.raymisupreme.mscatalogo.dto.PromocionPublicDTO;
import com.raymisupreme.mscatalogo.service.CatalogoPublicService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promociones/public")
public class PromocionTiendaController {

    private final CatalogoPublicService catalogoPublicService;

    public PromocionTiendaController(CatalogoPublicService catalogoPublicService) {
        this.catalogoPublicService = catalogoPublicService;
    }

    @GetMapping("/popup")
    public List<Map<String, Object>> popup(@RequestParam(defaultValue = "3") int limite) {
        return vigentes().stream()
                .map(this::aPopup)
                .filter(this::ofertaVisible)
                .limit(Math.max(limite, 0))
                .toList();
    }

    @GetMapping("/vigentes")
    public List<Map<String, Object>> vigentesPublicas() {
        return vigentes().stream().map(this::aPopup).filter(this::ofertaVisible).toList();
    }

    @PostMapping("/aplicar-descuento")
    public Map<String, Object> aplicar(@RequestBody Map<String, Object> solicitud) {
        long idPrenda = Long.parseLong(String.valueOf(solicitud.get("idPrenda")));
        BigDecimal precioOriginal = new BigDecimal(String.valueOf(solicitud.getOrDefault("precioOriginal", "0")));
        PromocionPublicDTO promo = vigentes().stream()
                .filter(item -> item.getProductosAsociados() != null
                        && item.getProductosAsociados().stream().anyMatch(producto -> idPrenda == producto.getId()))
                .findFirst()
                .orElse(null);
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idPrenda", idPrenda);
        respuesta.put("precioOriginal", precioOriginal);
        if (promo == null || promo.getPorcentajeDescuento() == null) {
            respuesta.put("porcentajeDescuento", 0);
            respuesta.put("precioFinal", precioOriginal);
            respuesta.put("tieneDescuento", false);
            return respuesta;
        }
        BigDecimal descuento = precioOriginal.multiply(promo.getPorcentajeDescuento())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        respuesta.put("idPromocion", promo.getId());
        respuesta.put("porcentajeDescuento", promo.getPorcentajeDescuento());
        respuesta.put("precioFinal", precioOriginal.subtract(descuento));
        respuesta.put("nombrePromocion", promo.getNombre());
        respuesta.put("tieneDescuento", true);
        return respuesta;
    }

    private List<PromocionPublicDTO> vigentes() {
        return catalogoPublicService.listarPromociones();
    }

    private Map<String, Object> aPopup(PromocionPublicDTO promo) {
        ProductoPublicDTO producto = promo.getProductosAsociados() == null || promo.getProductosAsociados().isEmpty()
                ? null
                : promo.getProductosAsociados().get(0);
        BigDecimal base = producto == null ? BigDecimal.ZERO
                : producto.getPrecioBase() != null ? producto.getPrecioBase()
                : producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO;
        BigDecimal porcentaje = promo.getPorcentajeDescuento() == null ? BigDecimal.ZERO : promo.getPorcentajeDescuento();
        BigDecimal finalPrice = producto != null && producto.getPrecio() != null ? producto.getPrecio() : base;
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("idPromocion", promo.getId());
        fila.put("idPrenda", producto == null ? null : producto.getId());
        fila.put("titulo", promo.getNombre());
        fila.put("descripcion", producto == null ? promo.getNombre() : producto.getDescripcion());
        fila.put("porcentajeDescuento", porcentaje);
        fila.put("precioBase", base);
        fila.put("precioFinal", finalPrice);
        fila.put("urlImagen", producto == null ? null : producto.getImagen());
        fila.put("nombrePrenda", producto == null ? promo.getNombre() : producto.getNombre());
        fila.put("fechaInicio", promo.getFechaInicio());
        fila.put("fechaFin", promo.getFechaFin());
        fila.put("activa", true);
        fila.put("vigente", true);
        fila.put("tipo", "TEMPORADA");
        return fila;
    }

    private boolean ofertaVisible(Map<String, Object> fila) {
        Object id = fila.get("idPrenda");
        Object base = fila.get("precioBase");
        return id instanceof Number && base instanceof BigDecimal && ((BigDecimal) base).compareTo(BigDecimal.ZERO) > 0;
    }
}

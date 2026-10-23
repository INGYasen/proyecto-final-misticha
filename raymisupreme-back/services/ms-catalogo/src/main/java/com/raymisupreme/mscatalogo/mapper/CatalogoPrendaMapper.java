package com.raymisupreme.mscatalogo.mapper;

import com.raymisupreme.mscatalogo.dto.CatalogoPrendaResponse;
import com.raymisupreme.mscatalogo.dto.PrendaInventarioResponse;
import com.raymisupreme.mscatalogo.entity.ItemCatalogoEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CatalogoPrendaMapper {

    public CatalogoPrendaResponse toPublicResponse(ItemCatalogoEntity item,
                                                   PrendaInventarioResponse prenda,
                                                   BigDecimal precioVigente) {
        CatalogoPrendaResponse response = new CatalogoPrendaResponse();
        response.setIdPrenda(item.getIdPrenda());
        response.setIdCategoria(prenda != null ? prenda.getIdCategoria() : null);
        response.setNombreCategoria(prenda != null ? prenda.getNombreCategoria() : null);
        response.setPublico(prenda != null ? prenda.getPublico() : null);
        response.setCodigoUnico(prenda != null ? prenda.getCodigoUnico() : null);
        response.setNombrePrenda(prenda != null ? prenda.getNombrePrenda() : null);
        response.setTalla(prenda != null ? prenda.getTalla() : null);
        response.setColor(prenda != null ? prenda.getColor() : null);
        response.setMaterial(prenda != null ? prenda.getMaterial() : null);
        response.setUrlFoto(item.getUrlFoto());
        response.setDescripcionPublica(item.getDescripcionPublica());
        response.setDestacado(item.getDestacado());
        response.setPrecioVigente(precioVigente);
        response.setStockDisponible(prenda != null && prenda.getStock() != null ? prenda.getStock() : 0);
        response.setDisponibleParaVenta(prenda != null && prenda.getStock() != null && prenda.getStock() > 0);
        return response;
    }
}

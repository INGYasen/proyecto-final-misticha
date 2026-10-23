package com.raymisupreme.mscatalogo.service;

import com.raymisupreme.mscatalogo.dto.CategoriaPublicDTO;
import com.raymisupreme.mscatalogo.dto.HomePublicDTO;
import com.raymisupreme.mscatalogo.dto.ProductoCatalogoResponse;
import com.raymisupreme.mscatalogo.dto.ProductoPublicDTO;
import com.raymisupreme.mscatalogo.dto.PromocionPublicDTO;

import java.util.List;

public interface CatalogoPublicService {

    HomePublicDTO obtenerHome();

    List<ProductoPublicDTO> listarProductos(Long idCategoria, String publico);

    List<ProductoCatalogoResponse> listarProductosCatalogo(Long idCategoria, String publico, String material, String color, String talla);

    ProductoCatalogoResponse obtenerProductoCatalogo(Long idPrenda);

    ProductoPublicDTO obtenerProducto(Long id);

    List<CategoriaPublicDTO> listarCategorias();

    List<PromocionPublicDTO> listarPromociones();
}

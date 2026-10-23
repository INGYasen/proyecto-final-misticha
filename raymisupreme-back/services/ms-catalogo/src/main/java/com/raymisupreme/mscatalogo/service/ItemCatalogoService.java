package com.raymisupreme.mscatalogo.service;

import com.raymisupreme.mscatalogo.dto.ItemCatalogoRequest;
import com.raymisupreme.mscatalogo.dto.ItemCatalogoResponse;

import java.util.List;

public interface ItemCatalogoService {

    ItemCatalogoResponse guardar(ItemCatalogoRequest request);

    List<ItemCatalogoResponse> listarTodos();

    ItemCatalogoResponse buscarPorId(Long id);

    ItemCatalogoResponse actualizar(Long id, ItemCatalogoRequest request);

    void eliminar(Long id);
}

package com.raymisupreme.mscatalogo.mapper;

import com.raymisupreme.mscatalogo.dto.ItemCatalogoRequest;
import com.raymisupreme.mscatalogo.dto.ItemCatalogoResponse;
import com.raymisupreme.mscatalogo.entity.ItemCatalogoEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemCatalogoMapper {

    public ItemCatalogoEntity toEntity(ItemCatalogoRequest request) {
        ItemCatalogoEntity entity = new ItemCatalogoEntity();
        entity.setIdPrenda(request.getIdPrenda());
        entity.setUrlFoto(request.getUrlFoto());
        entity.setDescripcionPublica(request.getDescripcionPublica());
        entity.setDestacado(request.getDestacado() != null ? request.getDestacado() : false);
        entity.setActivo(request.getActivo() != null ? request.getActivo() : true);
        return entity;
    }

    public ItemCatalogoResponse toResponse(ItemCatalogoEntity entity) {
        ItemCatalogoResponse response = new ItemCatalogoResponse();
        response.setIdItem(entity.getIdItem());
        response.setIdPrenda(entity.getIdPrenda());
        response.setUrlFoto(entity.getUrlFoto());
        response.setDescripcionPublica(entity.getDescripcionPublica());
        response.setDestacado(entity.getDestacado());
        response.setActivo(entity.getActivo());
        return response;
    }
}

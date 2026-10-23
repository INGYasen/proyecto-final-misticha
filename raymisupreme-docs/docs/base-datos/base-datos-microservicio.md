# Base de Datos por Microservicio

## Acopio

```mermaid
erDiagram
    ARTESANO ||--o{ LOTE_COMPRA : entrega

    ARTESANO {
        BIGINT id_artesano PK
        VARCHAR nombres
        VARCHAR apellidos
        VARCHAR dni
        VARCHAR comunidad_o_zona
        VARCHAR telefono
    }

    LOTE_COMPRA {
        BIGINT id_lote PK
        BIGINT id_artesano FK
        DATE fecha_compra
        DECIMAL precio_total_pagado
        INT cantidad_total_recibida
        VARCHAR descripcion_general
        VARCHAR estado_lote
    }
```

## Inventario

```mermaid
erDiagram
    CATEGORIA ||--o{ PRENDA : clasifica
    PRENDA ||--o{ INSPECCION_CALIDAD : recibe

    CATEGORIA {
        BIGINT id_categoria PK
        VARCHAR nombre
        VARCHAR descripcion
    }

    PRENDA {
        BIGINT id_prenda PK
        BIGINT id_lote
        BIGINT id_categoria FK
        VARCHAR codigo_unico
        VARCHAR nombre_prenda
        VARCHAR talla
        VARCHAR color
        VARCHAR material
        INT stock
    }

    INSPECCION_CALIDAD {
        BIGINT id_inspeccion PK
        BIGINT id_prenda FK
        DATE fecha_inspeccion
        VARCHAR resultado
        VARCHAR observaciones
    }
```

`id_lote` referencia lógicamente a `ms-acopio`, por lo que no tiene una clave foránea local.

## Catálogo

```mermaid
erDiagram
    DESCUENTO ||--o{ PRECIO : aplica

    ITEM_CATALOGO {
        BIGINT id_item PK
        BIGINT id_prenda
        VARCHAR url_foto
        VARCHAR descripcion_publica
        BOOLEAN destacado
        BOOLEAN activo
    }

    DESCUENTO {
        BIGINT id PK
        VARCHAR codigo
        VARCHAR descripcion
        DECIMAL porcentaje
        DATE fecha_inicio
        DATE fecha_fin
        BOOLEAN activo
    }

    PRECIO {
        BIGINT id PK
        BIGINT prenda_id
        VARCHAR nombre
        DECIMAL precio_base
        DECIMAL precio_final
        BOOLEAN activo
        BIGINT descuento_id FK
    }
```

`id_prenda` y `prenda_id` referencian lógicamente a una prenda de `ms-inventario`.

## Carrito

```mermaid
erDiagram
    CARRITO ||--o{ ITEM_CARRITO : contiene

    CARRITO {
        BIGINT id PK
        BIGINT cliente_id
        VARCHAR estado
        DATETIME creado_en
        DATETIME actualizado_en
    }

    ITEM_CARRITO {
        BIGINT id PK
        BIGINT carrito_id FK
        BIGINT prenda_id
        VARCHAR nombre_prenda
        DECIMAL precio
        INT cantidad
        DECIMAL subtotal
        DATETIME agregado_en
    }
```

## Ventas

```mermaid
erDiagram
    CLIENTE ||--o{ PEDIDO : realiza
    PEDIDO ||--|{ DETALLE_PEDIDO : contiene

    CLIENTE {
        BIGINT id_cliente PK
        VARCHAR nombres
        VARCHAR email
        VARCHAR pais
        VARCHAR ciudad
        VARCHAR direccion_envio
    }

    PEDIDO {
        BIGINT id_pedido PK
        BIGINT id_cliente FK
        DATETIME fecha_pedido
        VARCHAR estado_pedido
        DECIMAL total_pagar
    }

    DETALLE_PEDIDO {
        BIGINT id_detalle PK
        BIGINT id_pedido FK
        BIGINT id_prenda
        INT cantidad
        DECIMAL precio_unitario
    }
```

## Pagos

```mermaid
erDiagram
    PAGO ||--o| COMPROBANTE : genera

    PAGO {
        BIGINT id PK
        BIGINT pedido_id
        BIGINT cliente_id
        DECIMAL monto
        VARCHAR metodo_pago
        VARCHAR estado
        VARCHAR referencia_pago
        DATETIME fecha_pago
    }

    COMPROBANTE {
        BIGINT id PK
        BIGINT pago_id FK
        VARCHAR numero_comprobante
        VARCHAR tipo
        DATETIME fecha_emision
        DECIMAL monto_total
        BOOLEAN activo
    }
```
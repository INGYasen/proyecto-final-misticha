# Relaciones y Responsabilidades

## Relaciones completas

Las líneas continuas representan relaciones internas dentro de un microservicio.  
Las líneas punteadas representan referencias entre microservicios.

<div style="display:flex; gap:20px; flex-wrap:wrap; margin:20px 0 30px 0;">

  <div style="display:flex; align-items:center; gap:8px;">
    <div style="width:45px; border-top:3px solid #333;"></div>
    <span><strong>Relación interna</strong></span>
  </div>

  <div style="display:flex; align-items:center; gap:8px;">
    <div style="width:45px; border-top:3px dashed #777;"></div>
    <span><strong>Referencia entre microservicios</strong></span>
  </div>

</div>

---

## Vista general de relaciones

<div style="display:flex; flex-direction:column; align-items:center; gap:18px; margin:30px 0 60px 0;">

  <div style="background:#5e35b1; color:white; padding:16px 28px; border-radius:10px; width:330px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-acopio
    <br>
    <span style="font-weight:normal;">Artesano → Lote</span>
  </div>

  <div style="font-size:28px;">⇣</div>
  <div style="font-size:14px; color:#555;">Referencia de lote hacia inventario</div>

  <div style="background:#1e88e5; color:white; padding:16px 28px; border-radius:10px; width:330px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-inventario
    <br>
    <span style="font-weight:normal;">Categoría → Prenda → Inspección</span>
  </div>

  <div style="font-size:28px;">⇣</div>
  <div style="font-size:14px; color:#555;">Referencia de prenda hacia catálogo, carrito y ventas</div>

  <div style="background:#43a047; color:white; padding:16px 28px; border-radius:10px; width:330px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-catalogo
    <br>
    <span style="font-weight:normal;">Ítem catálogo → Precio → Descuento</span>
  </div>

  <div style="font-size:28px;">⇣</div>
  <div style="font-size:14px; color:#555;">Producto publicado usado por el carrito</div>

  <div style="background:#fb8c00; color:white; padding:16px 28px; border-radius:10px; width:330px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-carrito
    <br>
    <span style="font-weight:normal;">Cliente → Carrito → Ítem carrito</span>
  </div>

  <div style="font-size:28px;">⇣</div>
  <div style="font-size:14px; color:#555;">El carrito se usa para crear el pedido</div>

  <div style="background:#e53935; color:white; padding:16px 28px; border-radius:10px; width:330px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-ventas
    <br>
    <span style="font-weight:normal;">Cliente → Pedido → Detalle pedido</span>
  </div>

  <div style="font-size:28px;">⇣</div>
  <div style="font-size:14px; color:#555;">El pedido confirmado genera el pago</div>

  <div style="background:#8e24aa; color:white; padding:16px 28px; border-radius:10px; width:330px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-pagos
    <br>
    <span style="font-weight:normal;">Pedido → Pago → Comprobante</span>
  </div>

</div>

---

## Detalle de relaciones

<div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap:16px; margin:25px 0 50px 0;">

  <div style="border-left:6px solid #5e35b1; background:#faf7ff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <h3 style="margin-top:0;">ms-acopio</h3>
    <p><strong>Relación interna:</strong></p>
    <p><code>Artesano → Lote</code></p>
    <p>Un artesano puede estar asociado a uno o varios lotes registrados.</p>
  </div>

  <div style="border-left:6px solid #1e88e5; background:#f4faff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <h3 style="margin-top:0;">ms-inventario</h3>
    <p><strong>Relaciones internas:</strong></p>
    <p><code>Categoría → Prenda</code></p>
    <p><code>Prenda → Inspección</code></p>
    <p><strong>Referencia externa:</strong></p>
    <p><code>Prenda → id_lote</code></p>
  </div>

  <div style="border-left:6px solid #43a047; background:#f6fff6; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <h3 style="margin-top:0;">ms-catalogo</h3>
    <p><strong>Relaciones internas:</strong></p>
    <p><code>Ítem catálogo → Precio</code></p>
    <p><code>Descuento → Precio</code></p>
    <p><strong>Referencia externa:</strong></p>
    <p><code>Ítem catálogo → id_prenda</code></p>
  </div>

  <div style="border-left:6px solid #fb8c00; background:#fffaf3; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <h3 style="margin-top:0;">ms-carrito</h3>
    <p><strong>Relaciones internas:</strong></p>
    <p><code>Cliente → Carrito</code></p>
    <p><code>Carrito → Ítem carrito</code></p>
    <p><strong>Referencia externa:</strong></p>
    <p><code>Ítem carrito → id_prenda</code></p>
  </div>

  <div style="border-left:6px solid #e53935; background:#fff7f7; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <h3 style="margin-top:0;">ms-ventas</h3>
    <p><strong>Relaciones internas:</strong></p>
    <p><code>Cliente → Pedido</code></p>
    <p><code>Pedido → Detalle pedido</code></p>
    <p><strong>Referencia externa:</strong></p>
    <p><code>Detalle pedido → id_prenda</code></p>
  </div>

  <div style="border-left:6px solid #8e24aa; background:#fcf6ff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <h3 style="margin-top:0;">ms-pagos</h3>
    <p><strong>Relaciones internas:</strong></p>
    <p><code>Pedido → Pago</code></p>
    <p><code>Pago → Comprobante</code></p>
    <p><strong>Referencia externa:</strong></p>
    <p><code>Pago → pedido_id</code></p>
  </div>

</div>

## Tipos de relación

### Relaciones internas

Se implementan con claves foráneas porque las tablas pertenecen a la misma base de datos:

- Artesano con lote.
- Categoría con prenda.
- Prenda con inspección.
- Descuento con precio.
- Carrito con ítems.
- Cliente con pedido.
- Pedido con detalles.
- Pago con comprobante.

### Relaciones entre microservicios

Se mantienen mediante identificadores:

| Campo | Origen lógico |
|---|---|
| `prenda.id_lote` | Lote registrado en `ms-acopio` |
| `item_catalogo.id_prenda` | Prenda de `ms-inventario` |
| `precio.prenda_id` | Prenda de `ms-inventario` |
| `carrito.cliente_id` | Cliente de `ms-ventas` |
| `item_carrito.prenda_id` | Prenda publicada en catálogo |
| `detalle_pedido.id_prenda` | Prenda de `ms-inventario` |
| `pago.pedido_id` | Pedido de `ms-ventas` |
| `pago.cliente_id` | Cliente de `ms-ventas` |

Esta separación evita que un microservicio modifique directamente los datos de otro.
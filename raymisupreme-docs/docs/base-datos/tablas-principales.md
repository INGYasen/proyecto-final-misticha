# Tablas Principales

## Resumen

| Base de datos | Tabla | Descripción |
|---|---|---|
| Acopio | `artesano` | Datos del proveedor artesanal |
| Acopio | `lote_compra` | Productos recibidos del artesano |
| Inventario | `categoria` | Clasificación de prendas |
| Inventario | `prenda` | Características y stock |
| Inventario | `inspeccion_calidad` | Resultado del control de calidad |
| Catálogo | `item_catalogo` | Información pública del producto |
| Catálogo | `precio` | Precio base y precio final |
| Catálogo | `descuento` | Promociones aplicables |
| Carrito | `carrito` | Carrito activo del cliente |
| Carrito | `item_carrito` | Productos agregados al carrito |
| Ventas | `cliente` | Información comercial del comprador |
| Ventas | `pedido` | Cabecera de la compra |
| Ventas | `detalle_pedido` | Productos incluidos en el pedido |
| Pagos | `pago` | Transacción asociada al pedido |
| Pagos | `comprobante` | Comprobante generado por el pago |

## Estados principales

| Entidad | Estados utilizados |
|---|---|
| Lote | `RECIBIDO`, `PROCESADO` |
| Carrito | `ACTIVO` |
| Pedido | `PENDIENTE`, `PAGADO`, `CANCELADO` |
| Pago | `PENDIENTE`, `EXITOSO`, `FALLIDO` |
| Inspección | `APROBADO`, `OBSERVADO`, `DESCARTADO` |
# Colección Postman

## Descripción general

Postman se utiliza para probar los endpoints de Raymi Suprême a través del `API Gateway`.

La URL base utilizada para las pruebas es:

```http
http://localhost:7091
```

---

## Orden recomendado de pruebas

Para validar correctamente el flujo completo, se recomienda ejecutar las peticiones en el siguiente orden:

| Orden | Microservicio | Acción principal |
|---:|---|---|
| 1 | `ms-acopio` | Crear artesano y lote |
| 2 | `ms-inventario` | Crear categoría, prenda e inspección |
| 3 | `ms-catalogo` | Crear descuento, precio e ítem de catálogo |
| 4 | `ms-ventas` | Crear cliente |
| 5 | `ms-carrito` | Agregar producto al carrito |
| 6 | `ms-ventas` | Crear pedido mediante checkout |
| 7 | `ms-ventas` | Confirmar pedido |
| 8 | `ms-pagos` | Verificar pago |
| 9 | `ms-inventario` | Verificar stock actualizado |

---

## Variables recomendadas en Postman

Se recomienda crear variables para evitar escribir varias veces los mismos valores.

| Variable | Valor |
|---|---|
| `base_url` | `http://localhost:7091` |
| `cliente_id` | `1` |
| `prenda_id` | `1` |
| `carrito_id` | `1` |
| `pedido_id` | `1` |
| `token` | Token JWT obtenido desde Keycloak |

---

## Ejemplo usando variable base

```http
{{base_url}}/api/v1/artesanos
```

```http
{{base_url}}/api/v1/catalogo/productos
```

```http
{{base_url}}/api/v1/carrito/{{cliente_id}}
```

---

# Flujo de prueba en Postman

## 1. Crear artesano

```http
POST {{base_url}}/api/v1/artesanos
```

```json
{
  "nombres": "Rosa",
  "apellidos": "Quispe",
  "dni": "12345678",
  "comunidadOZona": "Juliaca",
  "telefono": "987654321"
}
```

---

## 2. Crear lote

```http
POST {{base_url}}/api/v1/lotes
```

```json
{
  "idArtesano": 1,
  "fechaCompra": "2026-06-17",
  "precioTotalPagado": 850.00,
  "cantidadTotalRecibida": 20,
  "descripcionGeneral": "Lote de fibra de alpaca premium",
  "estadoLote": "RECIBIDO"
}
```

---

## 3. Crear categoría

```http
POST {{base_url}}/api/v1/categorias
```

```json
{
  "nombre": "Chompas",
  "descripcion": "Prendas tejidas de alpaca"
}
```

---

## 4. Crear prenda

```http
POST {{base_url}}/api/v1/prendas
```

```json
{
  "idLote": 1,
  "idCategoria": 1,
  "codigoUnico": "CH-ALP-BEIGE-001",
  "nombrePrenda": "Chompa Alpaca Premium Beige",
  "talla": "M",
  "color": "Beige",
  "material": "Alpaca",
  "stock": 10
}
```

---

## 5. Crear inspección

```http
POST {{base_url}}/api/v1/inspecciones
```

```json
{
  "fechaInspeccion": "2026-06-17",
  "resultado": "APROBADO",
  "observaciones": "Prenda en buen estado",
  "idPrenda": 1
}
```

---

## 6. Crear descuento

```http
POST {{base_url}}/api/v1/precios/descuentos
```

```json
{
  "codigo": "DESC10",
  "descripcion": "Descuento de lanzamiento",
  "porcentaje": 10,
  "fechaInicio": "2026-06-17",
  "fechaFin": "2026-12-31"
}
```

---

## 7. Crear precio

```http
POST {{base_url}}/api/v1/precios/prendas
```

```json
{
  "prendaId": 1,
  "precioBase": 299.90,
  "descuentoId": 1
}
```

---

## 8. Crear ítem de catálogo

```http
POST {{base_url}}/api/v1/catalogo/items
```

```json
{
  "idPrenda": 1,
  "urlFoto": "https://example.com/chompa-beige.jpg",
  "descripcionPublica": "Chompa artesanal de alpaca premium color beige",
  "destacado": true,
  "activo": true
}
```

---

## 9. Crear cliente

```http
POST {{base_url}}/api/v1/clientes
```

```json
{
  "nombres": "Andrea Quispe",
  "email": "andrea@test.com",
  "pais": "Peru",
  "ciudad": "Juliaca",
  "direccionEnvio": "Av. Principal 123"
}
```

---

## 10. Agregar producto al carrito

```http
POST {{base_url}}/api/v1/carrito/1/items
```

```json
{
  "prendaId": 1,
  "nombrePrenda": "Temporal",
  "precio": 1,
  "cantidad": 2
}
```

### Validación esperada

Aunque se envíe precio `1`, el sistema debe usar el precio real desde `ms-catalogo`.

```text
Precio real: S/ 269.91
Cantidad: 2
Subtotal: S/ 539.82
```

---

## 11. Crear pedido mediante checkout

```http
POST {{base_url}}/api/v1/ventas/checkout/1
```

### Resultado esperado

```text
Pedido creado con estado PENDIENTE.
Carrito limpiado.
Pago no creado todavía.
Stock sin modificar.
```

---

## 12. Confirmar pedido

```http
POST {{base_url}}/api/v1/pedidos/1/confirmar
```

```json
{
  "metodoPago": "TARJETA"
}
```

### Resultado esperado

```text
Pedido: PAGADO
Pago: EXITOSO
Stock actualizado.
```

---

# Verificaciones finales

## Ver pedido

```http
GET {{base_url}}/api/v1/pedidos?idCliente=1
```

## Ver detalle del pedido

```http
GET {{base_url}}/api/v1/detalles-pedido?idPedido=1
```

## Ver pago

```http
GET {{base_url}}/api/v1/pagos/pedido/1
```

## Ver stock

```http
GET {{base_url}}/api/v1/prendas/1/stock
```

## Ver carrito

```http
GET {{base_url}}/api/v1/carrito/1
```

---

## Resultado final esperado

```text
Pedido: PAGADO
Pago: EXITOSO
Stock anterior: 10
Cantidad vendida: 2
Stock final: 8
Carrito: limpio
```

---

## Recomendación

No se debe crear manualmente un pedido ni un pago durante la prueba funcional completa.

El pedido debe generarse mediante:

```http
POST {{base_url}}/api/v1/ventas/checkout/1
```

El pago debe generarse mediante:

```http
POST {{base_url}}/api/v1/pedidos/1/confirmar
```

De esta forma se prueba correctamente la comunicación entre `ms-ventas`, `ms-carrito`, `ms-inventario` y `ms-pagos`.
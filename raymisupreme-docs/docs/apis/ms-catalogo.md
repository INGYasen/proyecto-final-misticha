# API - ms-catalogo

## Descripción general

El microservicio `ms-catalogo` administra la información comercial de las prendas que serán publicadas para la venta.

A diferencia de `ms-inventario`, que gestiona datos internos como stock, talla, color y material, `ms-catalogo` administra información visible para el cliente, como precio, descuento, imagen, descripción pública y estado de publicación.

---

## Responsabilidades principales

- Crear descuentos.
- Crear precios de prendas.
- Publicar ítems en el catálogo.
- Consultar productos publicados.
- Filtrar productos por material, color o talla.
- Consultar información de prendas desde `ms-inventario`.

---

## Gestión de descuentos

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear descuento | `POST` | `http://localhost:7091/api/v1/precios/descuentos` | Sí |
| Listar descuentos | `GET` | `http://localhost:7091/api/v1/precios/descuentos` | No |
| Actualizar descuento | `PUT` | `http://localhost:7091/api/v1/precios/descuentos/1` | Sí |

---

## Crear descuento

```http
POST http://localhost:7091/api/v1/precios/descuentos
```

### Body

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

## Gestión de precios

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear precio | `POST` | `http://localhost:7091/api/v1/precios/prendas` | Sí |
| Listar precios | `GET` | `http://localhost:7091/api/v1/precios/prendas` | No |
| Buscar precio por prenda | `GET` | `http://localhost:7091/api/v1/precios/prendas/1` | No |
| Actualizar precio | `PUT` | `http://localhost:7091/api/v1/precios/prendas/1` | Sí |
| Eliminar precio | `DELETE` | `http://localhost:7091/api/v1/precios/prendas/1` | No |

---

## Crear precio

```http
POST http://localhost:7091/api/v1/precios/prendas
```

### Body

```json
{
  "prendaId": 1,
  "precioBase": 299.90,
  "descuentoId": 1
}
```

### Resultado esperado

```text
Precio base: S/ 299.90
Descuento aplicado: 10%
Precio final esperado: S/ 269.91
```

---

## Gestión de ítems del catálogo

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear ítem | `POST` | `http://localhost:7091/api/v1/catalogo/items` | Sí |
| Listar ítems | `GET` | `http://localhost:7091/api/v1/catalogo/items` | No |
| Buscar ítem | `GET` | `http://localhost:7091/api/v1/catalogo/items/1` | No |
| Eliminar ítem | `DELETE` | `http://localhost:7091/api/v1/catalogo/items/1` | No |

---

## Crear ítem de catálogo

```http
POST http://localhost:7091/api/v1/catalogo/items
```

### Body

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

## Consulta de productos publicados

| Acción | Método | Endpoint |
|---|---:|---|
| Listar productos | `GET` | `http://localhost:7091/api/v1/catalogo/productos` |
| Buscar producto por ID | `GET` | `http://localhost:7091/api/v1/catalogo/productos/1` |
| Filtrar por material | `GET` | `http://localhost:7091/api/v1/catalogo/productos?material=Alpaca` |
| Filtrar por color | `GET` | `http://localhost:7091/api/v1/catalogo/productos?color=Beige` |
| Filtrar por talla | `GET` | `http://localhost:7091/api/v1/catalogo/productos?talla=M` |

---

## Comunicación con ms-inventario

`ms-catalogo` consulta información de la prenda desde `ms-inventario`.

```text
ms-catalogo → OpenFeign + Circuit Breaker → ms-inventario
```

---

## Endpoint para probar Feign + Circuit Breaker

```http
GET http://localhost:7091/api/v1/catalogo/productos/1
```

### Resultado esperado

El sistema debe mostrar un producto publicado con información comercial del catálogo e información técnica proveniente del inventario.

---

## Validación recomendada

Después de crear descuento, precio e ítem de catálogo, ejecutar:

```http
GET http://localhost:7091/api/v1/catalogo/productos
```

El producto debe aparecer publicado y disponible para ser usado por `ms-carrito`.
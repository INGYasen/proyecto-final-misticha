# API - ms-ventas

## Descripción general

El microservicio `ms-ventas` administra clientes, pedidos, detalles de pedido y el proceso de checkout.

Su función principal es convertir un carrito activo en un pedido con estado inicial `PENDIENTE`. Posteriormente, un usuario autorizado puede confirmar el pedido, procesar el pago y actualizar el inventario.

---

## Responsabilidades principales

- Registrar clientes.
- Consultar clientes.
- Crear pedidos desde el carrito.
- Consultar pedidos.
- Consultar detalles del pedido.
- Confirmar pedidos.
- Cancelar pedidos.
- Coordinar la comunicación con `ms-carrito`, `ms-inventario` y `ms-pagos`.

---

# Clientes

## Endpoints de clientes

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear cliente | `POST` | `http://localhost:7091/api/v1/clientes` | Sí |
| Listar clientes | `GET` | `http://localhost:7091/api/v1/clientes` | No |
| Buscar cliente | `GET` | `http://localhost:7091/api/v1/clientes/1` | No |
| Actualizar cliente | `PUT` | `http://localhost:7091/api/v1/clientes/1` | Sí |
| Eliminar cliente | `DELETE` | `http://localhost:7091/api/v1/clientes/1` | No |

---

## Crear cliente

```http
POST http://localhost:7091/api/v1/clientes
```

### Body

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

# Checkout

## Descripción del checkout

El checkout convierte el carrito activo de un cliente en un pedido con estado inicial `PENDIENTE`.

En esta etapa:

- Se crea el pedido.
- Se crean los detalles del pedido.
- Se limpia el carrito.
- No se procesa el pago todavía.
- No se descuenta stock todavía.

---

## Crear pedido desde carrito

```http
POST http://localhost:7091/api/v1/ventas/checkout/1
```

### Body

```text
No requiere body.
```

### Resultado esperado

```text
Pedido creado con estado PENDIENTE.
Detalle del pedido creado.
Carrito limpiado.
Pago no creado todavía.
Stock sin modificar.
```

---

# Pedidos

## Endpoints de pedidos

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Listar pedidos | `GET` | `http://localhost:7091/api/v1/pedidos` | No |
| Pedidos por cliente | `GET` | `http://localhost:7091/api/v1/pedidos?idCliente=1` | No |
| Obtener pedido | `GET` | `http://localhost:7091/api/v1/pedidos/1` | No |
| Detalles del pedido | `GET` | `http://localhost:7091/api/v1/detalles-pedido?idPedido=1` | No |
| Confirmar pedido | `POST` | `http://localhost:7091/api/v1/pedidos/1/confirmar` | Sí |
| Cancelar pedido | `POST` | `http://localhost:7091/api/v1/pedidos/1/cancelar` | No |

---

## Confirmar pedido y procesar pago

```http
POST http://localhost:7091/api/v1/pedidos/1/confirmar
```

### Body

```json
{
  "metodoPago": "TARJETA"
}
```

### Resultado esperado

```text
Pedido actualizado a PAGADO.
Pago creado con estado EXITOSO.
Stock reducido correctamente.
```

---

## Cancelar pedido

```http
POST http://localhost:7091/api/v1/pedidos/1/cancelar
```

### Body

```text
No requiere body.
```

### Resultado esperado

```text
Pedido actualizado a CANCELADO.
No se registra pago.
No se descuenta stock.
```

---

## Estados del pedido

| Estado | Descripción |
|---|---|
| `PENDIENTE` | Pedido creado desde el carrito, pero todavía no pagado. |
| `PAGADO` | Pedido confirmado, pago exitoso y stock actualizado. |
| `CANCELADO` | Pedido cancelado sin completar el pago. |

---

## Comunicaciones con otros microservicios

Al crear el pedido:

```text
ms-ventas → OpenFeign + Circuit Breaker → ms-carrito
ms-ventas → OpenFeign + Circuit Breaker → ms-inventario
```

Al confirmar el pago:

```text
ms-ventas → OpenFeign + Circuit Breaker → ms-pagos
ms-ventas → OpenFeign + Circuit Breaker → ms-inventario
```

---

## Verificación final

| Verificación | Endpoint |
|---|---|
| Pedido creado o pagado | `GET http://localhost:7091/api/v1/pedidos?idCliente=1` |
| Detalle creado | `GET http://localhost:7091/api/v1/detalles-pedido?idPedido=1` |
| Pago creado | `GET http://localhost:7091/api/v1/pagos/pedido/1` |
| Stock reducido | `GET http://localhost:7091/api/v1/prendas/1/stock` |
| Carrito limpiado | `GET http://localhost:7091/api/v1/carrito/1` |
# API - ms-pagos

## Descripción general

El microservicio `ms-pagos` administra los pagos asociados a los pedidos generados por `ms-ventas`.

En el flujo normal de compra, el pago no se crea manualmente. El pago se genera cuando se confirma un pedido pendiente desde `ms-ventas`.

---

## Responsabilidades principales

- Registrar pagos asociados a pedidos.
- Consultar pagos.
- Consultar pagos por pedido.
- Actualizar el estado de una transacción.
- Procesar pagos directos para pruebas aisladas.
- Generar y consultar comprobantes.

---

## Endpoints principales

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Listar pagos | `GET` | `http://localhost:7091/api/v1/pagos` | No |
| Obtener pago | `GET` | `http://localhost:7091/api/v1/pagos/1` | No |
| Obtener pago por pedido | `GET` | `http://localhost:7091/api/v1/pagos/pedido/1` | No |
| Actualizar estado | `PUT` | `http://localhost:7091/api/v1/pagos/1/estado` | Sí |
| Procesar pago directo | `POST` | `http://localhost:7091/api/v1/pagos/procesar` | Sí |
| Obtener comprobante | `GET` | `http://localhost:7091/api/pagos/comprobantes/1` | No |

---

## Consultar pagos

```http
GET http://localhost:7091/api/v1/pagos
```

### Resultado esperado

```text
Retorna la lista de pagos registrados.
```

---

## Consultar pago por pedido

```http
GET http://localhost:7091/api/v1/pagos/pedido/1
```

### Resultado esperado

```text
Retorna el pago asociado al pedido ID 1.
```

---

## Actualizar estado de pago

```http
PUT http://localhost:7091/api/v1/pagos/1/estado
```

### Body

```json
{
  "estado": "EXITOSO"
}
```

---

## Procesar pago directo

Este endpoint se utiliza solo para pruebas aisladas de `ms-pagos`.

No se recomienda usarlo durante el flujo normal de compra, porque el pago debe originarse desde la confirmación de un pedido en `ms-ventas`.

```http
POST http://localhost:7091/api/v1/pagos/procesar
```

### Body

```json
{
  "pedidoId": 1,
  "clienteId": 1,
  "monto": 539.82,
  "metodoPago": "TARJETA"
}
```

---

## Flujo normal de pago

```text
ms-ventas
   ↓
ms-pagos
   ↓
pago EXITOSO
```

---

## Confirmación desde ventas

El flujo recomendado es confirmar el pedido desde `ms-ventas`:

```http
POST http://localhost:7091/api/v1/pedidos/1/confirmar
```

### Body

```json
{
  "metodoPago": "TARJETA"
}
```

Al confirmar, `ms-ventas` invoca a `ms-pagos` y se registra el pago automáticamente.

---

## Seguridad de pagos

Los endpoints de pagos deben estar protegidos mediante roles.

Ejemplo de acceso directo al microservicio:

```http
GET http://localhost:8092/api/v1/pagos
```

| Rol | Acceso esperado |
|---|---|
| `SUPER_ADMIN` | Permitido |
| `VENDEDORA` | Restringido |

---

## Consideración técnica

`ms-pagos` no tiene Feign saliente en el flujo principal.  
El Circuit Breaker relacionado con pagos se prueba desde `ms-ventas`, cuando se confirma un pedido.

```text
ms-ventas → OpenFeign + Circuit Breaker → ms-pagos
```
# API - ms-carrito

## Descripción general

El microservicio `ms-carrito` administra el carrito de compras de un cliente.

Este servicio permite agregar productos, actualizar cantidades, eliminar ítems, vaciar el carrito y convertirlo posteriormente en un pedido mediante `ms-ventas`.

---

## Responsabilidades principales

- Crear o recuperar el carrito activo de un cliente.
- Agregar productos al carrito.
- Actualizar cantidades.
- Eliminar productos del carrito.
- Vaciar carrito.
- Validar productos contra `ms-catalogo`.
- Preparar el carrito para checkout.

---

## Endpoints principales

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Ver carrito activo | `GET` | `http://localhost:7091/api/v1/carrito/1` | No |
| Agregar ítem | `POST` | `http://localhost:7091/api/v1/carrito/1/items` | Sí |
| Actualizar cantidad | `PUT` | `http://localhost:7091/api/v1/carrito/1/items/1?cantidad=3` | No |
| Quitar ítem | `DELETE` | `http://localhost:7091/api/v1/carrito/1/items/1` | No |
| Vaciar carrito | `DELETE` | `http://localhost:7091/api/v1/carrito/1` | No |
| Convertir carrito | `PUT` | `http://localhost:7091/api/v1/carrito/1/convertir` | No |

---

## Ver carrito activo

```http
GET http://localhost:7091/api/v1/carrito/1
```

### Resultado esperado

```text
Retorna el carrito activo del cliente ID 1.
```

---

## Agregar ítem al carrito

```http
POST http://localhost:7091/api/v1/carrito/1/items
```

### Body de prueba

```json
{
  "prendaId": 1,
  "nombrePrenda": "Temporal",
  "precio": 1,
  "cantidad": 2
}
```

---

## Validación importante

Aunque el body envíe:

```text
nombrePrenda = Temporal
precio = 1
```

`ms-carrito` no debe utilizar esos valores directamente.

El servicio consulta `ms-catalogo` para obtener el nombre y precio reales del producto.

---

## Flujo de validación

<div style="display:flex; flex-direction:column; align-items:center; gap:12px; margin:25px 0 40px 0;">

  <div style="background:#3949ab; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    Cliente
    <br>
    <span style="font-weight:normal;">Solicita agregar producto</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#fb8c00; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    ms-carrito
    <br>
    <span style="font-weight:normal;">Recibe la solicitud</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#43a047; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    ms-catalogo
    <br>
    <span style="font-weight:normal;">Valida nombre, precio y estado</span>
  </div>

</div>

---

## Resultado esperado al agregar producto

```text
Nombre real usado: Chompa Alpaca Premium Beige
Precio real usado: S/ 269.91
Cantidad: 2
Subtotal esperado: S/ 539.82
```

---

## Actualizar cantidad

```http
PUT http://localhost:7091/api/v1/carrito/1/items/1?cantidad=3
```

### Resultado esperado

```text
La cantidad del ítem se actualiza a 3 unidades.
El subtotal se recalcula.
```

---

## Quitar ítem del carrito

```http
DELETE http://localhost:7091/api/v1/carrito/1/items/1
```

---

## Vaciar carrito

```http
DELETE http://localhost:7091/api/v1/carrito/1
```

---

## Comunicación con ms-catalogo

```text
ms-carrito → OpenFeign + Circuit Breaker → ms-catalogo
```

Esta comunicación se prueba al agregar un producto al carrito.

---

## Consideración técnica

`ms-carrito` no debe confiar únicamente en los datos enviados por el cliente, porque un usuario podría modificar el precio desde la solicitud.

Por eso, el precio, nombre y estado del producto deben validarse contra `ms-catalogo`.
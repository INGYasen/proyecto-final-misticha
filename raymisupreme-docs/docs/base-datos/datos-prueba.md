# Datos de Prueba

## Descripción general

Los siguientes datos permiten probar el flujo completo de Raymi Suprême, desde el registro de artesanos y prendas hasta la creación del carrito, generación del pedido, confirmación del pago y actualización del stock.

Estos datos deben cargarse respetando el orden indicado, porque varios registros dependen de datos creados previamente.

---

## Orden recomendado de prueba

El flujo funcional debe probarse en el siguiente orden:

<div style="display:flex; flex-direction:column; align-items:center; gap:12px; margin:25px 0 50px 0;">

  <div style="background:#5e35b1; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    1. ms-acopio
    <br>
    <span style="font-weight:normal;">Registrar artesano y lote</span>
  </div>

  <div style="font-size:26px;">↓</div>

  <div style="background:#1e88e5; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    2. ms-inventario
    <br>
    <span style="font-weight:normal;">Crear categoría, prenda e inspección</span>
  </div>

  <div style="font-size:26px;">↓</div>

  <div style="background:#43a047; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    3. ms-catalogo
    <br>
    <span style="font-weight:normal;">Crear descuento, precio e ítem de catálogo</span>
  </div>

  <div style="font-size:26px;">↓</div>

  <div style="background:#e53935; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    4. ms-ventas
    <br>
    <span style="font-weight:normal;">Registrar cliente</span>
  </div>

  <div style="font-size:26px;">↓</div>

  <div style="background:#fb8c00; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    5. ms-carrito
    <br>
    <span style="font-weight:normal;">Agregar producto al carrito</span>
  </div>

  <div style="font-size:26px;">↓</div>

  <div style="background:#8e24aa; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    6. Checkout y pago
    <br>
    <span style="font-weight:normal;">Crear pedido, confirmar pago y reducir stock</span>
  </div>

</div>

---

# 1. Datos para ms-acopio

## Objetivo

Crear la base inicial de producción: **artesano** y **lote de compra**.  
Estos datos serán utilizados posteriormente por `ms-inventario`.

---

## Crear artesano

**Endpoint:**

```http
POST http://localhost:7091/api/v1/artesanos
```

**Body:**

```json
{
  "nombres": "Rosa",
  "apellidos": "Quispe",
  "dni": "12345678",
  "comunidadOZona": "Juliaca",
  "telefono": "987654321"
}
```

**Resultado esperado:**

```text
Artesano creado correctamente con ID 1.
```

---

## Crear lote de compra

**Endpoint:**

```http
POST http://localhost:7091/api/v1/lotes
```

**Body:**

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

**Resultado esperado:**

```text
Lote creado correctamente y asociado al artesano Rosa Quispe.
```

---

## Endpoints de verificación

| Acción | Método | Endpoint |
|---|---:|---|
| Listar artesanos | GET | `http://localhost:7091/api/v1/artesanos` |
| Buscar artesano | GET | `http://localhost:7091/api/v1/artesanos/1` |
| Listar lotes | GET | `http://localhost:7091/api/v1/lotes` |
| Buscar lote | GET | `http://localhost:7091/api/v1/lotes/1` |

---

# 2. Datos para ms-inventario

## Objetivo

Crear la información interna de inventario: **categoría**, **prenda** e **inspección de calidad**.

Este microservicio puede consultar información del lote mediante `ms-acopio`.

---

## Crear categoría

**Endpoint:**

```http
POST http://localhost:7091/api/v1/categorias
```

**Body:**

```json
{
  "nombre": "Chompas",
  "descripcion": "Prendas tejidas de alpaca"
}
```

**Resultado esperado:**

```text
Categoría creada correctamente con ID 1.
```

---

## Crear prenda

**Endpoint:**

```http
POST http://localhost:7091/api/v1/prendas
```

**Body:**

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

**Resultado esperado:**

```text
Prenda creada correctamente con stock inicial de 10 unidades.
```

---

## Crear inspección de calidad

**Endpoint:**

```http
POST http://localhost:7091/api/v1/inspecciones
```

**Body:**

```json
{
  "fechaInspeccion": "2026-06-17",
  "resultado": "APROBADO",
  "observaciones": "Prenda en buen estado",
  "idPrenda": 1
}
```

**Resultado esperado:**

```text
Inspección registrada con resultado APROBADO.
```

---

## Endpoints de verificación

| Acción | Método | Endpoint |
|---|---:|---|
| Listar categorías | GET | `http://localhost:7091/api/v1/categorias` |
| Listar prendas | GET | `http://localhost:7091/api/v1/prendas` |
| Buscar prenda | GET | `http://localhost:7091/api/v1/prendas/1` |
| Consultar stock | GET | `http://localhost:7091/api/v1/prendas/1/stock` |
| Prendas por lote | GET | `http://localhost:7091/api/v1/prendas/lote/1` |
| Detalle completo | GET | `http://localhost:7091/api/v1/prendas/1/detalle-completo` |
| Inspecciones por prenda | GET | `http://localhost:7091/api/v1/inspecciones/prenda/1` |

---

## Comunicación probada

```text
ms-inventario → OpenFeign + Circuit Breaker → ms-acopio
```

Esta comunicación se prueba cuando se consulta el detalle completo de una prenda.

---

# 3. Datos para ms-catalogo

## Objetivo

Publicar la prenda en el catálogo comercial.  
Aquí se agregan datos visibles para el cliente, como precio, descuento, imagen y descripción pública.

---

## Crear descuento

**Endpoint:**

```http
POST http://localhost:7091/api/v1/precios/descuentos
```

**Body:**

```json
{
  "codigo": "DESC10",
  "descripcion": "Descuento de lanzamiento",
  "porcentaje": 10,
  "fechaInicio": "2026-06-17",
  "fechaFin": "2026-12-31"
}
```

**Resultado esperado:**

```text
Descuento creado correctamente con ID 1.
```

---

## Crear precio

**Endpoint:**

```http
POST http://localhost:7091/api/v1/precios/prendas
```

**Body:**

```json
{
  "prendaId": 1,
  "precioBase": 299.90,
  "descuentoId": 1
}
```

**Resultado esperado:**

```text
Precio creado correctamente para la prenda ID 1.
Precio base: S/ 299.90
Descuento: 10%
Precio final esperado: S/ 269.91
```

---

## Crear ítem de catálogo

**Endpoint:**

```http
POST http://localhost:7091/api/v1/catalogo/items
```

**Body:**

```json
{
  "idPrenda": 1,
  "urlFoto": "https://example.com/chompa-beige.jpg",
  "descripcionPublica": "Chompa artesanal de alpaca premium color beige",
  "destacado": true,
  "activo": true
}
```

**Resultado esperado:**

```text
Producto publicado correctamente en el catálogo.
```

---

## Endpoints de verificación

| Acción | Método | Endpoint |
|---|---:|---|
| Listar productos | GET | `http://localhost:7091/api/v1/catalogo/productos` |
| Buscar producto | GET | `http://localhost:7091/api/v1/catalogo/productos/1` |
| Filtrar por material | GET | `http://localhost:7091/api/v1/catalogo/productos?material=Alpaca` |
| Filtrar por color | GET | `http://localhost:7091/api/v1/catalogo/productos?color=Beige` |
| Filtrar por talla | GET | `http://localhost:7091/api/v1/catalogo/productos?talla=M` |
| Listar precios | GET | `http://localhost:7091/api/v1/precios/prendas` |
| Buscar precio por prenda | GET | `http://localhost:7091/api/v1/precios/prendas/1` |

---

## Comunicación probada

```text
ms-catalogo → OpenFeign + Circuit Breaker → ms-inventario
```

Esta comunicación se prueba cuando se consulta el producto completo desde el catálogo.

---

# 4. Datos para ms-ventas

## Objetivo

Registrar un cliente que posteriormente podrá tener un carrito, generar un pedido y realizar una compra.

---

## Crear cliente

**Endpoint:**

```http
POST http://localhost:7091/api/v1/clientes
```

**Body:**

```json
{
  "nombres": "Andrea Quispe",
  "email": "andrea@test.com",
  "pais": "Peru",
  "ciudad": "Juliaca",
  "direccionEnvio": "Av. Principal 123"
}
```

**Resultado esperado:**

```text
Cliente creado correctamente.
```

---

## Endpoints de verificación

| Acción | Método | Endpoint |
|---|---:|---|
| Listar clientes | GET | `http://localhost:7091/api/v1/clientes` |
| Buscar cliente | GET | `http://localhost:7091/api/v1/clientes/1` |
| Actualizar cliente | PUT | `http://localhost:7091/api/v1/clientes/1` |
| Eliminar cliente | DELETE | `http://localhost:7091/api/v1/clientes/1` |

---

# 5. Datos para ms-carrito

## Objetivo

Crear o recuperar el carrito activo de un cliente y agregar productos validados contra el catálogo.

---

## Ver carrito activo

**Endpoint:**

```http
GET http://localhost:7091/api/v1/carrito/1
```

**Resultado esperado:**

```text
Se obtiene el carrito activo del cliente ID 1.
Si no existe, el sistema puede crearlo automáticamente según la lógica implementada.
```

---

## Agregar ítem al carrito

**Endpoint:**

```http
POST http://localhost:7091/api/v1/carrito/1/items
```

**Body de prueba:**

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

Aunque en el body se envíe:

```text
nombrePrenda = Temporal
precio = 1
```

`ms-carrito` no debe confiar en esos valores.  
El servicio consulta `ms-catalogo` y utiliza el nombre y precio reales del catálogo.

**Resultado esperado:**

```text
Nombre real usado: Chompa Alpaca Premium Beige
Precio real usado: S/ 269.91
Cantidad: 2
Subtotal esperado: S/ 539.82
```

---

## Endpoints de verificación

| Acción | Método | Endpoint |
|---|---:|---|
| Ver carrito activo | GET | `http://localhost:7091/api/v1/carrito/1` |
| Agregar ítem | POST | `http://localhost:7091/api/v1/carrito/1/items` |
| Actualizar cantidad | PUT | `http://localhost:7091/api/v1/carrito/1/items/1?cantidad=3` |
| Quitar ítem | DELETE | `http://localhost:7091/api/v1/carrito/1/items/1` |
| Vaciar carrito | DELETE | `http://localhost:7091/api/v1/carrito/1` |
| Convertir carrito | PUT | `http://localhost:7091/api/v1/carrito/1/convertir` |

---

## Comunicación probada

```text
ms-carrito → OpenFeign + Circuit Breaker → ms-catalogo
```

Esta comunicación se prueba al agregar un producto al carrito.

---

# 6. Checkout en ms-ventas

## Objetivo

Convertir el carrito activo en un pedido con estado inicial `PENDIENTE`.

El checkout todavía no procesa el pago ni descuenta stock.  
Primero crea el pedido y sus detalles. Luego, un usuario autorizado puede confirmar o cancelar el pedido.

---

## Crear pedido pendiente desde el carrito

**Endpoint:**

```http
POST http://localhost:7091/api/v1/ventas/checkout/1
```

**Body:**

```text
No requiere body.
```

**Resultado esperado:**

```text
Pedido creado con estado PENDIENTE.
Detalle del pedido creado.
Carrito limpiado.
Pago todavía no existe.
Stock todavía no baja.
```

---

## Confirmar y pagar pedido

**Endpoint:**

```http
POST http://localhost:7091/api/v1/pedidos/1/confirmar
```

**Body:**

```json
{
  "metodoPago": "TARJETA"
}
```

**Resultado esperado:**

```text
Pedido actualizado a PAGADO.
Pago creado con estado EXITOSO.
Stock reducido correctamente.
```

---

## Cancelar pedido

**Endpoint:**

```http
POST http://localhost:7091/api/v1/pedidos/1/cancelar
```

**Body:**

```text
No requiere body.
```

**Resultado esperado:**

```text
Pedido actualizado a CANCELADO.
No se registra pago.
No se descuenta stock.
```

---

## Endpoints de verificación

| Acción | Método | Endpoint |
|---|---:|---|
| Listar pedidos | GET | `http://localhost:7091/api/v1/pedidos` |
| Pedidos por cliente | GET | `http://localhost:7091/api/v1/pedidos?idCliente=1` |
| Obtener pedido | GET | `http://localhost:7091/api/v1/pedidos/1` |
| Detalle del pedido | GET | `http://localhost:7091/api/v1/detalles-pedido?idPedido=1` |
| Confirmar pedido | POST | `http://localhost:7091/api/v1/pedidos/1/confirmar` |
| Cancelar pedido | POST | `http://localhost:7091/api/v1/pedidos/1/cancelar` |

---

## Comunicaciones probadas

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

# 7. Datos para ms-pagos

## Objetivo

Validar el pago asociado a un pedido.

En el flujo normal, el pago no debe crearse manualmente.  
El pago se genera automáticamente cuando se confirma un pedido pendiente desde `ms-ventas`.

---

## Consultar pagos

| Acción | Método | Endpoint |
|---|---:|---|
| Listar pagos | GET | `http://localhost:7091/api/v1/pagos` |
| Obtener pago | GET | `http://localhost:7091/api/v1/pagos/1` |
| Obtener pago por pedido | GET | `http://localhost:7091/api/v1/pagos/pedido/1` |
| Actualizar estado | PUT | `http://localhost:7091/api/v1/pagos/1/estado` |
| Procesar pago directo | POST | `http://localhost:7091/api/v1/pagos/procesar` |
| Obtener comprobante | GET | `http://localhost:7091/api/pagos/comprobantes/1` |

---

## Procesar pago directo solo para prueba aislada

Este endpoint no se recomienda para el flujo normal de compra.  
Solo debe utilizarse si se desea probar `ms-pagos` de manera aislada.

**Endpoint:**

```http
POST http://localhost:7091/api/v1/pagos/procesar
```

**Body:**

```json
{
  "pedidoId": 1,
  "clienteId": 1,
  "monto": 539.82,
  "metodoPago": "TARJETA"
}
```

---

## Actualizar estado de pago

**Endpoint:**

```http
PUT http://localhost:7091/api/v1/pagos/1/estado
```

**Body:**

```json
{
  "estado": "EXITOSO"
}
```

---

## Seguridad de pagos

El endpoint de pagos debe estar protegido por roles.

Ejemplo:

```text
GET http://localhost:8092/api/v1/pagos
```

Debe permitir acceso a:

```text
SUPER_ADMIN
```

Y debe restringir acceso a:

```text
VENDEDORA
```

Esto permite proteger operaciones sensibles relacionadas con pagos y transacciones.

---

# 8. Revisión final después del checkout

Cuando se complete el flujo de compra, se debe revisar el estado de cada módulo.

| Verificación | Endpoint | Resultado esperado |
|---|---|---|
| Pedido creado o pagado | `GET http://localhost:7091/api/v1/pedidos?idCliente=1` | Pedido en estado `PENDIENTE` o `PAGADO` |
| Detalle creado | `GET http://localhost:7091/api/v1/detalles-pedido?idPedido=1` | Detalle con prenda, cantidad y subtotal |
| Pago creado | `GET http://localhost:7091/api/v1/pagos/pedido/1` | Pago con estado `EXITOSO` |
| Stock reducido | `GET http://localhost:7091/api/v1/prendas/1/stock` | Stock final reducido |
| Carrito limpiado | `GET http://localhost:7091/api/v1/carrito/1` | Carrito vacío o sin ítems activos |

---

## Resultado esperado del checkout

Al crear el pedido:

```text
Pedido
├── Cliente: Andrea Quispe
├── Estado inicial: PENDIENTE
├── Total: S/ 539.82
└── Detalle
    ├── Prenda: Chompa Alpaca Premium Beige
    ├── Cantidad: 2
    └── Precio unitario: S/ 269.91
```

Después de confirmar el pedido:

```text
Pedido: PAGADO
Pago: EXITOSO
Stock anterior: 10
Cantidad vendida: 2
Stock final: 8
```

---

## Resumen de validaciones importantes

| Validación | Resultado esperado |
|---|---|
| `ms-carrito` valida producto contra `ms-catalogo` | Usa precio real y nombre real del catálogo |
| Checkout crea pedido | Pedido inicia como `PENDIENTE` |
| Confirmación genera pago | Pago queda en estado `EXITOSO` |
| Confirmación actualiza inventario | Stock baja de 10 a 8 |
| Cancelación no descuenta stock | Pedido queda `CANCELADO` |
| Carrito se limpia después del checkout | Carrito queda sin ítems activos |
| Pagos protegidos por rol | `SUPER_ADMIN` puede consultar pagos, `VENDEDORA` no |

---

## Nota final

No se recomienda insertar manualmente pedidos ni pagos durante una prueba funcional completa.

El pedido debe ser creado por `ms-ventas` mediante el checkout.  
El pago debe ser creado por `ms-pagos` cuando se confirma el pedido.

Esto permite validar correctamente la comunicación entre microservicios, el uso de OpenFeign, Circuit Breaker, la consistencia del flujo y la actualización del inventario.
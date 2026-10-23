# 🗂️ Endpoints Completos — RaymiSupreme
> Base URL: `http://localhost:8080` (Gateway)  
> Auth: **Bearer Token** en todos los endpoints protegidos  
> Header: `Authorization: Bearer <token>` · `Content-Type: application/json`

---

## 🔑 ROLES
| Rol | Descripción |
|-----|-------------|
| `SUPER_ADMIN` | Acceso total |
| `VENDEDORA` | Solo lectura operativa |
| *(autenticado)* | Cualquier token válido (flujo cliente web) |
| *(público)* | Sin token |

---

## 🟢 PÚBLICOS — Sin token

### Catálogo Público (ms-catalogo)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/catalogo/public/home` | Home de la tienda |
| GET | `/api/catalogo/public/productos` | Listar productos |
| GET | `/api/catalogo/public/productos/{id}` | Detalle de producto |
| GET | `/api/catalogo/public/categorias` | Listar categorías |
| GET | `/api/catalogo/public/promociones` | Listar promociones |
| GET | `/api/v1/catalogo/prendas` | Listar prendas (con filtros opcionales) |
| GET | `/api/v1/catalogo/prendas/{idPrenda}` | Detalle de prenda |
| GET | `/api/v1/catalogo/productos` | Productos del catálogo |

**Query params para `/api/v1/catalogo/prendas`:**
```
?idCategoria=1&material=lana&color=rojo&talla=M
```

---

## 🟡 AUTENTICADO — Cualquier token válido (flujo cliente web)

### Clientes (ms-ventas)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/clientes` | ↓ |

```json
{
  "nombres": "Juan Pérez",
  "email": "juan@email.com",
  "pais": "Perú",
  "ciudad": "Lima",
  "direccionEnvio": "Av. Ejemplo 123"
}
```

### Pedidos (ms-ventas)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/pedidos` | ↓ |

```json
{
  "idCliente": 1,
  "fechaPedido": "2025-06-16T10:00:00",
  "estadoPedido": "PENDIENTE",
  "totalPagar": 150.00
}
```

| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/pedidos/{idPedido}/confirmar` | ↓ |

```json
{
  "metodoPago": "YAPE"
}
```

### Detalle de Pedido (ms-ventas)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/detalles` | ↓ |

```json
{
  "idPedido": 1,
  "idPrenda": 3,
  "cantidad": 2,
  "precioUnitario": 75.00
}
```

### Checkout (ms-ventas)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/ventas/checkout/{idCliente}` | ↓ (opcional) |

```json
{
  "metodoPago": "TARJETA"
}
```

### Pagos (ms-pagos)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/pagos/procesar` | ↓ |

```json
{
  "pedidoId": 1,
  "clienteId": 1,
  "monto": 150.00,
  "metodoPago": "YAPE"
}
```

### Carrito (ms-carrito)
| Método | URL | Body / Params |
|--------|-----|---------------|
| GET | `/api/v1/carrito/{clienteId}` | — |
| POST | `/api/v1/carrito/{clienteId}/items` | ↓ |

```json
{
  "prendaId": 3,
  "nombrePrenda": "Chalina andina",
  "precio": 75.00,
  "cantidad": 2
}
```

| Método | URL | Params |
|--------|-----|--------|
| PUT | `/api/v1/carrito/{clienteId}/items/{itemId}` | `?cantidad=3` |
| DELETE | `/api/v1/carrito/{clienteId}/items/{itemId}` | — |
| DELETE | `/api/v1/carrito/{clienteId}` | — |
| PUT | `/api/v1/carrito/{clienteId}/convertir` | — |

---

## 🔵 VENDEDORA — Lectura operativa

### Acopio (ms-acopio)
| Método | URL |
|--------|-----|
| GET | `/api/v1/artesanos` |
| GET | `/api/v1/artesanos/{id}` |
| GET | `/api/v1/lotes` |
| GET | `/api/v1/lotes/{id}` |

### Inventario (ms-inventario)
| Método | URL |
|--------|-----|
| GET | `/api/v1/categorias` |
| GET | `/api/v1/categorias/{id}` |
| GET | `/api/v1/prendas` |
| GET | `/api/v1/prendas/{id}` |
| GET | `/api/v1/prendas/{id}/detalle-completo` |
| GET | `/api/v1/prendas/{id}/stock` |
| GET | `/api/v1/prendas/lote/{idLote}` |
| GET | `/api/v1/inspecciones` |
| GET | `/api/v1/inspecciones/{id}` |
| GET | `/api/v1/inspecciones/prenda/{idPrenda}` |

### Ventas (ms-ventas)
| Método | URL |
|--------|-----|
| GET | `/api/v1/clientes` |
| GET | `/api/v1/clientes/{idCliente}` |
| GET | `/api/v1/pedidos` |
| GET | `/api/v1/pedidos?idCliente=1` |
| GET | `/api/v1/pedidos/{idPedido}` |
| POST | `/api/v1/pedidos/{idPedido}/confirmar` (también VENDEDORA) |
| GET | `/api/v1/detalles` |
| GET | `/api/v1/detalles?idPedido=1` |
| GET | `/api/v1/detalles/{idDetalle}` |

### Catálogo Admin (ms-catalogo)
| Método | URL |
|--------|-----|
| GET | `/api/v1/catalogo/items` |
| GET | `/api/v1/catalogo/items/{id}` |
| GET | `/api/v1/precios/prendas` |
| GET | `/api/v1/precios/prendas/{prendaId}` |
| GET | `/api/v1/precios/descuentos` |

---

## 🔴 SUPER_ADMIN — Acceso total

### Auth / Usuarios (ms-auth)
| Método | URL | Body |
|--------|-----|------|
| GET | `/api/v1/auth` | — |
| GET | `/api/v1/auth/{id}` | — |
| POST | `/api/v1/auth` | ↓ |

```json
{
  "username": "vendedora01",
  "email": "vendedora@tienda.com",
  "firstName": "María",
  "lastName": "Quispe",
  "password": "pass1234",
  "roles": ["VENDEDORA"],
  "enabled": true
}
```

| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/v1/auth/{id}` | (mismo body que POST) |
| DELETE | `/api/v1/auth/{id}` | — |
| PATCH | `/api/v1/auth/{id}/estado?enabled=true` | — |

---

### Artesanos (ms-acopio)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/artesanos` | ↓ |

```json
{
  "nombres": "Celestina",
  "apellidos": "Huanca",
  "dni": "12345678",
  "comunidadOZona": "Pisac, Cusco",
  "telefono": "987654321"
}
```

| Método | URL |
|--------|-----|
| DELETE | `/api/v1/artesanos/{id}` |

---

### Lotes de Compra (ms-acopio)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/lotes` | ↓ |

```json
{
  "idArtesano": 1,
  "fechaCompra": "2025-06-10",
  "precioTotalPagado": 2500.00,
  "cantidadTotalRecibida": 50,
  "descripcionGeneral": "Lote de chales y chalinas",
  "estadoLote": "RECIBIDO"
}
```

---

### Categorías (ms-inventario)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/categorias` | ↓ |

```json
{
  "nombre": "Chalinas",
  "descripcion": "Chalinas tejidas a mano"
}
```

---

### Prendas (ms-inventario)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/prendas` | ↓ |

```json
{
  "nombrePrenda": "Chompa Alpaca",
  "idLote": 1,
  "idCategoria": 2,
  "codigoSku": "CHA-001-ROJ-M",
  "talla": "M",
  "color": "Rojo",
  "material": "Lana de alpaca",
  "stock": 10
}
```

| Método | URL | Params |
|--------|-----|--------|
| PUT | `/api/v1/prendas/{id}/descontar` | `?cantidad=3` |

---

### Inspecciones de Calidad (ms-inventario)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/inspecciones` | ↓ |

```json
{
  "fechaInspeccion": "2025-06-11",
  "resultado": "APROBADO",
  "observaciones": "Sin defectos visibles",
  "idPrenda": 1
}
```

---

### Items de Catálogo (ms-catalogo)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/catalogo/items` | ↓ |

```json
{
  "idPrenda": 1,
  "urlFoto": "https://storage.example.com/chalina-roja.jpg",
  "descripcionPublica": "Hermosa chalina roja tejida a mano en Pisac",
  "destacado": true,
  "activo": true
}
```

| Método | URL |
|--------|-----|
| DELETE | `/api/v1/catalogo/items/{id}` |

---

### Precios (ms-catalogo)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/precios/prendas` | ↓ |

```json
{
  "prendaId": 1,
  "nombre": "Chalina Roja Premium",
  "precioBase": 85.00,
  "descuentoId": null
}
```

| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/v1/precios/prendas/{id}` | (mismo body) |
| DELETE | `/api/v1/precios/prendas/{id}` | — |

---

### Descuentos (ms-catalogo)
| Método | URL | Body |
|--------|-----|------|
| POST | `/api/v1/precios/descuentos` | ↓ |

```json
{
  "codigo": "VERANO25",
  "descripcion": "Descuento de verano 2025",
  "porcentaje": 15.00,
  "fechaInicio": "2025-12-01",
  "fechaFin": "2026-01-31"
}
```

| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/v1/precios/descuentos/{id}` | (mismo body) |

---

### Ventas — Gestión Admin (ms-ventas)
| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/v1/clientes/{idCliente}` | ↓ |

```json
{
  "nombres": "Juan Pérez Actualizado",
  "email": "juan@nuevo.com",
  "pais": "Perú",
  "ciudad": "Cusco",
  "direccionEnvio": "Calle Nueva 456"
}
```

| Método | URL |
|--------|-----|
| DELETE | `/api/v1/clientes/{idCliente}` |

| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/v1/pedidos/{idPedido}` | ↓ |

```json
{
  "idCliente": 1,
  "fechaPedido": "2025-06-16T10:00:00",
  "estadoPedido": "ENTREGADO",
  "totalPagar": 150.00
}
```

| Método | URL |
|--------|-----|
| DELETE | `/api/v1/pedidos/{idPedido}` |

| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/v1/detalles/{idDetalle}` | ↓ |

```json
{
  "idPedido": 1,
  "idPrenda": 3,
  "cantidad": 1,
  "precioUnitario": 75.00
}
```

| Método | URL |
|--------|-----|
| DELETE | `/api/v1/detalles/{idDetalle}` |

---

### Pagos — Info financiera (ms-pagos)
| Método | URL |
|--------|-----|
| GET | `/api/pagos` |
| GET | `/api/pagos/{id}` |
| GET | `/api/pagos/pedido/{pedidoId}` |

| Método | URL | Body |
|--------|-----|------|
| PUT | `/api/pagos/{id}/estado` | ↓ |

```json
{
  "estado": "COMPLETADO"
}
```

### Comprobantes (ms-pagos)
| Método | URL |
|--------|-----|
| GET | `/api/pagos/comprobantes/{pagoId}` |

---

## 📋 RESUMEN POR ROL

| Rol | Puede |
|-----|-------|
| **Público** | Ver catálogo, prendas y promociones |
| **Autenticado** | Lo anterior + crear cliente, pedido, detalle, pagar, usar carrito, checkout |
| **VENDEDORA** | Lo anterior + consultar acopio, inventario, clientes, pedidos, catálogo admin, precios |
| **SUPER_ADMIN** | Todo: crear/editar/eliminar en todos los microservicios, ver pagos y comprobantes |
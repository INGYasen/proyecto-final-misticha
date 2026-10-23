# Protección de Endpoints

## Recursos públicos

No necesitan token:

| Método | Ruta |
|---|---|
| `GET` | `/api/catalogo/public/**` |
| `GET` | `/api/v1/catalogo/productos` |
| `GET` | `/api/v1/catalogo/productos/**` |
| `GET` | `/api/v1/prendas/public/**` |
| Varios | `/actuator/health` y `/actuator/info` |
| Varios | Rutas configuradas de Swagger y OpenAPI |

## Autenticación y usuarios

| Método | Ruta | Acceso |
|---|---|---|
| Todos | `/api/v1/auth/**` | `SUPER_ADMIN` |

`AuthController` también está protegido completamente con `SUPER_ADMIN`.

## Acopio

| Método | Ruta | Acceso |
|---|---|---|
| `GET` | `/api/v1/artesanos/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST` | `/api/v1/artesanos` | `SUPER_ADMIN` |
| `DELETE` | `/api/v1/artesanos/{id}` | `SUPER_ADMIN` |
| `GET` | `/api/v1/lotes/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST` | `/api/v1/lotes` | `SUPER_ADMIN` |
| `GET` | `/api/v1/acopio/instancia` | `SUPER_ADMIN` |

## Inventario

| Método | Ruta | Acceso |
|---|---|---|
| `GET` | `/api/v1/categorias/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST` | `/api/v1/categorias` | `SUPER_ADMIN` |
| `GET` | `/api/v1/prendas/public/{id}` | Público |
| `GET` | `/api/v1/prendas/{id}/stock` | Autenticado |
| `GET` | Otras rutas `/api/v1/prendas/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST` | `/api/v1/prendas` | `SUPER_ADMIN` |
| `PUT` | `/api/v1/prendas/{id}/descontar` | Autenticado |
| `GET` | `/api/v1/inspecciones/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST` | `/api/v1/inspecciones` | `SUPER_ADMIN` |
| `GET` | `/api/v1/inventario/instancia` | `SUPER_ADMIN` |

## Catálogo

| Método | Ruta | Acceso |
|---|---|---|
| `GET` | `/api/catalogo/public/**` | Público |
| `GET` | `/api/v1/catalogo/productos/**` | Público |
| `GET` | `/api/v1/catalogo/items/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST`, `DELETE` | `/api/v1/catalogo/items/**` | `SUPER_ADMIN` |
| `GET` | `/api/v1/precios/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST`, `PUT`, `DELETE` | Precios y descuentos | `SUPER_ADMIN` |
| `GET` | Endpoints de instancia | `SUPER_ADMIN` |

## Carrito

Todas las operaciones requieren un token válido:

| Método | Ruta |
|---|---|
| `GET` | `/api/v1/carrito/{clienteId}` |
| `POST` | `/api/v1/carrito/{clienteId}/items` |
| `PUT` | `/api/v1/carrito/{clienteId}/items/{itemId}` |
| `DELETE` | `/api/v1/carrito/{clienteId}/items/{itemId}` |
| `DELETE` | `/api/v1/carrito/{clienteId}` |
| `PUT` | `/api/v1/carrito/{clienteId}/convertir` |

Actualmente no existe un `@PreAuthorize` por rol en `CarritoController`.

## Ventas

| Método | Ruta | Acceso efectivo |
|---|---|---|
| `POST` | `/api/v1/clientes` | Autenticado |
| `GET` | `/api/v1/clientes/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `PUT`, `DELETE` | `/api/v1/clientes/**` | `SUPER_ADMIN` |
| `POST` | `/api/v1/ventas/checkout/{idCliente}` | Autenticado |
| `POST` | `/api/v1/pedidos` | Autenticado |
| `GET` | `/api/v1/pedidos/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `PUT`, `DELETE` | `/api/v1/pedidos/**` | `SUPER_ADMIN` |
| `POST` | `/api/v1/pedidos/{id}/confirmar` | `SUPER_ADMIN` |
| `POST` | `/api/v1/pedidos/{id}/cancelar` | `SUPER_ADMIN` |
| `GET` | `/api/v1/detalles/**` | `VENDEDORA` o `SUPER_ADMIN` |
| `POST` | `/api/v1/detalles` | Autenticado |
| `PUT`, `DELETE` | `/api/v1/detalles/**` | `SUPER_ADMIN` |

Aunque el Gateway incluye una regla que permite a `VENDEDORA` confirmar pedidos, `PedidoController` exige `SUPER_ADMIN`. Por ello, el acceso efectivo es únicamente para `SUPER_ADMIN`.

## Pagos

| Método | Ruta | Acceso |
|---|---|---|
| `POST` | `/api/v1/pagos/procesar` | Autenticado |
| `GET` | `/api/v1/pagos` | `SUPER_ADMIN` |
| `GET` | `/api/v1/pagos/{id}` | `SUPER_ADMIN` |
| `GET` | `/api/v1/pagos/pedido/{id}` | `SUPER_ADMIN` |
| `PUT` | `/api/v1/pagos/{id}/estado` | `SUPER_ADMIN` |
| `GET` | `/api/pagos/comprobantes/{pagoId}` | `SUPER_ADMIN` |

La información financiera está restringida al superadministrador.
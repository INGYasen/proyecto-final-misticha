# Convenciones de API

## Descripción general

Las APIs de Raymi Suprême se exponen a través del `API Gateway`, utilizando como puerto principal:

```http
http://localhost:7091
```

El Gateway centraliza las solicitudes externas y las redirige hacia el microservicio correspondiente mediante Eureka Server.

---

## Estructura base de rutas

Todas las rutas principales siguen la siguiente estructura:

```http
http://localhost:7091/api/v1/{recurso}
```

Ejemplos:

```http
http://localhost:7091/api/v1/artesanos
http://localhost:7091/api/v1/prendas
http://localhost:7091/api/v1/catalogo/productos
http://localhost:7091/api/v1/carrito/1
http://localhost:7091/api/v1/pedidos
http://localhost:7091/api/v1/pagos
```

---

## Métodos HTTP utilizados

| Método | Uso |
|---|---|
| `GET` | Consultar información. |
| `POST` | Crear registros o ejecutar operaciones. |
| `PUT` | Actualizar información existente. |
| `DELETE` | Eliminar o limpiar registros. |

---

## Formato de intercambio de datos

La mayoría de endpoints que reciben información utilizan formato JSON.

Ejemplo:

```json
{
  "nombres": "Rosa",
  "apellidos": "Quispe",
  "dni": "12345678"
}
```

---

## Cabeceras recomendadas

Para peticiones con body JSON:

```http
Content-Type: application/json
```

Para endpoints protegidos por seguridad:

```http
Authorization: Bearer {token_jwt}
```

---

## Estados comunes de respuesta

| Código | Significado |
|---:|---|
| `200 OK` | La operación se ejecutó correctamente. |
| `201 Created` | El recurso fue creado correctamente. |
| `400 Bad Request` | La solicitud contiene datos inválidos. |
| `401 Unauthorized` | No se envió un token válido. |
| `403 Forbidden` | El usuario no tiene permisos suficientes. |
| `404 Not Found` | El recurso solicitado no existe. |
| `500 Internal Server Error` | Error interno del servicio. |

---

## Enrutamiento principal

| Ruta | Microservicio destino |
|---|---|
| `/api/v1/auth/**` | `ms-auth` |
| `/api/v1/artesanos/**` | `ms-acopio` |
| `/api/v1/lotes/**` | `ms-acopio` |
| `/api/v1/categorias/**` | `ms-inventario` |
| `/api/v1/prendas/**` | `ms-inventario` |
| `/api/v1/inspecciones/**` | `ms-inventario` |
| `/api/v1/catalogo/**` | `ms-catalogo` |
| `/api/v1/precios/**` | `ms-catalogo` |
| `/api/v1/carrito/**` | `ms-carrito` |
| `/api/v1/clientes/**` | `ms-ventas` |
| `/api/v1/pedidos/**` | `ms-ventas` |
| `/api/v1/ventas/**` | `ms-ventas` |
| `/api/v1/detalles-pedido/**` | `ms-ventas` |
| `/api/v1/pagos/**` | `ms-pagos` |

---

## Consideraciones importantes

- Las peticiones externas deben ingresar por el `API Gateway`.
- Los microservicios no deben ser consumidos directamente desde el frontend.
- Cada microservicio administra sus propios datos.
- La comunicación entre servicios se realiza mediante OpenFeign.
- Eureka Server permite descubrir dinámicamente las instancias disponibles.
- Los endpoints sensibles deben protegerse mediante Keycloak y roles.
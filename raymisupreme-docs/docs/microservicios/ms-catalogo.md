

# Microservicio de Catálogo

`ms-catalogo` transforma una prenda del inventario en un producto disponible para la venta.

## Responsabilidades

- Publicar prendas.
- Agregar fotografías y descripciones.
- Definir precios.
- Aplicar descuentos.
- Activar o desactivar productos.
- Marcar productos destacados.
- Permitir búsquedas por talla, color y material.

## Recursos principales

| Recurso | Ruta |
|---|---|
| Productos | `/api/v1/catalogo/productos` |
| Ítems del catálogo | `/api/v1/catalogo/items` |
| Precios | `/api/v1/precios/prendas` |
| Descuentos | `/api/v1/precios/descuentos` |
| Catálogo público | `/api/catalogo/public` |

## Comunicación


ms-catalogo → ms-inventario

<p>El servicio consulta el inventario para obtener los datos de la prenda y complementar su información comercial.<br>
El catálogo público puede consultarse sin autenticación. Las operaciones de administración requieren permisos.</p>
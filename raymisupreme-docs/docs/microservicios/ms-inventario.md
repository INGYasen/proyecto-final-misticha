

# Microservicio de Inventario

`ms-inventario` recibe la referencia del lote registrado en acopio y administra las prendas disponibles.

## Responsabilidades

- Crear categorías.
- Registrar prendas.
- Asignar códigos únicos.
- Almacenar talla, color y material.
- Controlar el stock.
- Registrar inspecciones de calidad.
- Relacionar la prenda con su lote.

## Recursos principales

| Recurso | Ruta |
|---|---|
| Categorías | `/api/v1/categorias` |
| Prendas | `/api/v1/prendas` |
| Inspecciones | `/api/v1/inspecciones` |
| Stock | `/api/v1/prendas/{id}/stock` |

## Comunicación


ms-inventario → ms-acopio
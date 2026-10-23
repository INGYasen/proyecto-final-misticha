

# Microservicio de Acopio

`ms-acopio` representa el inicio del proceso productivo. Se encarga de registrar a los artesanos y los lotes de productos recibidos.

## Información administrada

### Artesanos

- Nombres y apellidos.
- DNI.
- Comunidad o zona.
- Teléfono.

### Lotes

- Artesano proveedor.
- Fecha de compra.
- Cantidad recibida.
- Precio total pagado.
- Descripción.
- Estado del lote.

## Rutas principales

| Recurso | Ruta |
|---|---|
| Artesanos | `/api/v1/artesanos` |
| Lotes | `/api/v1/lotes` |

## Relación con otros servicios


ms-acopio ← consulta ← ms-inventario
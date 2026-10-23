# API - ms-acopio

## Descripción general

El microservicio `ms-acopio` administra la información relacionada con los artesanos y los lotes de compra o recepción de prendas.

Este servicio representa la base inicial de la cadena de valor de Raymi Suprême, porque permite conocer el origen de las prendas y mantener trazabilidad desde el artesano hasta el inventario.

---

## Responsabilidades principales

- Registrar artesanos.
- Consultar artesanos.
- Registrar lotes de compra.
- Consultar lotes.
- Relacionar cada lote con un artesano.
- Proveer información de lotes a `ms-inventario`.

---

## Endpoints de artesanos

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear artesano | `POST` | `http://localhost:7091/api/v1/artesanos` | Sí |
| Listar artesanos | `GET` | `http://localhost:7091/api/v1/artesanos` | No |
| Buscar artesano por ID | `GET` | `http://localhost:7091/api/v1/artesanos/1` | No |
| Eliminar artesano | `DELETE` | `http://localhost:7091/api/v1/artesanos/1` | No |

---

## Crear artesano

```http
POST http://localhost:7091/api/v1/artesanos
```

### Body

```json
{
  "nombres": "Rosa",
  "apellidos": "Quispe",
  "dni": "12345678",
  "comunidadOZona": "Juliaca",
  "telefono": "987654321"
}
```

### Resultado esperado

```text
Artesano creado correctamente.
```

---

## Endpoints de lotes

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear lote | `POST` | `http://localhost:7091/api/v1/lotes` | Sí |
| Listar lotes | `GET` | `http://localhost:7091/api/v1/lotes` | No |
| Buscar lote por ID | `GET` | `http://localhost:7091/api/v1/lotes/1` | No |

---

## Crear lote de compra

```http
POST http://localhost:7091/api/v1/lotes
```

### Body

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

### Resultado esperado

```text
Lote creado correctamente y asociado al artesano ID 1.
```

---

## Relación con otros microservicios

`ms-acopio` actúa como proveedor de información para `ms-inventario`.

```text
ms-inventario → consulta lote → ms-acopio
```

---

## Feign y Circuit Breaker

En este flujo, `ms-acopio` no actúa como cliente Feign.  
Su responsabilidad es proveer información a otros microservicios.

| Comunicación | Aplica |
|---|---|
| Feign saliente | No |
| Circuit Breaker saliente | No |
| Es consumido por otro servicio | Sí, por `ms-inventario` |

---

## Verificación recomendada

Después de crear un artesano y un lote, se recomienda validar:

```http
GET http://localhost:7091/api/v1/artesanos
```

```http
GET http://localhost:7091/api/v1/lotes
```

Si ambos registros aparecen correctamente, `ms-acopio` está listo para ser utilizado por `ms-inventario`.
# API - ms-inventario

## Descripción general

El microservicio `ms-inventario` administra la información interna de las prendas disponibles en Raymi Suprême.

Este servicio controla categorías, prendas, stock e inspecciones de calidad. Además, puede consultar información de lotes desde `ms-acopio`.

---

## Responsabilidades principales

- Crear categorías.
- Registrar prendas.
- Consultar prendas.
- Controlar stock.
- Registrar inspecciones de calidad.
- Relacionar prendas con lotes.
- Consultar información de lotes mediante `ms-acopio`.

---

## Endpoints de categorías

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear categoría | `POST` | `http://localhost:7091/api/v1/categorias` | Sí |
| Listar categorías | `GET` | `http://localhost:7091/api/v1/categorias` | No |
| Buscar categoría | `GET` | `http://localhost:7091/api/v1/categorias/1` | No |

---

## Crear categoría

```http
POST http://localhost:7091/api/v1/categorias
```

### Body

```json
{
  "nombre": "Chompas",
  "descripcion": "Prendas tejidas de alpaca"
}
```

---

## Endpoints de prendas

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear prenda | `POST` | `http://localhost:7091/api/v1/prendas` | Sí |
| Listar prendas | `GET` | `http://localhost:7091/api/v1/prendas` | No |
| Buscar prenda | `GET` | `http://localhost:7091/api/v1/prendas/1` | No |
| Consultar stock | `GET` | `http://localhost:7091/api/v1/prendas/1/stock` | No |
| Consultar prendas por lote | `GET` | `http://localhost:7091/api/v1/prendas/lote/1` | No |
| Consultar detalle completo | `GET` | `http://localhost:7091/api/v1/prendas/1/detalle-completo` | No |

---

## Crear prenda

```http
POST http://localhost:7091/api/v1/prendas
```

### Body

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

### Resultado esperado

```text
Prenda creada con stock inicial de 10 unidades.
```

---

## Endpoints de inspección de calidad

| Acción | Método | Endpoint | Body |
|---|---:|---|---|
| Crear inspección | `POST` | `http://localhost:7091/api/v1/inspecciones` | Sí |
| Listar inspecciones | `GET` | `http://localhost:7091/api/v1/inspecciones` | No |
| Buscar inspección | `GET` | `http://localhost:7091/api/v1/inspecciones/1` | No |
| Inspecciones por prenda | `GET` | `http://localhost:7091/api/v1/inspecciones/prenda/1` | No |

---

## Crear inspección de calidad

```http
POST http://localhost:7091/api/v1/inspecciones
```

### Body

```json
{
  "fechaInspeccion": "2026-06-17",
  "resultado": "APROBADO",
  "observaciones": "Prenda en buen estado",
  "idPrenda": 1
}
```

---

## Comunicación con ms-acopio

`ms-inventario` consulta a `ms-acopio` para obtener información del lote relacionado con una prenda.

```text
ms-inventario → OpenFeign + Circuit Breaker → ms-acopio
```

---

## Endpoint para probar Feign + Circuit Breaker

```http
GET http://localhost:7091/api/v1/prendas/1/detalle-completo
```

### Resultado esperado

El sistema debe retornar información de la prenda junto con los datos del lote consultados desde `ms-acopio`.

---

## Validaciones recomendadas

| Validación | Endpoint |
|---|---|
| Ver prendas creadas | `GET http://localhost:7091/api/v1/prendas` |
| Ver stock | `GET http://localhost:7091/api/v1/prendas/1/stock` |
| Ver detalle completo | `GET http://localhost:7091/api/v1/prendas/1/detalle-completo` |
| Ver inspecciones | `GET http://localhost:7091/api/v1/inspecciones/prenda/1` |
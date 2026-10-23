# Comunicación entre Microservicios

Los servicios se comunican principalmente mediante APIs REST y clientes **OpenFeign**.

## Comunicaciones principales

| Origen | Destino | Propósito |
|---|---|---|
| `ms-inventario` | `ms-acopio` | Consultar el lote de una prenda |
| `ms-catalogo` | `ms-inventario` | Obtener datos y stock |
| `ms-carrito` | `ms-catalogo` | Validar producto y precio |
| `ms-ventas` | `ms-carrito` | Obtener los ítems del carrito |
| `ms-ventas` | `ms-inventario` | Verificar y reducir stock |
| `ms-ventas` | `ms-pagos` | Procesar el pago |

## Descubrimiento

Los microservicios se registran en **Eureka Server**. De esta manera pueden encontrarse utilizando el nombre del servicio, sin depender de una dirección fija.

## Tolerancia a fallos

Las llamadas entre servicios utilizan **Circuit Breaker**. Si un servicio no está disponible, se controla el error y se evita una cadena de fallos en toda la plataforma.
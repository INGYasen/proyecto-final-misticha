# Raymi Suprême — docs

Equipo 12, DIST 2026-2. Tienda de prendas andinas (alpaca y taller). El front es Angular. Al backend se entra solo por el API Gateway.

Acá está la arquitectura, los microservicios, cómo se levanta en local y cómo se prueba.

## Stack

| Tecnología | Uso |
|---|---|
| Spring Boot 3.5 | Microservicios |
| Spring Cloud Gateway | Entrada única |
| Eureka | Descubrimiento |
| OpenFeign | Llamadas entre servicios |
| Resilience4j | Circuit breaker |
| Keycloak 25 | Login y roles |
| MySQL 8 | Una base por servicio |
| Kafka | Topic `pedidos` |
| Angular 17 | Tienda |
| Mercado Pago | Cobro |
| Prometheus / Grafana / Loki | Métricas, dashboards y logs |

## Carpetas del repo

- `fronted/` — la tienda (el nombre de la carpeta quedó así)
- `raymisupreme-back/` — infra, servicios y observabilidad
- `raymisupreme-docs/` — esta documentación

El flujo de compra: catálogo → carrito (también como invitado) → checkout → pedido `PENDIENTE` → Mercado Pago → `PAGADO`. El stock no baja hasta que el pago queda aprobado.

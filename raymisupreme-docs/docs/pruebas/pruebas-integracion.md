# Integración

- Gateway → cada `ms-*` con JWT
- `ms-carrito` pregunta precio a `ms-catalogo`
- `ms-ventas` publica en Kafka al crear el pedido
- `ms-pagos` → Mercado Pago → `ms-ventas` confirma
- `ms-inventario` descuenta cuando el pago está aprobado
- Eureka balancea las dos copias de `ms-catalogo`

# Resumen ejecutivo

Raymi Suprême es la tienda del equipo 12: prendas andinas (alpaca, taller de Puno/Cusco) vendidas por microservicios. El lote entra por acopio, pasa a inventario, se publica en catálogo y se cobra con Mercado Pago.

## Qué cubre

Acopio (artesano y lote), inventario y calidad, catálogo con precios, carrito, pedidos, cobro, login con roles y Grafana.

## Solución

Cada área es un microservicio:

| Componente | Responsabilidad |
|---|---|
| `ms-auth` | Administración de usuarios y roles mediante Keycloak |
| `ms-acopio` | Registro de artesanos y lotes adquiridos |
| `ms-inventario` | Gestión de prendas, categorías, stock e inspecciones de calidad |
| `ms-catalogo` | Publicación de productos, precios y descuentos |
| `ms-carrito` | Administración del carrito de compra |
| `ms-ventas` | Gestión de clientes, pedidos y proceso de checkout |
| `ms-pagos` | Mercado Pago y comprobantes |
| API Gateway | Punto único de acceso a los servicios |
| Eureka Server | Registro y descubrimiento de microservicios |
| Config Server | Configuración centralizada |
| Keycloak | Autenticación y autorización |
| Prometheus, Loki y Grafana | Métricas, logs y visualización operativa |

Cada servicio tiene su MySQL. Entre ellos se hablan por Feign. Resilience4j cubre el circuit breaker.

## Flujo

1. Se registra al artesano.
2. Se registra el lote recibido o adquirido.
3. Las prendas del lote ingresan al inventario.
4. Se realiza el control de calidad.
5. Las prendas aprobadas se publican en el catálogo.
6. Se asignan precios y descuentos.
7. El cliente agrega productos a su carrito.
8. El carrito se convierte en un pedido `PENDIENTE`.
9. El cliente paga con Mercado Pago (Checkout Pro o Brick).
10. El webhook confirma el cobro y el pedido pasa a `PAGADO`.
11. Recién ahí se descuenta el stock.
12. El cliente ve su pedido en la tienda. El admin puede seguir el estado (confirmado, enviado, entregado).

## Seguridad

Keycloak, OAuth 2.0 y JWT. Roles: `CLIENTE`, `VENDEDORA`, `SUPER_ADMIN`. El catálogo se ve sin cuenta. Comprar y el admin piden token.

## Estado actual

Hay tienda Angular, microservicios, Keycloak y cobro con Mercado Pago. El admin ve pedidos, catálogo e inventario. Grafana tiene el dashboard de instancias.

Stripe no es el cobro principal. El envío internacional no está modelado: el despacho es local, desde Puno.
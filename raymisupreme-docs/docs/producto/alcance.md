

# Alcance del Sistema

## Alcance general

RaymiSupreme cubre la gestión backend del proceso comercial de prendas artesanales, desde el registro del artesano y el lote hasta la creación del pedido, el pago y la actualización del inventario.

El alcance incluye APIs REST, seguridad, persistencia, comunicación entre microservicios, tolerancia a fallos y observabilidad.

## Funcionalidades incluidas

### Usuarios y seguridad

- Integración con Keycloak.
- Autenticación mediante OAuth 2.0 y JWT.
- Creación y administración de usuarios.
- Asignación de roles.
- Protección de endpoints.
- Separación de permisos entre cliente, vendedora y administrador.

### Acopio

- Registro de artesanos.
- Consulta y eliminación de artesanos.
- Registro de datos de contacto y procedencia.
- Registro de lotes.
- Asociación entre artesano y lote.
- Consulta del estado y contenido general del lote.

### Inventario y calidad

- Registro de categorías.
- Registro de prendas.
- Generación y uso de códigos únicos.
- Asociación entre prenda, categoría y lote.
- Gestión de talla, color y material.
- Control de stock.
- Registro de inspecciones de calidad.
- Consulta de prendas por lote.
- Consulta del detalle completo de una prenda.

### Catálogo

- Publicación de prendas.
- Registro de fotografías mediante URL.
- Creación de descripciones comerciales.
- Activación y desactivación de productos.
- Gestión de productos destacados.
- Asignación de precios.
- Aplicación de descuentos.
- Consulta pública del catálogo.
- Filtrado por material, color y talla.

### Clientes y carrito

- Registro de clientes.
- Actualización de información del cliente.
- Creación o recuperación del carrito activo.
- Incorporación de productos al carrito.
- Validación del producto contra el catálogo.
- Actualización de cantidades.
- Eliminación de productos.
- Vaciado del carrito.

### Pedidos y ventas

- Creación de pedidos desde el carrito.
- Registro del detalle del pedido.
- Consulta de pedidos por cliente.
- Consulta del historial de estados.
- Confirmación de pedidos.
- Cancelación de pedidos.
- Coordinación con pagos e inventario.

### Pagos

- Registro de pagos.
- Asociación del pago con un pedido y un cliente.
- Gestión del estado del pago.
- Consulta de pagos por pedido.
- Registro de referencias de pago.
- Generación y consulta de comprobantes.

### Operación técnica

- Registro y descubrimiento de servicios.
- Configuración centralizada.
- Enrutamiento mediante API Gateway.
- Bases de datos separadas por microservicio.
- Migraciones de base de datos con Flyway.
- Comunicación mediante OpenFeign.
- Tolerancia a fallos con Circuit Breaker.
- Health checks.
- Métricas con Prometheus.
- Centralización de logs con Loki.
- Visualización mediante Grafana.
- Ejecución de infraestructura con Docker Compose.

## Límites del alcance actual

La versión actual se concentra en la implementación del backend y sus servicios distribuidos.

No forma parte del alcance implementado:

- Una aplicación web completa para clientes.
- Una aplicación móvil.
- Integración con una pasarela bancaria real.
- Cobro real de tarjetas.
- Conversión automática de monedas.
- Cálculo de impuestos internacionales.
- Gestión completa de envíos y transportistas.
- Seguimiento GPS de pedidos.
- Integración con aduanas.
- Gestión de devoluciones y reembolsos.
- Liquidaciones automáticas a artesanos.
- Marketing digital automatizado.
- Recomendaciones de productos mediante inteligencia artificial.
- Administración de contenido multimedia propio; actualmente se utilizan URLs.
- Notificaciones reales por correo, SMS o mensajería.

## Alcance geográfico

El sistema está orientado inicialmente al registro de artesanos y productos textiles peruanos, con especial referencia a productores de Puno y Juliaca.

Su diseño busca permitir que, en el futuro, la operación pueda ampliarse a:

- Otras regiones del Perú.
- Clientes nacionales.
- Clientes internacionales.
- Nuevos tipos de productos artesanales.

## Alcance técnico

La plataforma ofrece una arquitectura preparada para crecimiento horizontal y evolución independiente de los servicios. Sin embargo, la implementación actual se ejecuta principalmente como un entorno académico y demostrativo en infraestructura local mediante Maven y Docker Compose.

## Criterio de finalización del flujo principal

El flujo principal se considera completado cuando:

1. Existe un artesano registrado.
2. Se registra un lote asociado al artesano.
3. Se registra una prenda asociada al lote.
4. La prenda cuenta con control de calidad.
5. Se publica en el catálogo con precio.
6. Un cliente agrega el producto a su carrito.
7. Se genera un pedido pendiente.
8. El pedido se confirma.
9. Se registra el pago.
10. El pedido cambia a pagado.
11. El stock se reduce correctamente.
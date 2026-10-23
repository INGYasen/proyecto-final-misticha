

# Propuesta de Solución

## Descripción

Se propone desarrollar **RaymiSupreme**, una plataforma distribuida que centralice la gestión de artesanos, lotes, prendas, calidad, inventario, catálogo, ventas y pagos.

La solución permite seguir el recorrido de una prenda desde su ingreso al sistema hasta su compra:


Artesano
   ↓
Lote de compra
   ↓
Prenda e inspección de calidad
   ↓
Inventario
   ↓
Catálogo, precio y descuento
   ↓
Carrito
   ↓
Pedido
   ↓
Pago y comprobante
## Enfoque de la solución

Raymi Suprême plantea una solución basada en una arquitectura de microservicios. La plataforma se divide en servicios independientes, donde cada microservicio cumple una responsabilidad específica dentro del proceso de negocio.

Esta separación evita concentrar toda la lógica del sistema en una sola aplicación y permite que cada servicio pueda desarrollarse, mantenerse, probarse y escalarse de forma independiente.

El sistema está orientado a organizar la cadena de valor de las prendas artesanales peruanas, desde el registro del artesano y el lote de producción, hasta la publicación del producto, la compra, la venta y el pago.

---

## Gestión de identidad

El microservicio `ms-auth` se integra con Keycloak para administrar la identidad y seguridad de los usuarios del sistema.

Este módulo permite:

- Crear usuarios.
- Actualizar usuarios.
- Consultar usuarios registrados.
- Asignar roles.
- Gestionar autenticación mediante tokens JWT.
- Controlar el acceso a funcionalidades protegidas.

La integración con Keycloak permite separar la lógica de autenticación del resto de microservicios, mejorando la seguridad y facilitando la administración de usuarios y roles.

---

## Gestión de acopio

El microservicio `ms-acopio` permite administrar la información relacionada con los artesanos y los lotes de producción recibidos por la empresa.

Este módulo permite:

- Registrar artesanos.
- Almacenar datos de contacto.
- Registrar la procedencia del artesano.
- Registrar lotes comprados o recibidos.
- Relacionar cada lote con su artesano.
- Consultar el estado de un lote.
- Consultar las cantidades recibidas por lote.

Este servicio es importante porque permite mantener la trazabilidad del origen de las prendas y conocer qué artesano o comunidad participó en la producción.

---

## Gestión de inventario

El microservicio `ms-inventario` administra las prendas físicas disponibles dentro del negocio. Su función principal es controlar la información técnica y operativa de cada prenda.

Este módulo permite:

- Crear categorías.
- Registrar prendas.
- Asignar códigos únicos.
- Registrar talla, color y material.
- Controlar el stock disponible.
- Relacionar prendas con lotes de origen.
- Registrar inspecciones de calidad.
- Consultar información del lote mediante `ms-acopio`.

La relación entre inventario y acopio permite conservar la trazabilidad de cada prenda, desde su recepción hasta su disponibilidad para venta.

---

## Gestión de catálogo

El microservicio `ms-catalogo` se encarga de administrar la información comercial visible para el cliente final.

Este módulo permite:

- Publicar prendas disponibles para la venta.
- Incorporar fotografías del producto.
- Registrar descripciones públicas.
- Activar o desactivar publicaciones.
- Marcar productos como destacados.
- Definir precios.
- Aplicar descuentos.
- Consultar productos por material, talla o color.
- Obtener información de prendas desde `ms-inventario`.

Este servicio separa la información comercial del producto de la información interna del inventario. De esta forma, el catálogo muestra al cliente solo los datos necesarios para la compra.

---

## Gestión del carrito

El microservicio `ms-carrito` permite administrar los productos que un cliente desea comprar antes de confirmar una venta.

Este módulo permite:

- Crear o recuperar el carrito activo de un cliente.
- Agregar productos al carrito.
- Actualizar cantidades.
- Eliminar productos.
- Vaciar el carrito.
- Validar los datos del producto contra el catálogo.

El servicio no confía únicamente en el nombre o precio enviado por el usuario. Para evitar inconsistencias, consulta a `ms-catalogo` y utiliza la información vigente del producto.

Esto permite que el carrito trabaje con precios, nombres y datos actualizados.

---

## Gestión de ventas

El microservicio `ms-ventas` administra el proceso de generación de pedidos y ventas dentro del sistema.

Este módulo permite:

- Registrar clientes.
- Crear pedidos desde el carrito.
- Almacenar el detalle de cada pedido.
- Consultar el historial de pedidos.
- Confirmar pedidos.
- Cancelar pedidos.
- Coordinar el pago y la actualización del inventario.

El proceso de checkout crea inicialmente un pedido con estado pendiente. Posteriormente, un usuario autorizado puede confirmarlo o cancelarlo según el flujo comercial definido.

Este enfoque evita confirmar ventas sin validación y permite mantener control sobre el estado de cada pedido.

---

## Gestión de pagos

El microservicio `ms-pagos` administra la información relacionada con las transacciones realizadas por los clientes.

Este módulo permite:

- Registrar el pago asociado a un pedido.
- Guardar el método de pago.
- Guardar la referencia de la transacción.
- Actualizar el estado del pago.
- Consultar pagos por pedido.
- Generar comprobantes.
- Consultar comprobantes.

En el flujo normal del sistema, el pago se origina cuando se confirma un pedido desde `ms-ventas`. De esta manera, cada pago queda relacionado con una venta específica.

---

## Infraestructura de soporte

La solución incorpora componentes de infraestructura que permiten organizar, proteger, ejecutar y monitorear los microservicios.

| Componente | Función dentro del sistema |
|---|---|
| API Gateway | Centraliza el acceso a las APIs del sistema. |
| Eureka Server | Permite descubrir las instancias disponibles de los microservicios. |
| Config Server | Centraliza la configuración de los servicios. |
| Keycloak | Gestiona identidad, autenticación y roles. |
| MySQL | Mantiene la persistencia de datos de los servicios. |
| OpenFeign | Facilita la comunicación entre microservicios. |
| Circuit Breaker | Controla fallos en dependencias externas. |
| Flyway | Versiona la estructura de las bases de datos. |
| Docker Compose | Facilita el despliegue de la infraestructura. |
| Prometheus | Recolecta métricas técnicas del sistema. |
| Promtail y Loki | Recolectan y almacenan logs del sistema. |
| Grafana | Visualiza métricas y logs mediante dashboards. |

---

## Beneficios esperados

La solución propuesta ofrece beneficios técnicos y comerciales para la operación de Raymi Suprême.

Entre los principales beneficios se encuentran:

- Trazabilidad del origen de las prendas.
- Control de calidad documentado.
- Inventario centralizado y actualizado.
- Publicación organizada de productos.
- Mayor consistencia entre catálogo, carrito y venta.
- Separación clara de responsabilidades.
- Protección de endpoints según roles.
- Capacidad de monitorear el estado de los servicios.
- Base tecnológica escalable para futuras funcionalidades.
- Mejor organización de la cadena de valor artesanal.

---

## Resultado esperado

Raymi Suprême proporciona una base backend para que la marca Raymi Suprême pueda organizar su operación y avanzar hacia la comercialización digital de prendas artesanales peruanas en mercados nacionales e internacionales.

La plataforma permite integrar procesos que normalmente se encuentran separados, como acopio, inventario, catálogo, carrito, ventas, pagos y seguridad. Como resultado, el negocio obtiene una solución más ordenada, trazable, segura y preparada para crecer.
# Resumen Ejecutivo

## Descripción general

**RaymiSupreme** es una plataforma distribuida orientada a la gestión y comercialización de prendas textiles artesanales peruanas bajo la marca **Raymi Suprême**.

El proyecto busca atender una problemática frecuente del sector artesanal: la producción se encuentra dispersa, muchos artesanos trabajan de manera independiente y existen dificultades para controlar el origen, la calidad, el inventario y la comercialización de sus productos.

La plataforma centraliza el proceso desde la recepción de prendas o lotes provenientes de artesanos hasta la publicación de los productos en un catálogo y su posterior venta.

> **RaymiSupreme conecta la producción artesanal peruana con un modelo de comercialización digital, organizado y escalable.**

## Propósito del proyecto

El propósito de RaymiSupreme es proporcionar una base tecnológica que permita:

- Registrar artesanos y lotes de productos adquiridos.
- Mantener la trazabilidad entre el artesano, el lote y la prenda.
- Clasificar las prendas por categoría, talla, color y material.
- Registrar inspecciones de calidad.
- Controlar el inventario y el stock disponible.
- Publicar productos en un catálogo digital.
- Administrar precios, descuentos y productos destacados.
- Gestionar clientes y carritos de compra.
- Crear, confirmar o cancelar pedidos.
- Registrar pagos y generar comprobantes.
- Proteger las operaciones mediante usuarios, roles y permisos.
- Supervisar el funcionamiento de la plataforma mediante métricas y logs.

## Solución implementada

RaymiSupreme está construido con una arquitectura de microservicios. Cada área del negocio se encuentra separada en un servicio especializado:

| Componente | Responsabilidad |
|---|---|
| `ms-auth` | Administración de usuarios y roles mediante Keycloak |
| `ms-acopio` | Registro de artesanos y lotes adquiridos |
| `ms-inventario` | Gestión de prendas, categorías, stock e inspecciones de calidad |
| `ms-catalogo` | Publicación de productos, precios y descuentos |
| `ms-carrito` | Administración del carrito de compra |
| `ms-ventas` | Gestión de clientes, pedidos y proceso de checkout |
| `ms-pagos` | Registro de pagos y comprobantes |
| API Gateway | Punto único de acceso a los servicios |
| Eureka Server | Registro y descubrimiento de microservicios |
| Config Server | Configuración centralizada |
| Keycloak | Autenticación y autorización |
| Prometheus, Loki y Grafana | Métricas, logs y visualización operativa |

Cada microservicio mantiene su propia responsabilidad y persistencia. La comunicación interna se realiza mediante APIs REST y clientes Feign, incorporando mecanismos de tolerancia a fallos.

## Flujo principal del negocio

El flujo funcional implementado es el siguiente:

1. Se registra al artesano.
2. Se registra el lote recibido o adquirido.
3. Las prendas del lote ingresan al inventario.
4. Se realiza el control de calidad.
5. Las prendas aprobadas se publican en el catálogo.
6. Se asignan precios y descuentos.
7. El cliente agrega productos a su carrito.
8. El carrito se convierte en un pedido pendiente.
9. Un usuario autorizado confirma o cancela el pedido.
10. Al confirmar el pedido, se registra el pago.
11. El stock del inventario se actualiza.
12. El cliente puede consultar la información de su compra.

## Seguridad

La seguridad de la plataforma se gestiona con **Keycloak**, **OAuth 2.0** y tokens **JWT**.

Los principales roles definidos son:

- `CLIENTE`
- `VENDEDORA`
- `SUPER_ADMIN`

El catálogo puede consultarse públicamente, mientras que las operaciones administrativas y de compra requieren autenticación. Los permisos más sensibles se reservan para el rol `SUPER_ADMIN`.

## Valor aportado

RaymiSupreme aporta valor al permitir:

- Organizar información que normalmente se encuentra dispersa.
- Conservar el origen de las prendas mediante su relación con lotes y artesanos.
- Mejorar el control de calidad y stock.
- Facilitar la publicación digital de productos artesanales.
- Centralizar el proceso comercial.
- Separar las responsabilidades mediante microservicios.
- Preparar la plataforma para su crecimiento y evolución futura.

## Estado actual

La implementación actual cubre principalmente el **backend de la plataforma**, sus APIs, persistencia, seguridad, comunicación entre microservicios y observabilidad.

La tienda web pública, la integración con una pasarela bancaria real y la logística internacional completa se consideran extensiones futuras del producto.
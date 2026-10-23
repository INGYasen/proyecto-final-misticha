# Actores del Sistema

## Descripción general

Los actores de RaymiSupreme se dividen en dos grupos:

1. **Actores de negocio**, que participan en la operación comercial.
2. **Actores técnicos**, que interactúan automáticamente con la plataforma.

Es importante diferenciar un actor de negocio de un rol de seguridad. Por ejemplo, el artesano participa en el negocio como proveedor, pero en la versión actual no necesita una cuenta para ingresar al sistema.

## Actores de negocio

### Visitante

Es una persona que todavía no se ha autenticado.

Puede:

- Consultar el catálogo público.
- Visualizar productos disponibles.
- Consultar productos por material, color o talla.
- Revisar información comercial pública.

No puede:

- Administrar un carrito.
- Crear pedidos.
- Consultar información interna.
- Modificar inventario, catálogo o ventas.

### Cliente

Es el comprador de los productos publicados en RaymiSupreme.

Sus principales acciones son:

- Autenticarse en la plataforma.
- Registrar sus datos como cliente.
- Consultar el catálogo.
- Agregar productos al carrito.
- Modificar cantidades.
- Eliminar productos.
- Vaciar el carrito.
- Realizar el checkout.
- Consultar información relacionada con sus pedidos.

El cliente participa principalmente en las etapas de catálogo, carrito y compra.

### Artesano

Es el proveedor de las prendas o lotes adquiridos por Raymi Suprême.

La información registrada incluye:

- Nombres y apellidos.
- DNI.
- Comunidad o zona.
- Teléfono.
- Lotes entregados o vendidos.

En la versión actual, el artesano:

- Es un actor del negocio.
- Es registrado por personal autorizado.
- No necesita autenticarse.
- No administra directamente sus productos.
- No utiliza un panel propio.

### Vendedora

Corresponde al rol técnico `VENDEDORA`.

Representa al personal encargado de consultar y apoyar la operación comercial.

Puede realizar principalmente:

- Consulta de artesanos y lotes.
- Consulta de categorías, prendas y stock.
- Consulta de inspecciones de calidad.
- Consulta de precios y productos del catálogo.
- Consulta de clientes.
- Consulta de pedidos y detalles.
- Seguimiento de la operación de ventas.

Algunas operaciones de modificación o administración completa permanecen restringidas al `SUPER_ADMIN`.

### Superadministrador

Corresponde al rol `SUPER_ADMIN`.

Es el actor con mayor nivel de acceso y control sobre la plataforma.

Puede:

- Crear, consultar y administrar usuarios.
- Asignar roles.
- Registrar y eliminar artesanos.
- Registrar lotes.
- Crear categorías y prendas.
- Registrar inspecciones de calidad.
- Administrar publicaciones del catálogo.
- Crear, actualizar o eliminar precios y descuentos.
- Consultar y administrar clientes.
- Confirmar o cancelar pedidos.
- Consultar y administrar pagos.
- Consultar comprobantes.
- Acceder a operaciones técnicas restringidas.

El superadministrador es responsable de mantener la consistencia y seguridad de la operación.

## Matriz resumida de participación

| Funcionalidad | Visitante | Cliente | Vendedora | Superadministrador | Artesano |
|---|:---:|:---:|:---:|:---:|:---:|
| Consultar catálogo público | Sí | Sí | Sí | Sí | Indirecto |
| Gestionar carrito | No | Sí | Según permisos | Sí | No |
| Realizar checkout | No | Sí | Según permisos | Sí | No |
| Consultar operación interna | No | Limitado | Sí | Sí | No |
| Registrar artesanos | No | No | No | Sí | No |
| Consultar artesanos | No | No | Sí | Sí | No |
| Registrar prendas | No | No | No | Sí | No |
| Consultar inventario | No | Limitado | Sí | Sí | No |
| Administrar catálogo | No | No | Limitado | Sí | No |
| Confirmar o cancelar pedidos | No | No | No | Sí | No |
| Administrar pagos | No | No | No | Sí | No |
| Administrar usuarios y roles | No | No | No | Sí | No |

> Los permisos definitivos se aplican en el API Gateway y en los controladores de cada microservicio.

## Actores técnicos

### Keycloak

Sistema externo encargado de:

- Autenticar usuarios.
- Emitir tokens JWT.
- Mantener roles.
- Proporcionar la identidad utilizada por los servicios.

### API Gateway

Actúa como punto único de entrada.

Sus responsabilidades incluyen:

- Recibir solicitudes.
- Validar autenticación y permisos.
- Enrutar las solicitudes.
- Evitar el acceso directo innecesario a los microservicios.

### Eureka Server

Permite que los servicios registren sus instancias y que otros componentes puedan localizarlos dinámicamente.

### Config Server

Entrega configuraciones centralizadas para los diferentes servicios y ambientes.

### Microservicios internos

Los microservicios también actúan como consumidores entre sí:

| Servicio consumidor | Servicio consultado | Finalidad |
|---|---|---|
| `ms-inventario` | `ms-acopio` | Validar y consultar información del lote |
| `ms-catalogo` | `ms-inventario` | Obtener datos de las prendas |
| `ms-carrito` | `ms-catalogo` | Validar producto, nombre y precio |
| `ms-ventas` | `ms-carrito` | Obtener el contenido del carrito |
| `ms-ventas` | `ms-inventario` | Validar y actualizar el stock |
| `ms-ventas` | `ms-pagos` | Procesar el pago del pedido |

### Sistemas de observabilidad

Prometheus, Promtail, Loki y Grafana participan como actores técnicos encargados de:

- Recolectar métricas.
- Capturar logs.
- Consultar el estado de los servicios.
- Mostrar información operativa.
- Facilitar la detección de errores.

## Relación general entre actores


Visitante o cliente
        ↓
    API Gateway
        ↓
      Keycloak
        ↓
Microservicios de RaymiSupreme
        ↓
Bases de datos independientes
        ↓
Prometheus, Loki y Grafana
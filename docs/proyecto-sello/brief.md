# Brief Técnico del Proyecto Sello

Este documento es el hito de S2: el equipo declara qué sistema va a construir durante el ciclo.

## 1. Datos del equipo

- **Nombre del equipo:** DIST
- **Sección:** 5to ciclo — Aplicaciones Distribuidas 2026-2
- **Repositorio:** https://github.com/INGYasen/proyecto-final-misticha
- **Topics del repositorio configurados:** no (pendiente: `grupo-dist`)

| Integrante | Rol o énfasis previsto |
|------------|------------------------|
| Yasen Cutipa Mayhua | Catálogo de prendas, órdenes de compra |
| Russman Keny Torres Lopez | Pagos en línea, autenticación |

## 2. Dominio del proyecto

- **Nombre del proyecto:** ChaskaWear
- **Problema o necesidad:** Los artesanos y tiendas de ropa tradicional del Cusco venden en feria o por WhatsApp. No hay un catálogo único, no se arma un pedido formal y el cobro queda suelto. ChaskaWear concentra catálogo, orden y pago en línea para que el cliente compre sin ir al puesto.
- **Dominio de negocio:** Comercio electrónico de moda artesanal cuzqueña (ponchos, chullos, polleras, mantas, accesorios de lana y alpaca). Flujo: catálogo de prendas → orden de compra → pago. Más adelante se puede ampliar a joyería y cerámica del mismo origen.
- **Usuarios / actores:** Cliente (navega y compra), Administrador/artesano (publica prendas y revisa pedidos), Pasarela de pagos (cobro real).
- **Servicio externo real:** Mercado Pago (sandbox). Si no encaja, Culqi.
- **¿Continúa un proyecto de un ciclo anterior?** No. Dominio nuevo para este curso.

## 3. Microservicios previstos y alcance esperado

| Integrante | Microservicio transaccional | Microservicio no transaccional |
|------------|-----------------------------|--------------------------------|
| Yasen Cutipa Mayhua | orden-ms (Orden + OrdenDetalle) | catalogo-ms (Categoria + Producto) |
| Russman Keny Torres Lopez | pago-ms (Pago + Transaccion) | auth-ms (Usuario + Rol) |

### Microservicio: catalogo-ms (integrante: Yasen Cutipa Mayhua · tipo: no transaccional)

- **Descripción breve:** Publica las prendas artesanales por categoría. Es el punto de entrada del flujo: sin catálogo no hay qué comprar. Otros servicios solo guardan el `id_producto`.
- **Entidad principal:** `Categoria` / `Producto` (prenda).
- **Datos iniciales previstos:**
  - `Categoria`: nombre, descripcion
  - `Producto`: nombre, precio, material, talla, origen (Cusco), id_categoria, activo
- **Endpoints iniciales previstos:** `GET /api/categorias`, `POST /api/categorias`, `GET /api/productos`, `POST /api/productos`
- **¿Se comunica con otro microservicio?** Sí. Lo consultan `orden-ms` y `pago-ms` por REST/Feign (S6).
- **Rutas protegidas:** lectura pública. Alta y edición solo ADMIN (S7).
- **Requisitos:**
  1. El sistema debe listar prendas activas con precio y categoría.
  2. El sistema debe permitir al admin crear y editar categorías de ropa artesanal.
  3. El sistema debe exigir que cada prenda pertenezca a una sola categoría.

### Microservicio: orden-ms (integrante: Yasen Cutipa Mayhua · tipo: transaccional)

- **Descripción breve:** Arma el pedido del cliente: cabecera de orden y líneas de prendas. Recalcula el total al agregar o quitar detalle.
- **Cabecera-detalle:** `Orden` / `OrdenDetalle`.
- **Datos iniciales previstos:**
  - `Orden`: codigo, fecha, estado, total
  - `OrdenDetalle`: id_producto, cantidad, precio_unitario, subtotal
- **Endpoints iniciales previstos:** `GET /api/ordenes`, `POST /api/ordenes`, `GET /api/orden-detalles`, `POST /api/orden-detalles`
- **¿Se comunica con otro microservicio?** Sí. Valida la prenda en `catalogo-ms` (REST/Feign, S6). Publica evento a `pago-ms` (S8).
- **Rutas protegidas:** crear orden requiere cliente autenticado (S7). Consulta de una orden propia: CLIENTE. Listado global: ADMIN.
- **Requisitos:**
  1. El sistema debe permitir crear una orden con varias prendas.
  2. El sistema debe actualizar el total al cambiar cantidades o líneas.
  3. El sistema no debe aceptar un código de orden duplicado.

### Microservicio: pago-ms (integrante: Russman Keny Torres Lopez · tipo: transaccional)

- **Descripción breve:** Cobra la orden contra Mercado Pago. Confirma o rechaza el pago según la respuesta real de la pasarela, no un mock.
- **Cabecera-detalle:** `Pago` / `Transaccion`.
- **Datos iniciales previstos:**
  - `Pago`: id_orden, monto, estado, medio
  - `Transaccion`: id_externo, fecha, respuesta_pasarela
- **Endpoints iniciales previstos:** `POST /api/pagos`, `GET /api/pagos/{id}`
- **¿Se comunica con otro microservicio?** Sí. Lee la orden en `orden-ms` (REST/Feign). Llama a Mercado Pago (API real). Puede consultar precio en `catalogo-ms`.
- **Rutas protegidas:** iniciar pago: CLIENTE autenticado. Consulta de pagos: ADMIN o el mismo cliente.
- **Requisitos:**
  1. El sistema debe confirmar el pago solo si la pasarela responde OK.
  2. El sistema no debe permitir pagar la misma orden dos veces.
  3. El sistema debe guardar el identificador externo de la transacción.

### Microservicio: auth-ms (integrante: Russman Keny Torres Lopez · tipo: no transaccional)

- **Descripción breve:** Identifica clientes y administradores. Emite el token que el resto de microservicios valida en las rutas protegidas.
- **Entidad principal:** `Usuario` / `Rol`.
- **Datos iniciales previstos:**
  - `Usuario`: correo, clave, nombre, activo
  - `Rol`: nombre (CLIENTE, ADMIN)
- **Endpoints iniciales previstos:** `POST /api/auth/login`, `POST /api/auth/registro`
- **¿Se comunica con otro microservicio?** No llama a otros. Los demás validan el JWT que emite (S7). De preferencia Keycloak más adelante.
- **Rutas protegidas:** login y registro públicos. Gestión de usuarios: ADMIN.
- **Requisitos:**
  1. El sistema debe autenticar al cliente y devolver un token.
  2. El sistema debe restringir escritura del catálogo al rol ADMIN.
  3. El sistema debe permitir al cliente autenticado ver el historial de sus órdenes.

- **Qué SÍ cubre este proyecto:** catálogo de ropa artesanal cuzqueña, pedidos, cobro con pasarela real, autenticación y frontend por Gateway.
- **Qué NO cubre:** logística de envíos, facturación SUNAT, inventario físico en talleres.

Pendiente para las siguientes sesiones: Config Server, Eureka, Gateway, mensajería y observabilidad siguen el patrón de cada sesión sobre Pagatu. No se declaran aquí. La seguridad sí, porque todos los microservicios validan el token.

## 4. Aprobación

- **Docente:** Abel Angel Sullon Macalupu
- **Fecha:** 27/08/2026

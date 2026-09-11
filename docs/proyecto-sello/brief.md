# Brief Técnico — ChaskaWear (Equipo 12)

## 1. Datos del equipo

- **Equipo:** 12
- **Proyecto:** ChaskaWear
- **Sección:** 5to ciclo — Aplicaciones Distribuidas 2026-2
- **Repositorio:** https://github.com/INGYasen/proyecto-final-misticha

| Integrante | Microservicios (3 c/u) |
|------------|------------------------|
| Yasen Cutipa Mayhua | `catalogo-ms`, `orden-ms`, `inventario-ms` |
| Russman Keny Torres Lopez | `pago-ms`, `auth-ms`, `notificacion-ms` |

## 2. Dominio

Ropa artesanal del Cusco (ponchos, chullos, polleras, mantas). El cliente entra por el Gateway, ve el catálogo, consulta stock, arma la orden, paga y recibe un aviso. Cobro con Mercado Pago en sandbox.

Bases y apps: `chaskawear_*` (no se mezclan con Pagatu).

## 3. Microservicios

Cada integrante lleva tres microservicios: uno transaccional y dos de apoyo.

| Integrante | Transaccional | No transaccionales | Qué hace |
|------------|---------------|--------------------|----------|
| Yasen Cutipa Mayhua | **orden-ms** (Orden + OrdenDetalle) | **catalogo-ms** (Categoria + Producto), **inventario-ms** (Stock + Movimiento) | Publica prendas, controla stock y arma el pedido |
| Russman Keny Torres Lopez | **pago-ms** (Pago + Transaccion) | **auth-ms** (Usuario + Rol), **notificacion-ms** (Aviso + Canal) | Identifica al usuario, cobra y avisa el estado del pedido |

Infra compartida: Config Server, Eureka, Gateway, Prometheus y Grafana.

## 4. Arquitectura

El cliente no llama a cada microservicio por puerto. Entra por el Gateway (`17080`). El Gateway pregunta a Eureka qué instancias están vivas y reparte con `lb://`. Cada servicio lee su YAML desde Config Server.

Flujo de negocio: catálogo → inventario → orden → pago → notificación.

| Componente | App | Quién | Puerto DEV |
|------------|-----|-------|------------|
| Config | chaskawear-config | equipo | 17888 |
| Eureka | chaskawear-eureka | equipo | 17761 |
| Gateway | chaskawear-gateway | equipo | 17080 |
| Catálogo | chaskawear-catalogo-ms | Yasen | 8180 / 8181 |
| Orden | chaskawear-orden-ms | Yasen | 8182 |
| Inventario | chaskawear-inventario-ms | Yasen | 8184 |
| Pago | chaskawear-pago-ms | Russman | 8186 |
| Auth | chaskawear-auth-ms | Russman | 8188 |
| Notificación | chaskawear-notificacion-ms | Russman | 8190 |
| Grafana / Prometheus | obs DEV | equipo | 12000 / 18090 |

Rutas del Gateway:

- `/api/v1/categorias/**`, `/api/v1/productos/**` → catálogo
- `/api/v1/ordenes/**`, `/api/v1/orden-detalles/**` → orden
- `/api/v1/stocks/**`, `/api/v1/movimientos/**` → inventario
- `/api/v1/pagos/**`, `/api/v1/transacciones/**` → pago
- `/api/v1/usuarios/**`, `/api/v1/roles/**` → auth
- `/api/v1/avisos/**`, `/api/v1/canales/**` → notificación

## 5. Aprobación

- **Docente:** Abel Angel Sullon Macalupu
- **Fecha:** 11/09/2026

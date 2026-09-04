# Brief Técnico — ChaskaWear (Equipo 12)

## 1. Datos del equipo

- **Equipo:** 12
- **Proyecto:** ChaskaWear
- **Sección:** 5to ciclo — Aplicaciones Distribuidas 2026-2
- **Repositorio:** https://github.com/INGYasen/proyecto-final-misticha

| Integrante | Microservicios a cargo (3 c/u) |
|------------|-------------------------------|
| Yasen Cutipa Mayhua | `catalogo-ms`, `orden-ms`, `inventario-ms` |
| Russman Keny Torres Lopez | `pago-ms`, `auth-ms`, `notificacion-ms` |

## 2. Dominio

Ropa artesanal del Cusco (ponchos, chullos, polleras, mantas). Flujo: catálogo → stock → orden → pago → aviso al cliente (Mercado Pago sandbox).

Bases y apps propias: `chaskawear_*` (no se mezclan con Pagatu).

## 3. Microservicios (quién hace qué)

Cada integrante lleva **mínimo dos** microservicios; en este equipo repartimos **tres** por persona (equidad).

| Integrante | Transaccional | No transaccionales | Qué hace |
|------------|---------------|--------------------|----------|
| Yasen Cutipa Mayhua | **orden-ms** (Orden + OrdenDetalle) | **catalogo-ms** (Categoria + Producto), **inventario-ms** (Stock + Movimiento) | Publica prendas, controla stock y arma el pedido |
| Russman Keny Torres Lopez | **pago-ms** (Pago + Transaccion) | **auth-ms** (Usuario + Rol), **notificacion-ms** (Aviso + Canal) | Identifica al usuario, cobra y avisa el estado del pedido |

Infra compartida del equipo (no cuenta como micro de una sola persona): Config Server, Eureka y Gateway.

## 4. Arquitectura prevista / actual

| Componente | App | Responsable | Puerto DEV | Base |
|------------|-----|-------------|------------|------|
| Config | chaskawear-config | equipo | 17888 | — |
| Eureka | chaskawear-eureka | equipo | 17761 | — |
| Catálogo | chaskawear-catalogo-ms | Yasen | 8180 / 8181 | chaskawear_catalogo_db @ 16432 |
| Orden | chaskawear-orden-ms | Yasen | 8182 / 8183 | chaskawear_orden_db @ 16434 |
| Inventario | chaskawear-inventario-ms | Yasen | por definir | chaskawear_inventario_db |
| Pago | chaskawear-pago-ms | Russman | por definir | chaskawear_pago_db |
| Auth | chaskawear-auth-ms | Russman | por definir | chaskawear_auth_db |
| Notificación | chaskawear-notificacion-ms | Russman | por definir | chaskawear_notificacion_db |

## 5. Aprobación

- **Docente:** Abel Angel Sullon Macalupu
- **Fecha:** 04/09/2026

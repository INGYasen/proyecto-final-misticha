# Brief Técnico — ChaskaWear (Equipo 12)

## 1. Datos del equipo

- **Equipo:** 12
- **Proyecto:** ChaskaWear
- **Sección:** 5to ciclo — Aplicaciones Distribuidas 2026-2
- **Repositorio:** https://github.com/INGYasen/proyecto-final-misticha

| Integrante | Microservicios a cargo |
|------------|------------------------|
| Yasen Cutipa Mayhua | `catalogo-ms`, `orden-ms` |
| Russman Keny Torres Lopez | `pago-ms`, `auth-ms` |

## 2. Dominio

Ropa artesanal del Cusco (ponchos, chullos, polleras, mantas). Flujo: catálogo → orden → pago (Mercado Pago sandbox).

Bases y apps propias: `chaskawear_*` (no se mezclan con Pagatu).

## 3. Microservicios (quién hace qué)

Cada integrante lleva **mínimo dos** microservicios (uno transaccional y uno no transaccional).

| Integrante | Transaccional | No transaccional | Qué hace |
|------------|---------------|------------------|----------|
| Yasen Cutipa Mayhua | **orden-ms** (Orden + OrdenDetalle) | **catalogo-ms** (Categoria + Producto) | Publica prendas y arma el pedido |
| Russman Keny Torres Lopez | **pago-ms** (Pago + Transaccion) | **auth-ms** (Usuario + Rol) | Cobra con Mercado Pago e identifica al usuario |

Infra compartida del equipo (no es “microservicio de negocio” de una sola persona): Config Server y Eureka.

## 4. Arquitectura actual (S3)

| Componente | App | Puerto DEV | Base |
|------------|-----|------------|------|
| Config | chaskawear-config | 17888 | — |
| Eureka | chaskawear-eureka | 17761 | — |
| Catálogo (Yasen) | chaskawear-catalogo-ms | 8180 / 8181 | chaskawear_catalogo_db @ 16432 |
| Orden (Yasen) | chaskawear-orden-ms | 8182 / 8183 | chaskawear_orden_db @ 16434 |
| Pago (Russman) | chaskawear-pago-ms | por definir | chaskawear_pago_db |
| Auth (Russman) | chaskawear-auth-ms | por definir | chaskawear_auth_db |

## 5. Aprobación

- **Docente:** Abel Angel Sullon Macalupu
- **Fecha:** 04/09/2026

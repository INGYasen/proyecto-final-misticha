# Brief Técnico — ChaskaWear (Equipo 12)

## 1. Datos del equipo

- **Equipo:** 12
- **Proyecto:** ChaskaWear
- **Sección:** 5to ciclo — Aplicaciones Distribuidas 2026-2
- **Repositorio:** https://github.com/INGYasen/proyecto-final-misticha

| Integrante | Rol |
|------------|-----|
| Yasen Cutipa Mayhua | Catálogo y órdenes |
| Russman Keny Torres Lopez | Pagos y autenticación |

## 2. Dominio

Ropa artesanal del Cusco (ponchos, chullos, polleras, mantas). Flujo: catálogo → orden → pago (Mercado Pago sandbox).

Bases y apps propias: `chaskawear_*` (no se mezclan con Pagatu).

## 3. Microservicios

| Integrante | Transaccional | No transaccional |
|------------|---------------|------------------|
| Yasen Cutipa Mayhua | orden-ms | catalogo-ms |
| Russman Keny Torres Lopez | pago-ms | auth-ms |

## 4. Arquitectura actual (S3)

| Componente | App | Puerto DEV | Base |
|------------|-----|------------|------|
| Config | chaskawear-config | 17888 | — |
| Eureka | chaskawear-eureka | 17761 | — |
| Catálogo | chaskawear-catalogo-ms | 8180 / 8181 | chaskawear_catalogo_db @ 16432 |
| Orden | chaskawear-orden-ms | 8182 / 8183 | chaskawear_orden_db @ 16434 |

## 5. Aprobación

- **Docente:** Abel Angel Sullon Macalupu
- **Fecha:** 04/09/2026

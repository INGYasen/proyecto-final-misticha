# ChaskaWear

Equipo **12** — DIST 2026-2.  
Flujo: catálogo → stock → orden → pago → aviso (Mercado Pago sandbox).

Repo: https://github.com/INGYasen/proyecto-final-misticha

## Integrantes (3 microservicios c/u)

| Integrante | Microservicios |
|------------|----------------|
| Yasen Cutipa Mayhua | catalogo-ms, orden-ms, inventario-ms |
| Russman Keny Torres Lopez | pago-ms, auth-ms, notificacion-ms |

Infra compartida: Config Server, Eureka, Gateway.

## DEV (hasta S3 — ya corriendo)

| Servicio | Puerto / recurso |
|----------|------------------|
| Config | 17888 |
| Eureka | 17761 |
| Catálogo (Yasen) | 8180 / 8181 |
| Orden (Yasen) | 8182 / 8183 |
| Postgres catálogo | 16432 · `chaskawear_catalogo_db` |
| Postgres orden | 16434 · `chaskawear_orden_db` |

Usuario BD: `chaska` / `chaska`.

Pendientes (mismas bases `chaskawear_*`): inventario-ms (Yasen), pago-ms, auth-ms, notificacion-ms (Russman).

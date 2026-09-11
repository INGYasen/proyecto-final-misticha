# ChaskaWear

Equipo **12** — DIST 2026-2.  
Ropa artesanal del Cusco. Flujo: catálogo → stock → orden → pago → aviso (Mercado Pago sandbox).

Repo: https://github.com/INGYasen/proyecto-final-misticha

## Integrantes

| Integrante | Microservicios |
|------------|----------------|
| Yasen Cutipa Mayhua | catalogo-ms, orden-ms, inventario-ms |
| Russman Keny Torres Lopez | pago-ms, auth-ms, notificacion-ms |

Infra compartida: Config Server, Eureka, Gateway, Prometheus, Grafana.

## Puertos DEV

| Servicio | Puerto / recurso |
|----------|------------------|
| Config | 17888 |
| Eureka | 17761 |
| Gateway | **17080** |
| Catálogo (Yasen) | 8180 / 8181 |
| Orden (Yasen) | 8182 |
| Inventario (Yasen) | 8184 |
| Pago (Russman) | 8186 |
| Auth (Russman) | 8188 |
| Notificación (Russman) | 8190 |
| Postgres catálogo | 16432 · `chaskawear_catalogo_db` |
| Postgres orden | 16434 · `chaskawear_orden_db` |
| Postgres inventario | 16436 · `chaskawear_inventario_db` |
| Postgres pago | 16438 · `chaskawear_pago_db` |
| Postgres auth | 16440 · `chaskawear_auth_db` |
| Postgres notificación | 16442 · `chaskawear_notificacion_db` |
| Grafana | 12000 (admin / admin123) |
| Prometheus | 18090 |
| Loki | 12100 |

Usuario BD: `chaska` / `chaska`. Distinto de Pagatu.

### Gateway

```text
http://localhost:17080/api/v1/categorias
http://localhost:17080/api/v1/productos
http://localhost:17080/api/v1/ordenes
http://localhost:17080/api/v1/stocks
http://localhost:17080/api/v1/pagos
http://localhost:17080/api/v1/usuarios
http://localhost:17080/api/v1/avisos
```

### Observabilidad

```powershell
cd obs
docker compose -f compose-dev.yml up -d
```

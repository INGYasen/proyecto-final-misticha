# ChaskaWear

Proyecto sello del equipo **DIST** — DIST 2026-2.

Venta en línea de ropa artesanal del Cusco (ponchos, chullos, polleras, mantas). Flujo: catálogo → orden → pago.

Repo de equipo: https://github.com/INGYasen/proyecto-final-misticha

## Integrantes

- Yasen Cutipa Mayhua — catalogo-ms, orden-ms
- Russman Keny Torres Lopez — pago-ms, auth-ms

## Estructura

- `services/pagatu-catalogo-ms` — categorías y prendas
- `services/pagatu-orden-ms` — órdenes y detalles
- `infra/pagatu-config` — Config Server y `config-repo`
- `docs/proyecto-sello/brief.md` — brief técnico del equipo

## Puertos DEV

| Servicio | Puerto |
|----------|--------|
| pagatu-config | 18888 |
| pagatu-catalogo-ms | 8080 |
| pagatu-orden-ms | 8082 |

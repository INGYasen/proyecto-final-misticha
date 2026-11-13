# Configuración local

1. Red Docker: `docker network create ms-net`
2. Compose de cada servicio (MySQL) y el de `ms-auth` (Keycloak)
3. Compose de `raymisupreme-back/obs` (Prometheus, Grafana, Loki, Kafka)
4. Importar el realm `raymisupreme-dev` desde `raymisupreme-back/raymisupreme-dev.json` si Keycloak arranca vacío
5. Maven en este orden: Config Server → Eureka → microservicios → Gateway
6. `npm start` en `fronted/`

Puertos y el listado de comandos están en el `README.md` de la raíz.

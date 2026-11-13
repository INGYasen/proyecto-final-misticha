# Prometheus

Config: `raymisupreme-back/obs/prometheus/prometheus-dev.yml`.

Scrapea Config Server, Eureka, Gateway y los `ms-*` en sus puertos de desarrollo. Keycloak quedó con métricas en `keycloak-dev:9000`.

`ms-catalogo` se scrapeá en `8094`. La segunda instancia (`8194`) se ve en Eureka; si Grafana no la muestra, es porque Prometheus no la tiene en el yaml.

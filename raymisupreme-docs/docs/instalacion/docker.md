# Docker

Desarrollo: un `docker-compose-dev.yml` por servicio (MySQL) más `obs/docker-compose-dev.yml` (métricas y Kafka). Keycloak sale del compose de `ms-auth` en el puerto `9091`.

Producción: `raymisupreme-back/infra/docker-compose.yml` + `docker-compose.prod.yml`. Ahí sí hay que definir nombre y clave de cada base.

La red se llama `ms-net`. Si no existe, Grafana/Promtail no ven a los demás contenedores.

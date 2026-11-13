# Observabilidad

Prometheus scrapea `/actuator/prometheus` de cada servicio. Grafana lo dibuja. Loki + Promtail juntan logs.

Compose: `raymisupreme-back/obs/docker-compose-dev.yml`.

| Pieza | Puerto |
|---|---|
| Prometheus | 19090 |
| Grafana | 13000 |
| Loki | 13100 |
| Kafka UI | 8085 |

El dashboard que se usa en la demo se llama **Instancias** (uid `raymi-instancias`).

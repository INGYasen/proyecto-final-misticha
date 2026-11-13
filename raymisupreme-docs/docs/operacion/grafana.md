# Grafana

http://localhost:13000

El dashboard **Instancias** sale provisionado desde `obs/grafana/provisioning/dashboards/json/instancias.json`. Arriba están las tarjetas up/down, después la línea de tiempo y una tabla de tiempo en marcha. Abajo hay una nota de qué hace cada servicio.

Si las tarjetas salen vacías, casi siempre Grafana pide login o Prometheus no está scrapeando todavía.

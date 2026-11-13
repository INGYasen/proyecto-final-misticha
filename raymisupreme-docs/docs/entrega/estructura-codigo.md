# Estructura del código

El backend es un repo Maven por servicio, no un monorepo con un solo `pom` padre.

```
plusdistribuidas/
  fronted/                      Angular
  raymisupreme-back/
    infra/config-server
    infra/registry-server
    infra/gateway
    infra/config-repo           yaml que lee Config Server
    services/ms-auth
    services/ms-acopio
    services/ms-inventario
    services/ms-catalogo
    services/ms-carrito
    services/ms-ventas
    services/ms-pagos
    obs/                        docker-compose de Prometheus/Grafana/Kafka
  raymisupreme-docs/
```

Cada `ms-*` tiene su `docker-compose-dev.yml` con MySQL (o Keycloak en `ms-auth`). El front llama a `http://localhost:7091`.

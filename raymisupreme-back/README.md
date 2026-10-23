# Backend Raymi Supreme

La guía de arranque, los puertos y el flujo de la tienda están en el [README de la raíz](../README.md).

## Desarrollo

MySQL y Keycloak salen de `services/<servicio>/docker-compose-dev.yml`. Kafka y la observabilidad salen de `obs/docker-compose-dev.yml`. Config Server, Eureka, Gateway y los microservicios se corren con Maven en perfil `dev` (ya viene así en cada `application.yml`).

Orden: red `ms-net` → Keycloak y MySQL → Kafka → Config Server → Eureka → microservicios → Gateway.

## Producción

```powershell
cd infra
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

Hay que definir el nombre y la contraseña de cada base antes de levantarlo. Las promociones las atiende `ms-catalogo`; el compose de producción no construye un servicio `ms-promociones` porque esa carpeta no existe.

## Secretos

No pegar tokens en el código. `ms-pagos` toma el access token de Mercado Pago desde `MP_ACCESS_TOKEN`. El secreto interno de confirmación de pago se lee de `INTERNAL_PAYMENT_SECRET`. Los client secret de Keycloak del entorno local están en `raymisupreme-dev.json` y en `infra/config-repo/ms-auth-dev.yml`.

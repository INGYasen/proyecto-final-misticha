# Raymi Supreme

Equipo **12** — DIST 2026-2.  
Moda andina exclusiva: alpaca y artesanía. La tienda vive en Angular y entra al sistema solo por el API Gateway.

Repo: https://github.com/INGYasen/proyecto-final-misticha

Flujo de compra: catálogo → carrito → checkout → pedido `PENDIENTE` → Mercado Pago → `ms-pagos` confirma en `ms-ventas` → pedido `PAGADO`. El stock no baja hasta que el pago queda aprobado.

## Equipo

Equipo 12 — DIST 2026-2.

- Yasen Cutipa Mayhua
- Russman Keny Torres Lopez

## Qué hay en este repositorio

| Carpeta | Qué es |
| --- | --- |
| `fronted/` | Tienda Angular 17 (NgModules). El nombre de la carpeta es `fronted`. |
| `raymisupreme-back/infra/` | Config Server, Eureka y API Gateway. |
| `raymisupreme-back/services/` | `ms-auth`, `ms-acopio`, `ms-inventario`, `ms-catalogo`, `ms-carrito`, `ms-ventas`, `ms-pagos`. |
| `raymisupreme-back/obs/` | Prometheus, Grafana, Loki, Promtail, Kafka y Kafka UI. |
| `raymisupreme-docs/` | Documentación técnica (MkDocs). |

Las promociones no son un microservicio aparte: el Gateway las enruta a `ms-catalogo`.

## Seguridad de la tienda

- Realm de desarrollo: `raymisupreme-dev` en Keycloak 25 (`http://localhost:9091`).
- La tienda usa el cliente público `raymisupreme-spa` con Authorization Code + PKCE S256. El redirect es `http://localhost:4200/login`.
- La contraseña se escribe en Keycloak, no en Angular. El token de acceso queda en memoria: al recargar la página hay que entrar de nuevo.
- El cliente `raymisupreme-app` conserva el password grant solo para scripts. La SPA no lo usa.
- Roles del realm: `CLIENTE`, `VENDEDORA`, `SUPER_ADMIN`.
- CORS del navegador está solo en el Gateway, origen `http://localhost:4200`.

## Puertos de desarrollo

| Pieza | Puerto | Dónde |
| --- | ---: | --- |
| Tienda Angular | 4200 | `npm start` en `fronted/` |
| Config Server | 7071 | Maven |
| Eureka | 7081 | Maven · http://localhost:7081 |
| API Gateway | 7091 | Maven · la tienda llama aquí |
| Keycloak | 9091 | Docker · realm `raymisupreme-dev` |
| ms-acopio | 8086 | MySQL `localhost:3316` / `db_acopio` |
| ms-inventario | 8087 | MySQL `localhost:3317` / `db_inventario` |
| ms-ventas | 8088 | MySQL `localhost:3318` / `db_ventas` |
| ms-auth | 8089 | usuarios en Keycloak |
| ms-pagos | 8092 | MySQL `localhost:3321` / `db_pagos` |
| ms-carrito | 8093 | MySQL `localhost:3322` / `db_carrito` |
| ms-catalogo | 8094 | MySQL `localhost:3319` / `db_catalogo` |
| Kafka | 9092 | Docker |
| Kafka UI | 8085 | http://localhost:8085 |
| Prometheus | 19090 | http://localhost:19090 |
| Grafana | 13000 | http://localhost:13000 |
| Loki | 13100 | Docker |

MySQL de desarrollo: usuario `root`, base creada por el compose de cada servicio.  
Java 17 y Maven. No hace falta exportar `SPRING_PROFILES_ACTIVE`: los servicios ya arrancan en perfil `dev` y leen Config Server en `http://localhost:7071`.

Mercado Pago: la llave pública está en `fronted/src/environments/environment.ts`. El access token no va en el código; `ms-pagos` lo lee de la variable `MP_ACCESS_TOKEN`.

## Arranque en local

Hace falta Docker, JDK 17, Maven y Node.js. La red `ms-net` la usa observabilidad.

```powershell
docker network create ms-net
```

Si la red ya existe, Docker avisa y se sigue con el paso siguiente.

Bases y Keycloak. Cada `docker-compose-dev.yml` está dentro del servicio, no hay un compose único de infraestructura para desarrollo.

```powershell
cd raymisupreme-back\services
docker compose -f ms-auth\docker-compose-dev.yml up -d
docker compose -f ms-acopio\docker-compose-dev.yml up -d
docker compose -f ms-inventario\docker-compose-dev.yml up -d
docker compose -f ms-ventas\docker-compose-dev.yml up -d
docker compose -f ms-catalogo\docker-compose-dev.yml up -d
docker compose -f ms-pagos\docker-compose-dev.yml up -d
docker compose -f ms-carrito\docker-compose-dev.yml up -d
```

Kafka y observabilidad:

```powershell
cd ..\obs
docker compose -f docker-compose-dev.yml up -d
```

Keycloak queda en http://localhost:9091. El realm `raymisupreme-dev` se importa desde `raymisupreme-back/raymisupreme-dev.json` (consola de administración → Realm settings, o `kc.sh import`). Sin ese realm la tienda no puede iniciar sesión.

Microservicios, en este orden y en terminales distintas, desde la raíz del repo:

```powershell
mvn -f raymisupreme-back\infra\config-server\pom.xml spring-boot:run
mvn -f raymisupreme-back\infra\registry-server\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-auth\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-acopio\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-inventario\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-catalogo\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-carrito\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-ventas\pom.xml spring-boot:run
mvn -f raymisupreme-back\services\ms-pagos\pom.xml spring-boot:run
mvn -f raymisupreme-back\infra\gateway\pom.xml spring-boot:run
```

Esperar a que Eureka muestre los servicios en `UP` antes de abrir la tienda.

Tienda:

```powershell
cd fronted
npm install
npm start
```

http://localhost:4200

Comprobación rápida: http://localhost:7091/actuator/health debe responder `UP`, y en el navegador el botón Entrar debe redirigir a Keycloak con `client_id=raymisupreme-spa` y `code_challenge_method=S256`.

## Producción (Docker)

El archivo de producción sí levanta infraestructura, bases y microservicios. Pide variables de entorno de cada base (`ACOPIO_DB_NAME`, `ACOPIO_DB_PASSWORD`, y el mismo par para inventario, ventas, catálogo, pagos y carrito).

```powershell
cd raymisupreme-back\infra
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

| Pieza | Puerto en el host |
| --- | ---: |
| Config Server | 7072 |
| Eureka | 7082 |
| Gateway | 7092 |
| Keycloak | 9090 |
| ms-acopio | 8186 |
| ms-inventario | 8187 |
| ms-ventas | 8188 |
| ms-auth | 8189 |
| ms-catalogo | 8190 |
| ms-pagos | 8192 |
| ms-carrito | 8193 |

## Stack

Java 17 · Spring Boot 3.5 · Spring Cloud 2025.0.1 · Spring Cloud Gateway · Eureka · OpenFeign · Resilience4j · Keycloak 25 · MySQL 8 · Kafka · Angular 17 · Mercado Pago · Prometheus · Grafana · Loki.

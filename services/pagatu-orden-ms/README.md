# pagatu-orden-ms

Microservicio de **órdenes** del sistema distribuido Pagatu (curso DIST 2026-2, S1).
Replica el patrón de `pagatu-catalogo-ms`: capas Controller → Service → Mapper → Repository → Entity,
PostgreSQL con Flyway, Swagger, Actuator y filtro de trazabilidad `X-Trace-ID`.

## 1. Responsabilidad del dominio

`pagatu-orden-ms` gestiona **únicamente** las órdenes de compra y sus líneas de detalle:

| Entidad | Tabla | Qué representa |
|---------|-------|----------------|
| `Orden` | `ordenes` | Cabecera de la orden: código, cliente, fecha, estado (`CREADA`/`PAGADA`/`ANULADA`) y total |
| `OrdenDetalle` | `orden_detalles` | Cada línea de la orden: producto, cantidad, precio unitario y subtotal |

**No** almacena categorías ni productos: esos pertenecen a `pagatu-catalogo-ms`. El detalle guarda
`id_producto` como **referencia lógica sin llave foránea**, porque el producto vive en otra base de
datos, de otro microservicio. Cuando se necesite el nombre o el precio vigente del producto, se
consultará a `pagatu-catalogo-ms` por red — nunca leyendo su base de datos.

## 2. Requisitos

- Java 25 (Temurin/Microsoft JDK)
- Docker Desktop
- PowerShell 5.1 o superior (no se usa Postman)

Verificar Java antes de empezar:

```powershell
$env:JAVA_HOME = "C:\Users\yasen\.jdk\jdk-25.0.2"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

## 3. Puertos usados

| Componente | Puerto host | Nota |
|------------|-------------|------|
| PostgreSQL DEV | `15434` | Distinto al `15432` de catálogo, para que ambos convivan |
| PostgreSQL PROD | `25434` | Solo para inspección con psql |
| Microservicio DEV instancia 1 | `8080` | Puerto por defecto de `application-dev.yml` |
| Microservicio DEV instancia 2 | `8081` | Se pasa por argumento, no se toca el YML |
| Microservicio PROD | interno `8080` | No se publica al host (lo resolverá el Gateway) |

> Si `pagatu-catalogo-ms` está corriendo en `8080`, deténlo antes (o levanta orden en otros puertos):
> la guía pide evidenciar `orden-ms` en `8080` y `8081`.

## 4. DEV — Levantar PostgreSQL

```powershell
cd pagatu\services\pagatu-orden-ms
docker compose -f compose-dev.yml up -d
docker ps
```

Verificar que la base existe y todavía **no** tiene tablas (Flyway las creará al arrancar):

```powershell
docker exec pagatu-postgres-orden-dev psql -U pagatu -d pagatu_orden_db -c "SELECT current_database(), current_user;"
docker exec pagatu-postgres-orden-dev psql -U pagatu -d pagatu_orden_db -c "\dt"
```

## 5. DEV — Compilar y ejecutar con Maven Wrapper

```powershell
.\mvnw.cmd -DskipTests clean package
.\mvnw.cmd spring-boot:run
```

En el log de arranque debe aparecer la migración de Flyway y el puerto de Tomcat:

```text
Migrating schema "public" to version "1 - create orden tables"
Successfully applied 1 migration to schema "public", now at version v1
Tomcat started on port 8080 (http) with context path '/'
```

## 6. Endpoints

| Recurso | URL |
|---------|-----|
| Saludo | <http://localhost:8080/saludo> |
| Swagger UI | <http://localhost:8080/swagger-ui/index.html> |
| OpenAPI JSON | <http://localhost:8080/v3/api-docs> |
| Health | <http://localhost:8080/actuator/health> |
| Metrics | <http://localhost:8080/actuator/metrics> |
| Órdenes | <http://localhost:8080/api/ordenes> |
| Detalles de orden | <http://localhost:8080/api/orden-detalles> |

Códigos de respuesta implementados:

| Situación | Código |
|-----------|--------|
| Creación correcta | `201 Created` |
| Consulta / actualización correcta | `200 OK` |
| Eliminación correcta | `204 No Content` |
| Datos inválidos (campo vacío, estado no permitido, cantidad `0`) | `400 Bad Request` con el detalle por campo |
| Orden o detalle inexistente | `404 Not Found` |
| Código de orden duplicado | `409 Conflict` |

Toda respuesta de error incluye el `traceId` del filtro `CorrelationIdFilter`, que también viaja en
la cabecera `X-Trace-ID` y aparece en los logs.

## 7. Pruebas por PowerShell (sin Postman)

```powershell
.\scripts\test-crud.ps1                # prueba el CRUD completo contra 8080
.\scripts\test-crud.ps1 -Puerto 8081   # el mismo CRUD contra la instancia 2
.\scripts\test-escalamiento.ps1        # compara ambas instancias
```

`test-crud.ps1` cubre el CRUD de órdenes y de detalles, el recálculo automático del total y los
casos de error 400, 404 y 409.

## 8. Verificar la base de datos con psql

```powershell
$c = "pagatu-postgres-orden-dev"
docker exec $c psql -U pagatu -d pagatu_orden_db -c "\dt"
docker exec $c psql -U pagatu -d pagatu_orden_db -c "SELECT installed_rank, version, description, success FROM flyway_schema_history;"
docker exec $c psql -U pagatu -d pagatu_orden_db -c "\d ordenes"
docker exec $c psql -U pagatu -d pagatu_orden_db -c "SELECT id, codigo, cliente, estado, total FROM ordenes ORDER BY id;"
docker exec $c psql -U pagatu -d pagatu_orden_db -c "SELECT id, id_orden, id_producto, cantidad, precio_unitario, subtotal FROM orden_detalles ORDER BY id;"
```

Comprobar que el total de cada orden coincide con la suma de sus detalles:

```powershell
docker exec $c psql -U pagatu -d pagatu_orden_db -c "SELECT o.codigo, o.total, COALESCE(SUM(d.subtotal),0) AS suma_detalles, (o.total = COALESCE(SUM(d.subtotal),0)) AS coincide FROM ordenes o LEFT JOIN orden_detalles d ON d.id_orden = o.id GROUP BY o.id, o.codigo, o.total ORDER BY o.codigo;"
```

## 9. Escalamiento horizontal — dos instancias en paralelo

**Terminal 1** (instancia 1, puerto por defecto):

```powershell
.\mvnw.cmd spring-boot:run
```

**Terminal 2** (instancia 2, puerto por argumento — no se modifica ningún archivo):

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

Con `--server.port=0` Spring elige un puerto libre automáticamente, que es lo que ocurre en un
despliegue real detrás de un balanceador o un service discovery.

**Terminal 3** (verificación):

```powershell
.\scripts\test-escalamiento.ps1
```

## 10. PROD local con Docker (opcional)

```powershell
docker compose up -d --build --scale pagatu-orden-ms=2
docker compose ps
docker compose logs pagatu-orden-ms --tail 30
```

El microservicio **no publica puerto al host**: se accede desde la red interna `pagatu-orden-int`.
Por eso la prueba se hace desde otro contenedor de la misma red:

```powershell
docker run --rm --network pagatu-orden-int curlimages/curl:latest -s http://pagatu-orden-prod-pagatu-orden-ms-1:8080/actuator/health
```

Detener PROD:

```powershell
docker compose down
```

## 11. Detener todo

```powershell
docker compose down                        # PROD
docker compose -f compose-dev.yml down     # PostgreSQL DEV (agregar -v para borrar los datos)
```

Las instancias DEV se detienen con `Ctrl + C` en cada terminal.

## 12. Estructura del proyecto

```text
pagatu-orden-ms/
├── compose-dev.yml                  PostgreSQL para DEV (15434)
├── compose.yml                      PostgreSQL + microservicio para PROD local
├── Dockerfile                       Build multi-etapa (Maven 25 -> JRE 25)
├── .env / .env.example              Variables de PROD local
├── mvnw / mvnw.cmd / .mvn/          Maven Wrapper
├── scripts/
│   ├── test-crud.ps1                CRUD completo + errores 400/404/409
│   └── test-escalamiento.ps1        Verificación de las dos instancias
└── src/main/
    ├── java/pe/edu/upeu/orden/
    │   ├── config/OpenApiConfig.java
    │   ├── controller/              SaludoController, OrdenController, OrdenDetalleController
    │   ├── dto/                     Request y Response de cada recurso
    │   ├── entity/                  Orden, OrdenDetalle
    │   ├── exception/               ResourceNotFound, DuplicateResource, GlobalExceptionHandler
    │   ├── filter/CorrelationIdFilter.java
    │   ├── mapper/                  OrdenMapper, OrdenDetalleMapper
    │   ├── repository/              OrdenRepository, OrdenDetalleRepository
    │   └── service/                 OrdenService, OrdenDetalleService
    └── resources/
        ├── application.yml          Nombre del servicio y perfil activo
        ├── application-dev.yml      Puerto 8080, BD local, Swagger y métricas
        ├── application-prod.yml     Variables de entorno, Swagger deshabilitado
        ├── logback-spring.xml       Log con traceId
        └── db/migration/V1__create_orden_tables.sql
```

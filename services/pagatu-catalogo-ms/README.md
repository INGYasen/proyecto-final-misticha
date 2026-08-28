# pagatu-catalogo-ms - S1

Microservicio de catálogo para el curso DIST 2026-2 (Pagatu).

## Requisitos

- Java 25
- Docker Desktop

## Estructura del monorepo

```text
pagatu/
├── services/
│   └── pagatu-catalogo-ms/
├── infra/       (S2+: Config Server, Eureka, Gateway)
└── platform/    (S3+: observabilidad, Kafka)
```

## DEV - Levantar PostgreSQL

```powershell
cd services/pagatu-catalogo-ms
docker compose -f compose-dev.yml up -d
```

## DEV - Ejecutar microservicio

```powershell
cd services/pagatu-catalogo-ms
.\mvnw.cmd spring-boot:run
```

## Endpoints

| Recurso | URL |
|---------|-----|
| Saludo | http://localhost:8080/saludo |
| Swagger | http://localhost:8080/swagger-ui/index.html |
| Health | http://localhost:8080/actuator/health |
| Categorías | http://localhost:8080/api/categorias |
| Productos | http://localhost:8080/api/productos |

## Pruebas PowerShell

Ver `scripts/test-crud.ps1`

# pagatu-config

Config Server de Pagatu (S2).

## DEV

```powershell
cd infra/pagatu-config
.\mvnw.cmd spring-boot:run
```

- Health: http://localhost:18888/actuator/health
- Catalogo DEV: http://localhost:18888/pagatu-catalogo-ms/dev
- Orden DEV: http://localhost:18888/orden-ms/dev

## PROD local

```powershell
cd infra
docker compose up -d --build
```

- Health: http://localhost:28888/actuator/health

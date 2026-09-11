# chaskawear-gateway

Punto único de acceso de **ChaskaWear** — Equipo 12 (S4).

Puerto DEV: `17080` (Pagatu usa `18080`).

Rutas:
- `/api/v1/categorias/**`, `/api/v1/productos/**` → `lb://chaskawear-catalogo-ms`
- `/api/v1/ordenes/**`, `/api/v1/orden-detalles/**` → `lb://chaskawear-orden-ms`

```powershell
.\mvnw.cmd spring-boot:run
```

Ejemplo: `http://localhost:17080/api/v1/categorias`

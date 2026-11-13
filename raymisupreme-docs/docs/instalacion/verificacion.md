# Verificación

```powershell
.\chequear-stack.ps1
```

A mano:

- Eureka `http://localhost:7081` — los servicios en `UP`. `CATALOGO` puede aparecer dos veces (`8094` y `8194`).
- Gateway `http://localhost:7091/actuator/health` → `UP`
- Tienda `http://localhost:4200`
- Grafana `http://localhost:13000` — dashboard **Instancias**
- JWT: `.\demo-jwt-401-200.ps1` (sin token 401, con `cliente1` 200)

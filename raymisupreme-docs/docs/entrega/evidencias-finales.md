# Evidencias

Para la demo, esto es lo que se abre:

- Tienda: http://localhost:4200
- Eureka: http://localhost:7081
- Gateway health: http://localhost:7091/actuator/health
- Grafana (dashboard Instancias): http://localhost:13000
- Kafka UI: http://localhost:8085
- Prometheus: http://localhost:19090

JWT (401 sin token, 200 con token de `cliente1`):

```powershell
.\demo-jwt-401-200.ps1
```

El cobro se ve en el checkout de la tienda (Mercado Pago). El evento de Kafka aparece al **crear** el pedido, no al marcarlo pagado.

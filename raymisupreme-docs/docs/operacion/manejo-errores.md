# Errores que ya vimos

| Qué sale | Casi siempre es |
|---|---|
| Gateway no responde | Se cerró Cursor y Maven del Gateway se cayó |
| 401 en `/api/v1/clientes/me` | Normal sin token. Con token de Keycloak tiene que ser 200 |
| Preferencia 400 "pedido sin items" | El pedido se creó vacío |
| Yape "Collector doesn't respond" | La cuenta no tiene Yape online. La tienda abre Checkout Pro |
| Grafana en blanco | Login de Grafana, o Prometheus sin scrape |
| Pedido en `PENDIENTE` eterno | No llegó el webhook / no se confirmó el pago |

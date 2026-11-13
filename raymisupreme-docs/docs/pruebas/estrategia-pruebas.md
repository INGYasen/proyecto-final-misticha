# Cómo lo probamos

No hay una batería enorme de unitarios. Lo que se enseña en clase se prueba así:

- Puerto arriba / Eureka `UP`
- JWT: 401 sin token, 200 con token
- Compra en la tienda hasta Mercado Pago
- Pedido `PENDIENTE` → `PAGADO` y el stock
- Circuit breaker (inventario caído → fallback)
- Grafana con las instancias en verde

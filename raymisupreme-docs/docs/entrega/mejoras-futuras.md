# Qué falta

- Stripe: hay endpoint, pero la tienda cobra con Mercado Pago.
- Prometheus todavía scrapea `ms-catalogo` en `8094`. La copia de `8194` se ve en Eureka, no siempre en Grafana.
- Webhook de Mercado Pago en local necesita un túnel si se quiere probar el aviso real desde afuera.
- El carrito de invitado vive en `localStorage`; si se limpia el navegador, se pierde.

# Probar el flujo de compra

1. Abrir http://localhost:4200
2. Meter una prenda al carrito
3. Ir a pagar (si pide login: `cliente1` / `cliente1`)
4. Completar datos y Mercado Pago
5. En Kafka UI (`:8085`) el topic `pedidos` tiene el evento del pedido creado
6. En admin, el pedido pasa a `PAGADO` cuando Mercado Pago aprueba

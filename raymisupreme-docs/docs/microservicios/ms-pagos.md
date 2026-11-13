# ms-pagos

`ms-pagos` cobra los pedidos que crea `ms-ventas`. El cobro real va por Mercado Pago. Stripe quedó como fallback y no es el camino que usa la tienda.

## Qué hace

- Arma la preferencia de Mercado Pago (Checkout Pro)
- Recibe el pago del Brick (tarjeta / Yape) cuando está habilitado
- Escucha el webhook y deja el pago en `CONFIRMADO`
- Avisa a `ms-ventas` para pasar el pedido a `PAGADO`
- Guarda el comprobante

## Rutas que importan

| Recurso | Ruta |
|---|---|
| Preferencia Checkout Pro | `POST /api/v1/pagos/mercadopago/preferencia` |
| Pago Brick | `POST /api/v1/pagos/mercadopago/payment` |
| Webhook | `POST /api/v1/pagos/mercadopago/webhook` |
| Pago por pedido | `GET /api/v1/pagos/pedido/{idPedido}` |
| Comprobantes | `/api/pagos/comprobantes` |

## Flujo

1. El checkout crea el pedido en `PENDIENTE`.
2. `ms-pagos` pide a Mercado Pago la preferencia o registra el pago del Brick.
3. Si Mercado Pago aprueba, el webhook (o la confirmación) deja el pago en `CONFIRMADO`.
4. `ms-ventas` pasa el pedido a `PAGADO` y recién ahí se descuenta stock.

El access token no va en el código: `ms-pagos` lo lee de `MP_ACCESS_TOKEN`. La llave pública de la tienda está en `fronted/src/environments/environment.ts`.

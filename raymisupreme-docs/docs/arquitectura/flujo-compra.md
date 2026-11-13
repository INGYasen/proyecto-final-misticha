# Flujo de compra

Se puede mirar el catálogo sin cuenta. El carrito también se arma como invitado (queda en el navegador). Para pagar hay que entrar con Keycloak.

## Qué pasa

1. El cliente (o el invitado) mete prendas al carrito.
2. En el checkout, si era invitado, inicia sesión. El carrito se conserva.
3. `ms-ventas` crea el pedido en `PENDIENTE` y publica el evento en Kafka (`pedidos`).
4. `ms-pagos` pide a Mercado Pago la preferencia (Checkout Pro) o registra el Brick.
5. Si Mercado Pago aprueba, el webhook deja el pago en `CONFIRMADO`.
6. `ms-ventas` pasa el pedido a `PAGADO` y `ms-inventario` descuenta stock.
7. El admin puede marcar confirmado / enviado / entregado.

Si Yape directo no responde (cuenta de prueba), la tienda abre Checkout Pro.

```mermaid
sequenceDiagram
    actor Cliente
    participant Front as Angular
    participant GW as Gateway
    participant Keycloak
    participant Cat as ms-catalogo
    participant Car as ms-carrito
    participant Ven as ms-ventas
    participant Pag as ms-pagos
    participant MP as Mercado Pago
    participant Inv as ms-inventario
    participant Kafka

    Cliente->>Front: Ve catálogo
    Front->>GW: GET productos
    GW->>Cat: publicados
    Cat-->>Front: prendas

    Cliente->>Front: Agrega al carrito
    Front->>GW: carrito
    GW->>Car: guarda
    Car->>Cat: precio vigente

    Cliente->>Keycloak: login (si no había sesión)
    Cliente->>Front: Pagar
    Front->>GW: crear pedido
    GW->>Ven: POST /pedidos
    Ven->>Car: items
    Ven->>Kafka: pedido PENDIENTE
    Ven-->>Front: idPedido

    Front->>GW: preferencia MP
    GW->>Pag: POST /mercadopago/preferencia
    Pag->>MP: Checkout Pro / Brick
    MP-->>Pag: aprobado
    MP->>Pag: webhook
    Pag->>Ven: confirmar pago
    Ven->>Inv: descontar stock
    Ven-->>Front: pedido PAGADO
```

## Estados

| Estado | Cuándo |
|---|---|
| `PENDIENTE` | Pedido creado, todavía no cobrado |
| `PAGADO` | Mercado Pago aprobó y se bajó stock |
| `CONFIRMADO` / `ENVIADO` / `ENTREGADO` | Los marca el admin |
| `CANCELADO` | No se completó la compra |

Reglas que importan: el carrito no se fía del precio que manda el front (pregunta a catálogo). El stock no baja en `PENDIENTE`.

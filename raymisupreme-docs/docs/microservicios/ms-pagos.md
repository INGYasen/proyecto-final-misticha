# Microservicio de Pagos

`ms-pagos` administra los pagos asociados a los pedidos creados por `ms-ventas`.

## Responsabilidades

- Procesar un pago.
- Asociarlo con un pedido y un cliente.
- Registrar el monto y método de pago.
- Mantener el estado del pago.
- Generar comprobantes.
- Consultar pagos por pedido.

## Recursos principales

| Recurso | Ruta |
|---|---|
| Pagos | `/api/v1/pagos` |
| Procesar pago | `/api/v1/pagos/procesar` |
| Pago por pedido | `/api/v1/pagos/pedido/{idPedido}` |
| Comprobantes | `/api/pagos/comprobantes` |

## Flujo


ms-ventas → ms-pagos → Registro del pago

En el flujo normal, el pago no se crea manualmente. ms-ventas solicita su procesamiento cuando el pedido pendiente es confirmado.
Si el pago se registra correctamente:
<p> Pago = EXITOSO<br>
Pedido = PAGADO</p>
Stock = ACTUALIZADO
La implementación actual simula el registro del pago y no está conectada a una pasarela bancaria real.
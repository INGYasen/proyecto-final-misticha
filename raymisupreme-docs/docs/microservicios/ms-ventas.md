# Microservicio de Ventas

`ms-ventas` coordina el proceso de compra. Administra clientes, pedidos y detalles del pedido.

## Responsabilidades

- Registrar clientes.
- Obtener el contenido del carrito.
- Crear pedidos.
- Registrar los detalles de la compra.
- Consultar pedidos por cliente.
- Confirmar pedidos.
- Cancelar pedidos.
- Coordinar el pago.
- Solicitar la actualización del stock.

## Recursos principales

| Recurso | Ruta |
|---|---|
| Clientes | `/api/v1/clientes` |
| Checkout | `/api/v1/ventas/checkout/{idCliente}` |
| Pedidos | `/api/v1/pedidos` |
| Detalles | `/api/v1/detalles-pedido` |

## Creación del pedido


<p> ms-ventas → ms-carrito <br>
ms-ventas → ms-inventario</p>
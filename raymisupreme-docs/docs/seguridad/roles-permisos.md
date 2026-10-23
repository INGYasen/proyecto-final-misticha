# Roles y Permisos

El código de RaymiSupreme utiliza principalmente los siguientes roles:

| Rol | Responsabilidad |
|---|---|
| `SUPER_ADMIN` | Administración completa |
| `VENDEDORA` | Consulta operativa y atención de ventas |
| Usuario autenticado | Carrito, checkout y algunas operaciones de compra |
| Visitante | Catálogo y recursos públicos |

## Superadministrador

`SUPER_ADMIN` puede:

- Administrar usuarios.
- Registrar y eliminar artesanos.
- Registrar lotes.
- Crear categorías y prendas.
- Registrar inspecciones.
- Administrar productos, precios y descuentos.
- Modificar clientes y pedidos.
- Confirmar y cancelar pedidos.
- Consultar pagos y comprobantes.
- Consultar endpoints técnicos de instancias.

## Vendedora

`VENDEDORA` puede consultar:

- Artesanos y lotes.
- Categorías y prendas.
- Stock e inspecciones.
- Ítems, precios y descuentos.
- Clientes.
- Pedidos y detalles.

No puede:

- Administrar usuarios.
- Crear o eliminar información maestra.
- Modificar inventario.
- Consultar pagos y comprobantes.
- Cancelar pedidos.
- Confirmar pedidos directamente en el controller actual.

## Usuario autenticado

El código no exige actualmente el rol `CLIENTE` para el flujo de compra. Solo exige un JWT válido en:

- Carrito.
- Registro inicial del cliente.
- Checkout.
- Creación de pedido.
- Creación de detalle.
- Consulta de stock.
- Descuento interno de stock.
- Procesamiento del pago.

Esto significa que cualquier usuario autenticado puede atravesar esas reglas si posee un token válido.

## Matriz general

| Operación | Público | Autenticado | VENDEDORA | SUPER_ADMIN |
|---|:---:|:---:|:---:|:---:|
| Consultar catálogo público | Sí | Sí | Sí | Sí |
| Gestionar carrito | No | Sí | Sí | Sí |
| Realizar checkout | No | Sí | Sí | Sí |
| Consultar artesanos | No | No | Sí | Sí |
| Registrar artesanos | No | No | No | Sí |
| Consultar inventario | No | Parcial | Sí | Sí |
| Modificar inventario | No | Solo operaciones internas autorizadas | No | Sí |
| Consultar pedidos | No | No | Sí | Sí |
| Confirmar pedido | No | No | No | Sí |
| Cancelar pedido | No | No | No | Sí |
| Consultar pagos | No | No | No | Sí |
| Administrar usuarios | No | No | No | Sí |

> Los permisos efectivos se obtienen combinando las reglas del Gateway con los `@PreAuthorize` de cada controller.
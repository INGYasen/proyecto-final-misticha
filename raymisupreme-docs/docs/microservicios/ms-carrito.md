
# Microservicio de Carrito

`ms-carrito` administra los productos que un cliente desea comprar antes de generar un pedido.

## Responsabilidades

- Crear o recuperar el carrito activo.
- Agregar productos.
- Modificar cantidades.
- Eliminar productos.
- Vaciar el carrito.
- Calcular subtotales.
- Cambiar el estado del carrito.

## Ruta principal


`/api/v1/carrito/{idCliente}`

## Validación del producto

Cuando el cliente agrega un producto al carrito, el servicio `ms-carrito` no utiliza directamente el nombre ni el precio enviados desde el frontend.  
En su lugar, consulta a `ms-catalogo` para obtener la información vigente del producto.

Esto permite evitar que un usuario pueda modificar manualmente datos sensibles como el precio, el nombre del producto o el estado de publicación.

---

## Flujo de validación

<div style="display:flex; flex-direction:column; align-items:center; gap:12px; margin:25px 0 40px 0;">

  <div style="background:#3949ab; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    Cliente
    <br>
    <span style="font-weight:normal;">Solicita agregar producto</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#fb8c00; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-carrito
    <br>
    <span style="font-weight:normal;">Recibe la solicitud</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#43a047; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
    ms-catalogo
    <br>
    <span style="font-weight:normal;">Valida nombre, precio y estado</span>
  </div>

</div>
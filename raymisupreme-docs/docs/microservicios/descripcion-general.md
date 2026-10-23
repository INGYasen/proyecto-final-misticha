# Descripción General de los Microservicios

RaymiSupreme divide sus funcionalidades en microservicios independientes. Cada uno representa una etapa del negocio y administra sus propios datos.


## Flujo general

El sistema sigue una secuencia funcional donde el acceso ingresa por el API Gateway, pasa por autenticación y luego continúa por los microservicios del negocio hasta finalizar en el pago.

<div style="display: flex; flex-direction: column; align-items: center; gap: 12px; margin: 30px 0;">

  <div style="background:#3949ab; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    API Gateway
    <br>
    <span style="font-weight:normal;">Entrada principal del sistema</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#00897b; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    ms-auth
    <br>
    <span style="font-weight:normal;">Autenticación y roles</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="display:flex; align-items:center; gap:10px; flex-wrap:wrap; justify-content:center;">

    <div style="background:#5e35b1; color:white; padding:14px 20px; border-radius:10px; width:220px; text-align:center; font-weight:bold;">
      ms-acopio
      <br>
      <span style="font-weight:normal;">Artesanos y lotes</span>
    </div>

    <div style="font-size:26px;">→</div>

    <div style="background:#1e88e5; color:white; padding:14px 20px; border-radius:10px; width:220px; text-align:center; font-weight:bold;">
      ms-inventario
      <br>
      <span style="font-weight:normal;">Prendas y stock</span>
    </div>

    <div style="font-size:26px;">→</div>

    <div style="background:#43a047; color:white; padding:14px 20px; border-radius:10px; width:220px; text-align:center; font-weight:bold;">
      ms-catalogo
      <br>
      <span style="font-weight:normal;">Productos publicados</span>
    </div>

  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#fb8c00; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    ms-carrito
    <br>
    <span style="font-weight:normal;">Carrito del cliente</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#e53935; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    ms-ventas
    <br>
    <span style="font-weight:normal;">Pedidos y ventas</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#8e24aa; color:white; padding:14px 24px; border-radius:10px; width:280px; text-align:center; font-weight:bold;">
    ms-pagos
    <br>
    <span style="font-weight:normal;">Pagos y comprobantes</span>
  </div>

</div>

## Interpretación del flujo

El flujo inicia en el `API Gateway`, que centraliza el acceso al sistema. Luego interviene `ms-auth`, encargado de la autenticación y la gestión de roles.

Después, los microservicios `ms-acopio`, `ms-inventario` y `ms-catalogo` organizan la información relacionada con artesanos, prendas, stock y productos publicados.

Finalmente, `ms-carrito` administra la selección de productos del cliente, `ms-ventas` genera el pedido y `ms-pagos` registra la transacción final.

<h4>Los servicios se comunican mediante APIs REST y OpenFeign. Eureka permite localizar las instancias disponibles y el API Gateway funciona como punto único de entrada.</h4>

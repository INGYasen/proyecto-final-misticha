# API Gateway

El **API Gateway** es el punto único de entrada a RaymiSupreme. Los usuarios no necesitan conocer la dirección de cada microservicio.

## Responsabilidades

- Recibir las solicitudes externas.
- Enrutar cada solicitud al servicio correspondiente.
- Validar los tokens JWT.
- Aplicar restricciones según el rol.
- Permitir el acceso público al catálogo.
- Centralizar el manejo de rutas.

## Flujo de una solicitud


Usuario → API Gateway → Microservicio → Base de datos

## Ejemplos de enrutamiento

<div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap:16px; margin: 24px 0;">

  <div style="border-left:6px solid #3949ab; background:#f5f7ff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#3949ab; margin-bottom:8px;">/api/v1/auth/**</div>
    <div><strong>Servicio destino:</strong> ms-auth</div>
  </div>

  <div style="border-left:6px solid #5e35b1; background:#faf7ff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#5e35b1; margin-bottom:8px;">/api/v1/artesanos/**</div>
    <div><strong>Servicio destino:</strong> ms-acopio</div>
  </div>

  <div style="border-left:6px solid #7b1fa2; background:#fcf7ff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#7b1fa2; margin-bottom:8px;">/api/v1/lotes/**</div>
    <div><strong>Servicio destino:</strong> ms-acopio</div>
  </div>

  <div style="border-left:6px solid #1e88e5; background:#f4faff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#1e88e5; margin-bottom:8px;">/api/v1/prendas/**</div>
    <div><strong>Servicio destino:</strong> ms-inventario</div>
  </div>

  <div style="border-left:6px solid #43a047; background:#f6fff6; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#43a047; margin-bottom:8px;">/api/v1/catalogo/**</div>
    <div><strong>Servicio destino:</strong> ms-catalogo</div>
  </div>

  <div style="border-left:6px solid #fb8c00; background:#fffaf3; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#fb8c00; margin-bottom:8px;">/api/v1/carrito/**</div>
    <div><strong>Servicio destino:</strong> ms-carrito</div>
  </div>

  <div style="border-left:6px solid #e53935; background:#fff7f7; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#e53935; margin-bottom:8px;">/api/v1/pedidos/**</div>
    <div><strong>Servicio destino:</strong> ms-ventas</div>
  </div>

  <div style="border-left:6px solid #8e24aa; background:#fcf6ff; padding:16px; border-radius:10px; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
    <div style="font-weight:bold; color:#8e24aa; margin-bottom:8px;">/api/v1/pagos/**</div>
    <div><strong>Servicio destino:</strong> ms-pagos</div>
  </div>

</div>

 "Descubrimiento de servicios"
    El API Gateway consulta **Eureka Server** para encontrar la instancia disponible de cada microservicio antes de redirigir la solicitud.
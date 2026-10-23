# Eureka Server

## ¿Qué es Eureka Server?

Eureka Server es el componente encargado del **registro y descubrimiento de microservicios** dentro de Raymi Suprême.

En una arquitectura de microservicios, cada servicio funciona de manera independiente. Por ejemplo:

- `ms-acopio`
- `ms-inventario`
- `ms-catalogo`
- `ms-carrito`
- `ms-ventas`
- `ms-pagos`

El problema es que cada microservicio puede ejecutarse en un puerto diferente, reiniciarse o cambiar de ubicación. Por eso, en lugar de que un servicio tenga que conocer manualmente la dirección exacta de otro, todos se registran en Eureka Server.

Eureka funciona como una **agenda central** donde cada microservicio dice:

> “Estoy activo, me llamo `ms-catalogo` y estoy disponible para recibir solicitudes”.

---

## Explicación sencilla

Eureka Server se puede entender como una **central de contactos** para los microservicios.

Por ejemplo, si `ms-carrito` necesita consultar información de un producto, no necesita saber directamente el puerto exacto de `ms-catalogo`.

En lugar de eso, ocurre lo siguiente:

1. `ms-catalogo` se registra en Eureka Server.
2. `ms-carrito` necesita comunicarse con `ms-catalogo`.
3. `ms-carrito` consulta a Eureka.
4. Eureka le indica dónde está disponible `ms-catalogo`.
5. `ms-carrito` puede enviar la solicitud correctamente.

---

## Funcionamiento dentro del sistema

```text
Microservicio inicia
        ↓
Se registra en Eureka Server
        ↓
Eureka guarda su nombre, IP y puerto
        ↓
API Gateway u otro microservicio consulta Eureka
        ↓
Eureka devuelve la instancia disponible
        ↓
La solicitud llega al microservicio correcto
```

---

## Vista del flujo

<div style="display:flex; flex-direction:column; align-items:center; gap:12px; margin:30px 0 50px 0;">

  <div style="background:#3949ab; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    Microservicio
    <br>
    <span style="font-weight:normal;">ms-catalogo, ms-carrito, ms-ventas, etc.</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#00897b; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    Registro en Eureka Server
    <br>
    <span style="font-weight:normal;">Nombre, IP, puerto y estado</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#1e88e5; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    API Gateway consulta Eureka
    <br>
    <span style="font-weight:normal;">Busca el servicio solicitado</span>
  </div>

  <div style="font-size:28px;">↓</div>

  <div style="background:#43a047; color:white; padding:14px 24px; border-radius:10px; width:330px; text-align:center; font-weight:bold;">
    Solicitud enviada al microservicio correcto
    <br>
    <span style="font-weight:normal;">La comunicación se realiza automáticamente</span>
  </div>

</div>

---

## ¿Por qué es necesario Eureka Server?

En un sistema distribuido, los microservicios no deben depender de direcciones fijas. Si se configura manualmente la URL de cada servicio, el sistema se vuelve difícil de mantener.

Por ejemplo, sería poco recomendable que `ms-carrito` tenga que saber directamente:

```text
http://localhost:8085/api/v1/catalogo
```

Lo correcto es que se comunique usando el nombre del servicio registrado:

```text
lb://ms-catalogo
```

De esta manera, Eureka permite que los servicios se encuentren automáticamente.

---

## Responsabilidades de Eureka Server

| Responsabilidad | Descripción |
|---|---|
| Registro de servicios | Cada microservicio se registra cuando inicia. |
| Descubrimiento de servicios | Permite encontrar un microservicio por su nombre. |
| Control de disponibilidad | Verifica si los servicios siguen activos. |
| Integración con Gateway | Permite que el API Gateway redirija solicitudes al servicio correcto. |
| Soporte para escalabilidad | Permite trabajar con varias instancias del mismo servicio. |

---

## Ejemplo aplicado en Raymi Suprême

Cuando un usuario consulta el catálogo desde el frontend, la solicitud no llega directamente a `ms-catalogo`.

El flujo es el siguiente:

```text
Frontend
   ↓
API Gateway
   ↓
Eureka Server busca ms-catalogo
   ↓
API Gateway redirige la solicitud
   ↓
ms-catalogo responde con los productos disponibles
```

Esto permite que el Gateway no dependa de una IP fija ni de un puerto escrito manualmente.

---

## Microservicios registrados en Eureka

En Raymi Suprême, los siguientes servicios se registran en Eureka Server:

| Microservicio | Función |
|---|---|
| `gateway` | Punto único de entrada al sistema. |
| `ms-auth` | Gestión de autenticación y usuarios. |
| `ms-acopio` | Gestión de artesanos y lotes. |
| `ms-inventario` | Gestión de prendas, stock e inspecciones. |
| `ms-catalogo` | Publicación de productos para venta. |
| `ms-carrito` | Gestión del carrito de compras. |
| `ms-ventas` | Gestión de pedidos y ventas. |
| `ms-pagos` | Gestión de pagos y comprobantes. |

---

## Heartbeats o latidos

Los microservicios registrados envían señales constantes a Eureka Server.  
Estas señales se conocen como **heartbeats** o **latidos**.

Su objetivo es confirmar que el microservicio sigue activo.

Si un microservicio deja de enviar latidos, Eureka puede marcarlo como no disponible. Esto evita que el Gateway o los demás servicios intenten comunicarse con una instancia caída.

---

## Beneficios dentro del proyecto

Eureka Server aporta los siguientes beneficios a Raymi Suprême:

- Permite descubrir microservicios automáticamente.
- Evita depender de direcciones IP o puertos fijos.
- Facilita la comunicación entre servicios.
- Se integra con el API Gateway.
- Permite ejecutar varias instancias de un mismo microservicio.
- Ayuda a mejorar la disponibilidad del sistema.
- Reduce el acoplamiento entre microservicios.
- Facilita el mantenimiento de la arquitectura distribuida.

---

## Lo que Eureka Server no hace

Eureka Server no procesa operaciones del negocio.

No se encarga de:

- Registrar ventas.
- Procesar pagos.
- Gestionar inventario.
- Crear productos.
- Autenticar usuarios directamente.
- Guardar datos comerciales.

Su responsabilidad principal es mantener el **registro de servicios disponibles** para que el sistema pueda comunicarse de forma dinámica.

---

## Resumen

Eureka Server es el componente que permite que los microservicios de Raymi Suprême se encuentren entre sí sin depender de direcciones fijas.

En términos simples:

> Eureka Server es como una agenda central donde cada microservicio se registra para que los demás puedan encontrarlo cuando lo necesiten.

Gracias a Eureka, el sistema puede funcionar de forma más ordenada, flexible y preparada para crecer.
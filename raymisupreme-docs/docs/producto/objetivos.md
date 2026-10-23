# Objetivos del Proyecto

## Objetivo general

Desarrollar una plataforma distribuida basada en microservicios que permita centralizar la gestión de productos textiles artesanales peruanos, desde su recepción y control de calidad hasta su publicación, venta y pago bajo la marca Raymi Suprême.

## Objetivos específicos

### Centralizar el registro de artesanos

Implementar un módulo que permita registrar a los artesanos proveedores, sus datos de contacto y su comunidad o zona de procedencia.

### Mantener la trazabilidad de los productos

Relacionar cada prenda con una categoría y con el lote del cual procede, conservando indirectamente la relación con el artesano que entregó dicho lote.

### Gestionar los lotes de adquisición

Registrar la fecha de compra, cantidad recibida, importe pagado, descripción y estado de cada lote.

### Controlar el inventario

Mantener información actualizada sobre las prendas, sus características, códigos únicos y cantidades disponibles.

### Registrar el control de calidad

Permitir que las prendas sean inspeccionadas y que el resultado y las observaciones queden almacenados antes de su comercialización.

### Crear un catálogo digital

Publicar productos con fotografía, descripción, precio, descuento y estado de disponibilidad.

### Facilitar la búsqueda de productos

Permitir la consulta del catálogo y el filtrado de productos según características como material, color y talla.

### Gestionar el proceso de compra

Implementar un carrito que permita agregar productos, modificar cantidades y convertir su contenido en un pedido.

### Mantener la consistencia de precios

Validar los productos agregados al carrito contra el catálogo para utilizar la información comercial vigente.

### Gestionar clientes y pedidos

Registrar clientes, crear pedidos, conservar sus detalles y permitir la consulta del historial de compras.

### Coordinar la confirmación de ventas

Permitir que un usuario autorizado confirme o cancele los pedidos pendientes.

### Registrar pagos y comprobantes

Crear el pago asociado a un pedido confirmado y almacenar la información necesaria para su seguimiento y comprobación.

### Actualizar el stock después de una venta

Coordinar `ms-ventas` con `ms-inventario` para reducir las existencias cuando el pedido sea confirmado correctamente.

### Implementar seguridad basada en roles

Proteger las operaciones mediante Keycloak, tokens JWT y permisos diferenciados para clientes, personal de ventas y administradores.

### Separar las responsabilidades del sistema

Dividir la plataforma en microservicios especializados para facilitar el mantenimiento y la evolución independiente de cada dominio.

### Mejorar la resiliencia

Aplicar mecanismos de tolerancia a fallos en la comunicación entre microservicios para evitar que una dependencia no disponible provoque errores descontrolados.

### Centralizar la configuración

Utilizar un servidor de configuración para administrar propiedades compartidas y configuraciones específicas por entorno.

### Incorporar observabilidad

Recolectar métricas y logs para conocer el estado de los servicios, detectar errores y analizar el comportamiento de la plataforma.

### Facilitar el despliegue

Proporcionar configuraciones con Docker Compose para ejecutar la infraestructura y los componentes necesarios en ambientes de desarrollo y producción.

## Resultados esperados

Al cumplir estos objetivos, RaymiSupreme deberá permitir:

- Conocer el origen de las prendas.
- Controlar la calidad de los productos.
- Consultar el stock disponible.
- Publicar productos de forma organizada.
- Completar el flujo desde el carrito hasta el pago.
- Restringir las operaciones según el rol del usuario.
- Supervisar la salud y comportamiento de los servicios.
- Contar con una base preparada para futuras interfaces web o móviles.
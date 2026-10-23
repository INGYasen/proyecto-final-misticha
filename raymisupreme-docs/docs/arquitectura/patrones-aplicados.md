# Patrones Aplicados

## Microservicios

El sistema está dividido según las responsabilidades del negocio: acopio, inventario, catálogo, carrito, ventas y pagos.

## API Gateway

Todas las solicitudes externas ingresan por un único punto de acceso, encargado del enrutamiento y la seguridad.

## Service Discovery

Eureka permite registrar y localizar automáticamente las instancias de los microservicios.

## Database per Service

Cada microservicio administra sus propias tablas y datos. No existen relaciones de clave foránea entre bases de datos diferentes.

## Circuit Breaker

Controla los errores producidos cuando otro microservicio no responde.

## Configuración centralizada

Config Server mantiene las configuraciones comunes y específicas de cada ambiente.

## DTO y Mapper

Los DTO transportan información entre capas y los mappers convierten entidades en objetos de respuesta.

## Repository

Los repositorios encapsulan el acceso a la base de datos mediante Spring Data JPA.
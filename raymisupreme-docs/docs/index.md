# Documentación Técnica - Raymi Suprême

## Resumen general

Raymi Suprême es una plataforma e-commerce distribuida orientada a la comercialización de prendas artesanales peruanas. El sistema está diseñado bajo una arquitectura de microservicios, permitiendo separar responsabilidades como catálogo, inventario, carrito, ventas, pagos, seguridad y monitoreo.

Esta documentación está organizada como una entrega técnica para una empresa real. Su objetivo es explicar la visión del producto, la arquitectura del sistema, los microservicios implementados, la base de datos, la seguridad, las APIs, el proceso de instalación, las pruebas realizadas y las evidencias finales del proyecto.

## Tecnologías principales

| Tecnología | Uso |
|---|---|
| Spring Boot | Desarrollo de microservicios |
| Spring Cloud Gateway | Punto único de entrada |
| Eureka Server | Descubrimiento de servicios |
| OpenFeign | Comunicación entre microservicios |
| Resilience4j | Tolerancia a fallos |
| Keycloak | Seguridad y autenticación |
| MySQL | Persistencia de datos |
| Docker | Contenedores |
| Prometheus | Métricas |
| Grafana | Dashboards de monitoreo |
| Postman | Pruebas de APIs |

## Módulos principales

- Gestión de artesanos y acopio.
- Gestión de inventario.
- Gestión del catálogo comercial.
- Gestión del carrito de compras.
- Gestión de ventas.
- Gestión de pagos.
- Seguridad con autenticación y roles.
- Observabilidad con métricas y dashboards.

## Objetivo de la documentación

Permitir que un evaluador técnico, desarrollador o administrador pueda comprender, ejecutar, probar y mantener el sistema Raymi Suprême.
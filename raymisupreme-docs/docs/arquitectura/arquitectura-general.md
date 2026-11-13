# Arquitectura general

Cada parte del negocio es un servicio. El corte C1–C4 está en [Modelo C4](modelo-c4.md).

## Componentes principales

| Componente | Función |
|---|---|
| API Gateway | Punto único de entrada |
| Config Server | Configuración centralizada |
| Eureka Server | Registro y descubrimiento de servicios |
| Keycloak | Autenticación y roles |
| Microservicios | Procesan las funciones del negocio |
| MySQL | Persistencia separada por servicio |
| Grafana, Prometheus y Loki | Monitoreo, métricas y logs |

## Vista general

```mermaid
flowchart LR
    U[Usuario] --> G[API Gateway]
    G --> K[Keycloak]
    G --> MS[Microservicios]
    MS --> DB[(Bases de datos MySQL)]
    MS --> E[Eureka Server]
    MS --> C[Config Server]
    MS --> O[Observabilidad]
```

Esta separación permite mantener y ampliar cada módulo de forma independiente.
# Orden de arranque

Si se levanta el Gateway antes que Eureka, las rutas `lb://` no resuelven.

1. Docker: MySQL, Keycloak, Kafka, Prometheus, Grafana
2. Config Server (`7071`)
3. Eureka (`7081`)
4. Microservicios
5. Gateway (`7091`)
6. Angular (`4200`)

Atajo: `.\levantar-stack-independiente.ps1` abre cada Maven en su ventana. `.\chequear-stack.ps1` lista qué puerto contestó.

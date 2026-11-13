# Conclusiones

Raymi Suprême quedó como tienda de prendas andinas con microservicios. El cliente entra por Angular, el Gateway corta lo que no lleva JWT, y el cobro pasa por Mercado Pago. El pedido nace `PENDIENTE` y el stock baja recién cuando el pago queda aprobado.

Lo que más se notó al armarlo: el orden Config Server → Eureka → servicios → Gateway. Si se cierra el IDE, Maven se cae; por eso hay un script de PowerShell que abre cada servicio en su ventana.

No es un ecommerce de producción. Es el proyecto del curso, con las piezas que pedían: gateway, descubrimiento, JWT, circuit breaker, Kafka y observabilidad.

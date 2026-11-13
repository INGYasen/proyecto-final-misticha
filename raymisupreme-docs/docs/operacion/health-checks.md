# Health

Cada servicio Spring expone `/actuator/health`. El Gateway en `http://localhost:7091/actuator/health` es el que más se mira en la demo.

Eureka es el otro chequeo: si un servicio no aparece, o aparece `DOWN`, el Gateway no lo va a balancear.

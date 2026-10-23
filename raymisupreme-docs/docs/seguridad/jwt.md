# JSON Web Token

El JWT identifica al usuario autenticado y contiene sus roles. Keycloak firma el token y los servicios verifican la firma mediante el endpoint de certificados del realm.

## Contenido simplificado

```json
{
  "sub": "identificador-del-usuario",
  "preferred_username": "admin1",
  "email": "admin@example.com",
  "roles": [
    "SUPER_ADMIN"
  ],
  "realm_access": {
    "roles": [
      "SUPER_ADMIN"
    ]
  },
  "exp": 1782810000
}
```

## Conversión de roles

El Gateway y los microservicios buscan los roles en este orden:

1. Claim personalizado `roles`.
2. Claim estándar `realm_access.roles`.

Después los convierten al formato esperado por Spring Security:

```text
SUPER_ADMIN → ROLE_SUPER_ADMIN
VENDEDORA   → ROLE_VENDEDORA
```

Por eso:

```java
hasRole("SUPER_ADMIN")
```

comprueba internamente la autoridad:

```text
ROLE_SUPER_ADMIN
```

## Validación

En desarrollo, los servicios obtienen las claves públicas desde:

```text
http://localhost:9091/realms/raymisupreme-dev/protocol/openid-connect/certs
```

En producción utilizan el realm:

```text
raymisupreme-prod
```

## Propagación entre servicios

Las llamadas OpenFeign utilizan un interceptor que copia el JWT actual:

```http
Authorization: Bearer <token>
```

Esto permite que el servicio receptor vuelva a validar la identidad y los permisos.

## Respuestas esperadas

| Código | Significado |
|---:|---|
| `401 Unauthorized` | Token ausente, inválido o vencido |
| `403 Forbidden` | Token válido, pero sin el rol requerido |
| `200–204` | Solicitud autorizada correctamente |
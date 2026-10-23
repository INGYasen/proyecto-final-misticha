# Keycloak

Keycloak es el proveedor de identidad de RaymiSupreme. Administra usuarios, credenciales, roles y emisión de tokens.

## Ambientes

| Ambiente | Realm | Puerto |
|---|---|---:|
| Desarrollo | `raymisupreme-dev` | `9091` |
| Producción | `raymisupreme-prod` | `9090` |

## Clientes configurados

| Cliente | Función |
|---|---|
| `raymisupreme-app` | Autenticación de usuarios |
| `ms-auth-admin` | Administración de usuarios desde `ms-auth` |

`ms-auth-admin` utiliza una cuenta de servicio para crear, actualizar, habilitar o eliminar usuarios dentro de Keycloak.

## Configuración relevante

- Registro público de usuarios deshabilitado.
- Inicio de sesión mediante correo permitido.
- Correos duplicados no permitidos.
- Protección contra fuerza bruta habilitada.
- Restablecimiento de contraseña habilitado.
- Duración del access token: 300 segundos.
- Protocolo utilizado: OpenID Connect.

## Obtención del token

```http
POST http://localhost:9091/realms/raymisupreme-dev/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded
```

Parámetros:

```text
grant_type=password
client_id=raymisupreme-app
client_secret=<CLIENT_SECRET>
username=<USUARIO>
password=<CONTRASEÑA>
```

## Uso del token

```http
Authorization: Bearer <ACCESS_TOKEN>
```

## Administración de usuarios

Las operaciones de `ms-auth` se encuentran en:

```text
/api/v1/auth
```

Todo el controller está protegido con:

```java
@PreAuthorize("hasRole('SUPER_ADMIN')")
```

Por lo tanto, únicamente `SUPER_ADMIN` puede administrar usuarios y asignar roles.
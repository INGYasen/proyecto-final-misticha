

# Microservicio de Autenticación

`ms-auth` administra los usuarios de RaymiSupreme y se comunica con Keycloak para gestionar la identidad y los permisos.

## Responsabilidades

- Crear usuarios.
- Consultar usuarios.
- Actualizar sus datos.
- Eliminar usuarios.
- Asignar roles.
- Integrarse con Keycloak.

## Roles principales

| Rol | Descripción |
|---|---|
| `CLIENTE` | Realiza compras |
| `VENDEDORA` | Consulta y atiende la operación |
| `SUPER_ADMIN` | Administra toda la plataforma |

## Seguridad

El usuario obtiene un token JWT desde Keycloak. Este token se envía en las solicitudes protegidas:

Authorization: Bearer <token>
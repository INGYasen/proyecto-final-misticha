# Variables

Los servicios ya arrancan en perfil `dev` y leen Config Server en `http://localhost:7071`. No hace falta exportar `SPRING_PROFILES_ACTIVE`.

La que sí hace falta para cobrar:

```
MP_ACCESS_TOKEN
```

La pone `ms-pagos`. Sin eso, Maven de pagos arranca pero el checkout falla al crear la preferencia.

Opcional:

```
MP_WEBHOOK_SECRET
INTERNAL_PAYMENT_SECRET
STRIPE_SECRET_KEY
```

Stripe no es el flujo de la tienda. Las bases de desarrollo usan `root` / `root` en cada compose.

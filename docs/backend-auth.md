# Autenticación y perfil

Este módulo cubre registro, login, recuperación de contraseña y edición de perfil.

## Endpoints

### `POST /api/users/register`

Registra un usuario, lo autentica y devuelve token + perfil.

Frontend que lo usa: [frontend/app/pages/auth/index.vue](../frontend/app/pages/auth/index.vue)

Request:

```json
{
  "username": "Ana Perez",
  "email": "ana@example.com",
  "password": "secret1234"
}
```

Response:

```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
    "username": "Ana Perez",
    "email": "ana@example.com",
    "avatarUrl": null,
    "location": null,
    "phone": null,
    "bio": null
  }
}
```

### `POST /api/users/login`

Autentica por `username` o `email` y devuelve token + perfil.

Frontend que lo usa: [frontend/app/pages/auth/index.vue](../frontend/app/pages/auth/index.vue)

Request:

```json
{
  "email": "ana@example.com",
  "password": "secret1234"
}
```

Response:

```json
{
  "token": "eyJhbGciOi...",
  "user": {
    "id": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
    "username": "Ana Perez",
    "email": "ana@example.com",
    "avatarUrl": "https://.../avatars/9d9e7f7e/avatar.png",
    "location": "Madrid",
    "phone": "+34 600 000 000",
    "bio": "Vendo ropa en buen estado"
  }
}
```

### `POST /api/users/forgot-password`

Dispara el envío del correo de recuperación.

Frontend que lo usa: [frontend/app/pages/auth/forgot.vue](../frontend/app/pages/auth/forgot.vue)

Request:

```json
{
  "email": "ana@example.com"
}
```

Response:

```json
{
  "message": "Si el correo existe, recibirás un enlace para restablecer tu contraseña."
}
```

### `POST /api/users/reset-password`

Cambia la contraseña usando el token recibido por correo.

Frontend que lo usa: [frontend/app/pages/auth/reset.vue](../frontend/app/pages/auth/reset.vue)

Request:

```json
{
  "token": "Z2VvZC1yZXNldC10b2tlbg",
  "password": "newsecret123"
}
```

Response:

```json
{
  "message": "Tu contraseña se actualizó correctamente."
}
```

### `GET /api/users`

Devuelve el listado público de usuarios.

Frontend que lo usa: [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue) y [frontend/app/pages/vendedor/[id].vue](../frontend/app/pages/vendedor/[id].vue)

Response:

```json
[
  {
    "id": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
    "username": "Ana Perez",
    "email": "ana@example.com",
    "avatarUrl": "https://...",
    "location": "Madrid",
    "phone": "+34 600 000 000",
    "bio": "Vendo ropa en buen estado"
  }
]
```

### `PUT /api/users/{id}/profile`

Actualiza perfil con `multipart/form-data` y permite subir o borrar avatar.

Frontend que lo usa: [frontend/app/components/EditProfile.vue](../frontend/app/components/EditProfile.vue)

Request form-data:

```text
username=Ana Perez
email=ana@example.com
location=Madrid
phone=+34 600 000 000
bio=Vendo ropa en buen estado
avatar=<file>
clearAvatar=true
```

Response:

```json
{
  "id": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
  "username": "Ana Perez",
  "email": "ana@example.com",
  "avatarUrl": "https://.../avatars/9d9e7f7e/avatar.png",
  "location": "Madrid",
  "phone": "+34 600 000 000",
  "bio": "Vendo ropa en buen estado"
}
```

## Servicios que intervienen

- [backend/src/main/java/com/raremarket/backend/controller/UserController.java](../backend/src/main/java/com/raremarket/backend/controller/UserController.java)
- [backend/src/main/java/com/raremarket/backend/service/UserService.java](../backend/src/main/java/com/raremarket/backend/service/UserService.java)
- [backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java](../backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java)
- [backend/src/main/java/com/raremarket/backend/service/MailService.java](../backend/src/main/java/com/raremarket/backend/service/MailService.java)
- [frontend/app/composables/useSessionUser.ts](../frontend/app/composables/useSessionUser.ts)

## Cómo funciona en el frontend

- [frontend/app/pages/auth/index.vue](../frontend/app/pages/auth/index.vue) guarda `token` y `user` en sesión local.
- [frontend/app/pages/auth/forgot.vue](../frontend/app/pages/auth/forgot.vue) muestra un mensaje neutro para evitar enumeración de usuarios.
- [frontend/app/pages/auth/reset.vue](../frontend/app/pages/auth/reset.vue) valida token y confirmación de contraseña antes de enviar.
- [frontend/app/components/EditProfile.vue](../frontend/app/components/EditProfile.vue) usa `FormData` para enviar texto y archivo en la misma request.
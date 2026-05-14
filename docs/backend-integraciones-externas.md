# Integraciones externas: Supabase y Brevo

Este documento explica cómo se conecta el backend con los servicios externos del proyecto: Supabase para base de datos y almacenamiento, y Brevo para envío de correos.

## 1. Supabase como base de datos principal

Supabase se usa como PostgreSQL gestionado. El backend Spring Boot no habla con Supabase mediante la API REST de Supabase, sino mediante JDBC/JPA contra el pooler PostgreSQL de Supabase.

### Dónde se configura

Archivo: [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties)

- `spring.datasource.url` apunta al pooler de Supabase.
- `spring.datasource.username` y `spring.datasource.password` salen de variables de entorno.
- `spring.jpa.hibernate.ddl-auto=none` deja que el esquema lo controle la base existente.

El archivo también define variables auxiliares como:

- `SUPABASE_HOST`
- `SUPABASE_PORT`
- `SUPABASE_DATABASE`
- `SUPABASE_USER`
- `SUPABASE_PASSWORD`

### Cómo se carga la configuración local

Archivo: [backend/src/main/java/com/raremarket/backend/config/DotenvConfig.java](../backend/src/main/java/com/raremarket/backend/config/DotenvConfig.java)

- Lee un archivo `.env` en desarrollo.
- Copia sus variables al sistema antes de que Spring resuelva propiedades.
- Eso permite tener credenciales fuera del código sin cambiar la app.

### Flujo de conexión

1. Spring Boot arranca.
2. `DotenvConfig` carga variables locales si existen.
3. `application.properties` resuelve la URL JDBC.
4. Hibernate/JPA crea conexiones contra el pooler de Supabase.
5. Los repositories leen y escriben datos.

### Qué parte del backend usa Supabase DB

- [backend/src/main/java/com/raremarket/backend/repository/ItemRepository.java](../backend/src/main/java/com/raremarket/backend/repository/ItemRepository.java)
- [backend/src/main/java/com/raremarket/backend/repository/UserRepository.java](../backend/src/main/java/com/raremarket/backend/repository/UserRepository.java)
- [backend/src/main/java/com/raremarket/backend/repository/OrderRepository.java](../backend/src/main/java/com/raremarket/backend/repository/OrderRepository.java)
- [backend/src/main/java/com/raremarket/backend/repository/ConversationRepository.java](../backend/src/main/java/com/raremarket/backend/repository/ConversationRepository.java)
- [backend/src/main/java/com/raremarket/backend/repository/MessageRepository.java](../backend/src/main/java/com/raremarket/backend/repository/MessageRepository.java)

### Ejemplo de conexión efectiva

Cuando el frontend llama a `GET /api/items`, el flujo real es:

1. [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) hace `$fetch`.
2. [backend/src/main/java/com/raremarket/backend/controller/ItemController.java](../backend/src/main/java/com/raremarket/backend/controller/ItemController.java) recibe la request.
3. [backend/src/main/java/com/raremarket/backend/service/ItemService.java](../backend/src/main/java/com/raremarket/backend/service/ItemService.java) construye la consulta.
4. `ItemRepository` ejecuta SQL en Supabase PostgreSQL.

## 2. Supabase Storage para imágenes y avatares

Supabase Storage se usa para archivos binarios: fotos de productos y avatar de perfil.

### Dónde se configura

Archivo: [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties)

- `SUPABASE_URL`
- `SUPABASE_SERVICE_ROLE_KEY`
- `SUPABASE_ITEM_BUCKET`
- `SUPABASE_AVATAR_BUCKET`

### Servicio que lo encapsula

Archivo: [backend/src/main/java/com/raremarket/backend/service/SupabaseStorageService.java](../backend/src/main/java/com/raremarket/backend/service/SupabaseStorageService.java)

- `uploadAvatar(file, userId)`: sube el avatar al bucket configurado.
- `uploadItemImages(files, userId)`: sube las imágenes de un artículo, con límite de 6 archivos.
- `deleteItemImagesForOwner(userId, publicUrls)`: borra solo rutas válidas del propietario.
- `deleteAvatar(storagePath)`: elimina el avatar anterior cuando se reemplaza o limpia.
- `extractStoragePathFromPublicUrl(...)`: convierte una URL pública en ruta interna para borrado.

### Flujo de subida de avatar

1. [frontend/app/components/EditProfile.vue](../frontend/app/components/EditProfile.vue) envía `FormData` con `avatar`.
2. [backend/src/main/java/com/raremarket/backend/controller/UserController.java](../backend/src/main/java/com/raremarket/backend/controller/UserController.java) recibe la imagen.
3. `SupabaseStorageService.uploadAvatar(...)` crea la ruta y hace `POST` al endpoint de Storage.
4. El backend guarda la URL pública en el usuario.
5. Si el avatar cambia, el servicio intenta borrar el anterior.

### Flujo de subida de imágenes de artículo

1. [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) manda `multipart/form-data` a `POST /api/items/images`.
2. [backend/src/main/java/com/raremarket/backend/controller/ItemController.java](../backend/src/main/java/com/raremarket/backend/controller/ItemController.java) valida autenticación.
3. `SupabaseStorageService.uploadItemImages(...)` sube cada archivo al bucket `item-images`.
4. El frontend usa las URLs devueltas para publicar o editar el artículo.
5. Si el usuario cancela o elimina archivos, `cleanupUploadedImages(...)` borra los sobrantes.

### Seguridad aplicada

- El backend usa la `service role key` de Supabase, no la anon key, porque necesita permisos de escritura y borrado.
- Las rutas que borra el backend se validan para que pertenezcan al usuario propietario.
- Los buckets se tratan como infraestructura de servidor, no como acceso directo del navegador.

## 3. Brevo para correos transaccionales

Brevo se usa para enviar el correo de recuperación de contraseña.

### Dónde se configura

Archivo: [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties)

- `brevo.api.key`
- `app.mail.sender-email`
- `app.mail.sender-name`
- `app.frontend.base-url`

### Servicio que lo encapsula

Archivo: [backend/src/main/java/com/raremarket/backend/service/MailService.java](../backend/src/main/java/com/raremarket/backend/service/MailService.java)

- `sendPasswordResetEmail(recipientEmail, resetUrl)`: construye el JSON y hace `POST` a `https://api.brevo.com/v3/smtp/email`.
- Si falta la API key o el remitente, devuelve `503 Service Unavailable`.

### Flujo de recuperación de contraseña

1. [frontend/app/pages/auth/forgot.vue](../frontend/app/pages/auth/forgot.vue) envía el email al backend.
2. [backend/src/main/java/com/raremarket/backend/controller/UserController.java](../backend/src/main/java/com/raremarket/backend/controller/UserController.java) delega en `PasswordResetService`.
3. [backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java](../backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java) crea un token temporal y arma la URL de recuperación.
4. `MailService` envía el correo con el enlace a la app frontend.
5. [frontend/app/pages/auth/reset.vue](../frontend/app/pages/auth/reset.vue) recibe el token en la query y permite cambiar la contraseña.

### Ejemplo del correo generado

El HTML incluye un enlace como este:

```text
http://localhost:3000/auth/reset?token=TOKEN_TEMPORAL
```

En producción, el dominio sale de `app.frontend.base-url`.

### Qué parte del frontend ve el usuario

- [frontend/app/pages/auth/forgot.vue](../frontend/app/pages/auth/forgot.vue) confirma el envío.
- [frontend/app/pages/auth/reset.vue](../frontend/app/pages/auth/reset.vue) valida el token y actualiza la contraseña.

## 4. Resumen de integración

- Supabase DB: persistencia relacional del sistema.
- Supabase Storage: subida y borrado de imágenes y avatares.
- Brevo: correo transaccional de recuperación de contraseña.

## 5. Puntos de entrada clave

- [backend/src/main/java/com/raremarket/backend/config/DotenvConfig.java](../backend/src/main/java/com/raremarket/backend/config/DotenvConfig.java)
- [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties)
- [backend/src/main/java/com/raremarket/backend/service/SupabaseStorageService.java](../backend/src/main/java/com/raremarket/backend/service/SupabaseStorageService.java)
- [backend/src/main/java/com/raremarket/backend/service/MailService.java](../backend/src/main/java/com/raremarket/backend/service/MailService.java)
- [backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java](../backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java)

## 6. Variables de entorno necesarias

### Backend

```env
# Base de datos Supabase
SUPABASE_USER=postgres
SUPABASE_PASSWORD=tu-password
SUPABASE_HOST=aws-1-eu-west-1.pooler.supabase.com
SUPABASE_PORT=6543
SUPABASE_DATABASE=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:6543/postgres?user=${SUPABASE_USER}&password=${SUPABASE_PASSWORD}&prepareThreshold=0&preferQueryMode=simple

# Seguridad y frontend
JWT_SECRET=cambia-este-secreto
JWT_TTL_SECONDS=86400
FRONTEND_BASE_URL=http://localhost:3000

# Brevo
BREVO_API_KEY=tu_api_key_de_brevo
BREVO_SENDER_EMAIL=no-reply@tu-dominio.com
BREVO_SENDER_NAME=Closely

# Supabase Storage
SUPABASE_URL=https://tu-proyecto.supabase.co
SUPABASE_SERVICE_ROLE_KEY=tu_service_role_key
SUPABASE_ITEM_BUCKET=item-images
SUPABASE_AVATAR_BUCKET=avatars
```

### Frontend

```env
NUXT_PUBLIC_API_BASE_URL=http://localhost:8081/api
NUXT_PUBLIC_SUPABASE_URL=https://tu-proyecto.supabase.co
NUXT_PUBLIC_SUPABASE_ANON_KEY=tu_anon_key
```

### Quién usa cada variable

- `SPRING_DATASOURCE_URL` y `SUPABASE_*`: [backend/src/main/resources/application.properties](../backend/src/main/resources/application.properties) y [backend/src/main/java/com/raremarket/backend/config/DotenvConfig.java](../backend/src/main/java/com/raremarket/backend/config/DotenvConfig.java).
- `BREVO_API_KEY`, `BREVO_SENDER_EMAIL` y `BREVO_SENDER_NAME`: [backend/src/main/java/com/raremarket/backend/service/MailService.java](../backend/src/main/java/com/raremarket/backend/service/MailService.java).
- `FRONTEND_BASE_URL`: [backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java](../backend/src/main/java/com/raremarket/backend/service/PasswordResetService.java).
- `NUXT_PUBLIC_API_BASE_URL`: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) y el resto de llamadas `$fetch` del frontend.
- `NUXT_PUBLIC_SUPABASE_URL` y `NUXT_PUBLIC_SUPABASE_ANON_KEY`: [frontend/app/composables/useSupabaseClient.ts](../frontend/app/composables/useSupabaseClient.ts).
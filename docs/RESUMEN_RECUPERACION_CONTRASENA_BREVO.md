# Resumen: Recuperación de contraseña con Brevo

## Objetivo
Implementar un flujo de "olvidé mi contraseña" para el proyecto, usando Brevo como canal de envío de correo.

## Lo que ya se hizo

### Backend
- Se añadió persistencia para tokens de recuperación en la base de datos con expiración y uso único.
- Se crearon los endpoints:
  - `POST /api/users/forgot-password`
  - `POST /api/users/reset-password`
- Se implementó un servicio de correo que usa la API de Brevo para enviar el enlace de recuperación.
- Se mantuvo la compatibilidad con el sistema actual de autenticación y hash de contraseñas.
- Se ajustó el manejo de errores para mostrar mejor por qué falla el envío de correo.

### Frontend
- Se añadió la pantalla de solicitud de recuperación.
- Se añadió la pantalla para definir una nueva contraseña.
- Se añadió el enlace "Olvidé mi contraseña" en la vista de acceso.
- Se ajustó el layout para que las rutas de auth no muestren header/footer del sitio.

### Documentación y configuración
- Se añadió una plantilla `.env.example` para desarrollo local.
- Se limpió `application.properties` para leer secretos desde variables de entorno.
- Se preparó el proyecto para que el correo funcione sin depender de SMTP.

## Configuración necesaria

Para que funcione en local debes definir:

- `BREVO_API_KEY`
- `BREVO_SENDER_EMAIL`
- `BREVO_SENDER_NAME`
- `FRONTEND_BASE_URL`

## Lo que debes tener en cuenta

- El correo remitente debe estar verificado en Brevo.
- Si Brevo bloquea la petición, el error suele venir por IP no autorizada, remitente no verificado o API key inválida.
- No conviene dejar claves reales dentro de `application.properties`.
- Si el proyecto se usa como demo o TFG, basta con un sender verificado sin dominio propio.

## Estado actual

- El backend compila correctamente.
- El frontend compila correctamente.
- El flujo está montado y listo para ajustar la cuenta de Brevo.

## Pendientes recomendados

- Rotar cualquier clave expuesta en el entorno de Brevo.
- Verificar el sender en Brevo antes de hacer pruebas finales.
- Si el correo sigue fallando, revisar la respuesta exacta de Brevo en los logs del backend.

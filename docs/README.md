# Tienda RareMarket

Proyecto dividido en dos aplicaciones independientes:

- Frontend: Nuxt (carpeta `frontend`)
- Backend: Spring Boot (carpeta `backend`)

## 1) Frontend (Nuxt)

### Configuración

1. Copia `frontend/.env.example` a `frontend/.env`.
2. Ajusta la URL del backend:

```bash
NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

### Ejecutar

Desde la carpeta `frontend`:

```bash
npm install
npm run dev
```

Frontend en: `http://localhost:3000`

## 2) Backend (Spring Boot)

### Configuración

1. Copia `backend/.env.example` a `backend/.env`.
2. Configura tus credenciales de Supabase y los orígenes CORS permitidos.

Variables clave:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://.../postgres?sslmode=require&user=...&password=...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

Si necesitas más de un frontend:

```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://tu-dominio.com
```

### Ejecutar

Desde la carpeta `backend`:

```bash
mvnw.cmd spring-boot:run
```

Backend en: `http://localhost:8080`

## Arquitectura Separada

La separación frontend/backend queda definida por entorno:

- Frontend consume la API mediante `NUXT_PUBLIC_API_BASE_URL`.
- Backend expone CORS mediante `CORS_ALLOWED_ORIGINS`.
- No hay dependencias hardcodeadas de dominio entre ambos.

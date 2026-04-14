# Guía: Conectar Backend con Supabase

## ¿Qué es Supabase?
Supabase es una alternativa de Firebase basada en PostgreSQL. Como tu aplicación ya usa PostgreSQL, es la opción perfecta.

## Pasos completados en el proyecto

### 1. ✅ Configuración automática realizada:
- `application.properties` actualizado para usar variables de entorno
- Dependencia `dotenv-java` agregada a `pom.xml`
- Archivo `DotenvConfig.java` creado para cargar variables de entorno
- Archivos `.env` y `.env.example` creados

## Próximos pasos: Configuración manual

### 1. Crear proyecto en Supabase
1. Ve a [supabase.com](https://supabase.com)
2. Registrate/Inicia sesión
3. Crea un nuevo proyecto
4. Espera a que se despliegue (5-15 minutos)

### 2. Obtener credenciales
En tu proyecto de Supabase:
1. Ve a **Settings** → **Database**
2. Copia estos valores:
   - **Host**: `xxx.supabase.co`
   - **Port**: `5432`
   - **Database**: `postgres`
   - **User**: `postgres`
   - **Password**: (la que configuraste)

### 3. Configurar archivo `.env` local
Edita `backend/.env` con tus credenciales:

```env
SUPABASE_HOST=your-project.supabase.co
SUPABASE_PORT=5432
SUPABASE_DATABASE=postgres
SUPABASE_USER=postgres
SUPABASE_PASSWORD=tu-contraseña-super-segura
SSLMODE=require
```

### 4. Compilar y ejecutar

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## Características de Supabase que complementan tu proyecto

### Autenticación (Supabase Auth)
Puedes usar Supabase Auth para gestionar usuarios:
- Registro e inicio de sesión
- Autenticación con OAuth (Google, GitHub, etc.)
- Recuperación de contraseña

**Para integrarlo en tu backend**, agregar en pom.xml:
```xml
<dependency>
    <groupId>io.supabase</groupId>
    <artifactId>supabase-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Almacenamiento de archivos (Supabase Storage)
Perfecto para guardar fotos de items. Usar API REST o SDK de Java.

### Realtime
Supabase proporciona actualizaciones en tiempo real para cambios en la BD.

## Solución de problemas

### Error: "could not translate host name to address"
- Verifica que el `SUPABASE_HOST` sea correcto (sin protocolo)
- Asegúrate de tener internet activo

### Error: "password authentication failed"
- Verifica que `SUPABASE_PASSWORD` sea exacta
- Evita caracteres especiales no escapados en el password

### Error de SSL
- Asegúrate de que `SSLMODE=require` esté en `.env`

## Estructura de carpetas local vs Supabase

**Antes (Local PostgreSQL):**
```
localhost:5432/raremarket
├── usuarios (tabla)
├── items (tabla)
└── ...
```

**Ahora (Supabase):**
```
tu-proyecto.supabase.co/postgres
├── public (schema)
│   ├── usuarios
│   ├── items
│   └── ...
└── auth (schema - para autenticación)
```

## Variables de entorno por entorno

### Desarrollo (local)
- Archivo `.env` carga automáticamente
- `sslmode=disable` funciona en localhost

### Producción
- Establecer variables de entorno en tu servidor/plataforma (Heroku, Railway, AWS, etc.)
- **Nunca** incluyas `.env` en producción
- Usa `sslmode=require`

## Recursos útiles

- [Docs de Supabase](https://supabase.com/docs)
- [Documentación de Spring Boot + PostgreSQL](https://spring.io/projects/spring-data-jpa)
- [Gestión de BD con Supabase](https://supabase.com/docs/guides/database)

## Próximos pasos recomendados

1. **Migrar datos locales a Supabase** (si tienes datos existentes)
2. **Configurar autenticación** en el frontend (Nuxt) con Supabase Auth
3. **Agregar validación CORS** en tu backend para permitir requests desde el frontend
4. **Implementar Storage** para fotos de items

---

**Última actualización**: Marzo 2025


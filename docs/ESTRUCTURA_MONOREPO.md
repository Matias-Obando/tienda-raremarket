# Estructura del Monorepo

- **backend/**: Proyecto Java Spring Boot (API, lógica de negocio, conexión a Supabase)
- **frontend/**: Proyecto Nuxt 3 (Vue, Tailwind, lógica de cliente)
- **docs/**: Documentación en Markdown

## Cómo migrar archivos

1. Mueve todos los archivos y carpetas del backend (Java, pom.xml, mvnw, etc.) a la carpeta `backend/`.
2. Mueve todos los archivos y carpetas del frontend (Nuxt, Vue, Tailwind, etc.) a la carpeta `frontend/`.
3. Mueve todos los archivos `.md` de documentación a la carpeta `docs/`.

## Recomendación

Actualiza los paths en tus scripts, configuraciones y documentación para reflejar la nueva estructura.
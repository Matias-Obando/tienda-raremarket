# ⚡ Referencia Rápida: Supabase + Spring Boot

## Comandos esenciales

```powershell
# Compilar backend
cd backend
mvn clean install

# Ejecutar backend
mvn spring-boot:run

# Ejecutar frontend
npm run dev

# Ejecutar tests
mvn test

# Limpiar caché
mvn clean
```

## Variables de entorno necesarias

```env
SUPABASE_HOST=your-project.supabase.co
SUPABASE_PORT=5432
SUPABASE_DATABASE=postgres
SUPABASE_USER=postgres
SUPABASE_PASSWORD=your-password
SSLMODE=require
```

## URLs locales

```
Frontend:  http://localhost:3000
Backend:   http://localhost:8080
API:       http://localhost:8080/api/...
Database:  tu-proyecto.supabase.co:5432
```

## Endpoints ejemplo

```
GET    http://localhost:8080/api/items
GET    http://localhost:8080/api/items/1
POST   http://localhost:8080/api/items
PUT    http://localhost:8080/api/items/1
DELETE http://localhost:8080/api/items/1
```

## Estructura de carpetas clave

```
backend/
  ├── .env ........................ Credenciales (NO en Git)
  ├── src/main/java/.../
  │   ├── config/ ............... Configuraciones
  │   ├── controller/ ........... APIs REST
  │   ├── service/ .............. Lógica
  │   ├── repository/ ........... BD
  │   ├── model/ ................ Entidades
  │   └── dto/ .................. Transfer Objects
  └── pom.xml ................... Dependencias

app/ (Nuxt)
  ├── pages/ .................... Rutas
  ├── components/ ............... Componentes Vue
  └── assets/ ................... CSS/imágenes
```

## Archivo .env (ejemplo)

```env
# Supabase
SUPABASE_HOST=my-project.supabase.co
SUPABASE_PORT=5432
SUPABASE_DATABASE=postgres
SUPABASE_USER=postgres
SUPABASE_PASSWORD=my-secure-password
SSLMODE=require
```

## Pasos rápidos para nuevos desarrolladores

1. Clona el repo
2. `cd backend`
3. Copia `.env.example` a `.env`
4. Llena `.env` con credenciales de Supabase
5. `mvn clean install`
6. `mvn spring-boot:run`
7. En otra terminal: `npm run dev`
8. Abre `http://localhost:3000`

## Testing con curl

```powershell
# GET
Invoke-RestMethod -Uri "http://localhost:8080/api/items" -Method Get

# POST
$body = @{
    nombre = "Producto"
    precio = 99.99
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/items" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body

# DELETE
Invoke-RestMethod -Uri "http://localhost:8080/api/items/1" -Method Delete
```

## Archivo .gitignore (asegurar que incluya)

```
.env
.env.local
.env.*.local
node_modules/
target/
.idea/
.vscode/
*.class
```

## Instalación de dependencias

```bash
# Backend (Maven)
mvn clean install

# Frontend (npm)
npm install
npm run dev

# O si usas yarn
yarn install
yarn dev
```

## Principales dependencias

**Backend (Spring Boot)**:
- `spring-boot-starter-data-jpa` - ORM
- `spring-boot-starter-webmvc` - REST APIs
- `postgresql` - Driver JDBC
- `dotenv-java` - Cargar .env
- `lombok` - Boilerplate

**Frontend (Nuxt)**:
- `nuxt` - Framework
- `vue` - Componentes
- `typescript` - Type safety

## Error común: "could not translate host name"

**Causa**: SUPABASE_HOST incluye protocolo o está incorrecto

**Solución**: 
```
❌ SUPABASE_HOST=postgresql://my-project.supabase.co
✅ SUPABASE_HOST=my-project.supabase.co
```

## Error común: "password authentication failed"

**Causa**: Contraseña incorrecta o mal escapada

**Solución**:
- Verifica contraseña en Supabase exactamente igual
- Si tiene caracteres especiales, úsalos tal cual (no escape)
- Copia desde Supabase directamente

## Debugging

```powershell
# Ver logs del backend (terminal debe estar abierta)
# Los logs aparecen cuando ejecutas: mvn spring-boot:run

# Ver logs del frontend
# Abrir Developer Tools en navegador (F12)
# Ir a "Console" tab
```

## Resumen arqutectura

```
User → Nuxt Frontend (Vue) → Spring Boot Backend → Supabase DB
```

- **Frontend**: Nuxt.js en `http://localhost:3000`
- **Backend**: Spring Boot en `http://localhost:8080`
- **Database**: PostgreSQL en Supabase (cloud)
- **Comunicación**: HTTP REST API + JDBC

## Archivos más importantes para mantener actualizados

1. `backend/src/main/resources/application.properties` - Configuración general
2. `backend/pom.xml` - Dependencias del backend
3. `backend/.env` - Credenciales locales
4. `nuxt.config.ts` - Configuración de Nuxt
5. `package.json` - Dependencias de frontend

---

📖 Para documentación completa: Revisa archivos .md en la raíz del proyecto


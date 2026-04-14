# Diagrama de Arquitectura: Supabase + Spring Boot + Nuxt

## Arquitectura General

```
┌─────────────────────────────────────────────────────────────────────┐
│                     INTERNET PÚBLICA                               │
└─────────────────────────────────────────────────────────────────────┘
              │                           │
              │                           │
       ┌──────▼──────┐           ┌────────▼────────┐
       │  Frontend    │           │  Backend API    │
       │  Nuxt.js     │◄─────────►│  Spring Boot    │
       │              │  HTTP/REST│  Java 21        │
       │ localhost    │           │ localhost       │
       │ :3000        │           │ :8080           │
       └──────┬──────┘           └────────┬────────┘
              │                          │
              │                          │
              │         ┌────────────────▼──────────────┐
              │         │  PostgreSQL Database         │
              │         │  Supabase (Cloud)            │
              │         │  tu-proyecto.supabase.co     │
              │         │  :5432                       │
              │         └──────────────────────────────┘
              │
              └──► Guarda fotos/archivos en
                   Supabase Storage (opcional)
```

## Configuración de variables de entorno

```
┌──────────────────────────────────────────────┐
│         archivo: backend/.env                │
├──────────────────────────────────────────────┤
│ SUPABASE_HOST=tu-proyecto.supabase.co        │
│ SUPABASE_PORT=5432                           │
│ SUPABASE_DATABASE=postgres                   │
│ SUPABASE_USER=postgres                       │
│ SUPABASE_PASSWORD=tu-contraseña              │
│ SSLMODE=require                              │
└──────────────────────────────────────────────┘
                     ▼
         ┌──────────────────────────┐
         │  DotenvConfig.java       │
         │  Carga al iniciar App    │
         └──────────────────────────┘
                     ▼
         ┌──────────────────────────┐
         │  application.properties  │
         │  Spring Boot obtiene     │
         │  credenciales de BD      │
         └──────────────────────────┘
                     ▼
         ┌──────────────────────────┐
         │  Hibernate/JPA           │
         │  Conecta a Supabase      │
         └──────────────────────────┘
```

## Flujo de una petición del usuario

```
Usuario interactúa con Frontend (Nuxt)
         │
         ▼
    [Frontend Nuxt]
    - Vue.js component hace click
    - fetch() o axios request
    
         │ POST http://localhost:8080/api/items
         │
         ▼
    [Backend Spring Boot]
    - Controller recibe request
    - @PostMapping("/api/items")
    - Valida datos
    
         │
         ▼
    [Service]
    - Lógica de negocio
    - itemService.crearItem(...)
    
         │
         ▼
    [Repository (JPA)]
    - itemRepository.save(item)
    - Spring genera SQL
    
         │ INSERT INTO items (...)
         │
         ▼
    [Supabase PostgreSQL]
    - Ejecuta query
    - Guarda en BD
    
         │ Respuesta
         ▼
    [Response]
    - 201 Created
    - {"id": 123, "nombre": "..."}
    
         │
         ▼
    [Frontend actualiza]
    - Muestra item en pantalla
```

## Estructura de carpetas del Backend

```
backend/
│
├── .env .......................... Credenciales (local, NO en Git)
├── .env.example .................. Plantilla (incluir en Git)
├── .gitignore .................... Debe incluir .env
│
├── pom.xml ....................... Dependencias Maven
│   └── dotenv-java (agregada)
│
└── src/main/java/com/raremarket/backend/
    │
    ├── BackendApplication.java ... Clase main
    │
    ├── config/ ................... Configuraciones globales
    │   ├── DotenvConfig.java .... Carga .env ✅
    │   └── CorsConfig.java ...... Permite requests de Nuxt ✅
    │
    ├── controller/ ............... REST endpoints
    │   ├── ItemController.java
    │   ├── UserController.java
    │   └── ...
    │
    ├── service/ .................. Lógica de negocio
    │   ├── ItemService.java
    │   ├── UserService.java
    │   └── ...
    │
    ├── repository/ ............... Acceso a datos (JPA)
    │   ├── ItemRepository.java
    │   ├── UserRepository.java
    │   └── ...
    │
    ├── model/ .................... Entidades JPA
    │   ├── Item.java
    │   ├── User.java
    │   └── ...
    │
    ├── dto/ ...................... Data Transfer Objects
    │   ├── ItemDTO.java
    │   └── ...
    │
    └── exception/ ................ Excepciones personalizadas
        ├── ItemNotFoundException.java
        └── ...
```

## Diferencia: Local vs Supabase

### ANTES (PostgreSQL Local)
```
┌─────────────────┐
│  Tu computadora │
├─────────────────┤
│ PostgreSQL      │
│ localhost:5432  │
│ usuario: postgres
│ bd: raremarket
└─────────────────┘
   Solo funciona localmente
   Datos se pierden si reinstalaas
```

### AHORA (Supabase Cloud)
```
┌─────────────────┐
│  Supabase Cloud │
├─────────────────┤
│ PostgreSQL      │
│ tu-proyecto.    │
│ supabase.co:5432│
│ usuario: postgres
│ bd: postgres
└─────────────────┘
   Accesible desde cualquier lugar
   Backups automáticos
   Seguridad profesional
   Fácil despliegue
```

## Comandos importantes

```bash
# En Windows (PowerShell)
cd backend

# Limpiar y compilar
mvn clean install

# Ejecutar backend
mvn spring-boot:run

# Ejecutar tests
mvn test

# Generar JAR
mvn package
```

## Checklist de Configuración

```
[ ] Crear proyecto en Supabase
[ ] Obtener credenciales
[ ] Editar backend/.env con credenciales
[ ] Ejecutar mvn clean install
[ ] Ejecutar mvn spring-boot:run
[ ] Backend conecta a Supabase ✓
[ ] Desde Nuxt, hacer petición a http://localhost:8080/api/...
[ ] Verificar datos en Supabase Dashboard
```

---

Para más detalles, revisa: `SUPABASE_SETUP.md`


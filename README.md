# ¿Cómo arrancar el frontend?

1. Entra a la carpeta del frontend:

   ```bash
   cd frontend
   ```

2. Instala las dependencias:

   ```bash
   npm install
   ```

3. Arranca el servidor de desarrollo:

   ```bash
   npm run dev
   ```

Esto levantará el proyecto Nuxt 3 en modo desarrollo. Por defecto suele estar en http://localhost:3000

---

# ¿Cómo arrancar el backend?

1. Entra a la carpeta del backend:

   ```bash
   cd backend
   ```

2. Arranca el backend con Maven Wrapper:

   ```bash
   ./mvnw spring-boot:run
   ```
   o en Windows:
   ```bat
   mvnw.cmd spring-boot:run
   ```

Esto levantará el backend Java Spring Boot.

---

# Notas
- Asegúrate de tener Node.js y Java instalados.
- Si tienes dudas, revisa la documentación en la carpeta `docs/`.

# 📋 Checklist: Configurar Supabase paso a paso

## FASE 1: Preparación en Supabase (10 minutos)

- [ ] **1.1** Ir a https://supabase.com
- [ ] **1.2** Crear cuenta o iniciar sesión
- [ ] **1.3** Hacer clic en "New Project"
- [ ] **1.4** Completar formulario:
  - Nombre del proyecto: `tienda-raremarket`
  - Región: Elegir la más cercana a tu ubicación
  - Password: Guardar en lugar seguro
- [ ] **1.5** Hacer clic en "Create new project"
- [ ] **1.6** Esperar a que se despliegue (5-15 minutos)
- [ ] **1.7** Cuando esté listo, ir a **Settings → Database**
- [ ] **1.8** Copiar y guardar:
  - [ ] Host: `xxx.supabase.co`
  - [ ] Database: `postgres`
  - [ ] Port: `5432`
  - [ ] User: `postgres`
  - [ ] Password: (la que configuraste)
  - [ ] Connection string: `postgresql://postgres:password@xxx.supabase.co:5432/postgres`

---

## FASE 2: Configurar Backend Local (5 minutos)

- [ ] **2.1** Abrir terminal PowerShell
- [ ] **2.2** Navegar a la carpeta backend:
  ```powershell
  cd C:\Users\esaul\OneDrive\Documents\tienda-raremarket\backend
  ```

- [ ] **2.3** Editar archivo `.env` con tus credenciales:
  - Abrir `backend/.env` en tu editor
  - Reemplazar `tu-proyecto` con tu SUPABASE_HOST
  - Reemplazar `tu-contraseña` con tu SUPABASE_PASSWORD
  - Guardar archivo

Contenido esperado en `.env`:
```env
SUPABASE_HOST=tu-proyecto.supabase.co
SUPABASE_PORT=5432
SUPABASE_DATABASE=postgres
SUPABASE_USER=postgres
SUPABASE_PASSWORD=tu-contraseña-segura
SSLMODE=require
```

- [ ] **2.4** Verificar que el archivo no esté en `.gitignore` violando privacidad
- [ ] **2.5** Asegurarse de que `.env` esté en `.gitignore`

---

## FASE 3: Compilar Backend (5 minutos)

- [ ] **3.1** Estar en carpeta `backend/`
- [ ] **3.2** Ejecutar:
  ```powershell
  mvn clean install
  ```
  ⏳ Esto tardará 2-5 minutos (descarga dependencias)

- [ ] **3.3** Verificar que no haya ERRORES (rojo en consola)
  - Si hay errores: Revisar mensaje y contactar

- [ ] **3.4** Cuando termine, debería decir: `BUILD SUCCESS`

---

## FASE 4: Iniciar Backend (2 minutos)

- [ ] **4.1** En la misma terminal, ejecutar:
  ```powershell
  mvn spring-boot:run
  ```

- [ ] **4.2** Esperar a que aparezca:
  ```
  Tomcat started on port(s): 8080 (http)
  ```

- [ ] **4.3** Si aparece ese mensaje: ✅ **BACKEND CONECTADO A SUPABASE**

---

## FASE 5: Probar Conexión (5 minutos)

### Opción A: Postman (más fácil)

- [ ] **5.1** Descargar Postman: https://www.postman.com/downloads/
- [ ] **5.2** Abrir Postman
- [ ] **5.3** Crear nueva request:
  - Tipo: `GET`
  - URL: `http://localhost:8080/api/items`
  - Hacer clic en "Send"

- [ ] **5.4** Debería responder:
  - Status: `200 OK`
  - Body: `[]` (lista vacía de items)

### Opción B: cURL en PowerShell

- [ ] **5.1** Abrir otra terminal PowerShell
- [ ] **5.2** Ejecutar:
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/api/items" -Method Get
  ```

- [ ] **5.3** Debería responder: `@()` o `[]`

### Opción C: Navegar en navegador

- [ ] **5.1** Abrir navegador
- [ ] **5.2** Ir a: `http://localhost:8080/api/items`
- [ ] **5.3** Debería mostrar JSON vacío: `[]`

---

## FASE 6: Verificar en Supabase Dashboard (3 minutos)

- [ ] **6.1** Ir a https://supabase.com
- [ ] **6.2** Abrir tu proyecto
- [ ] **6.3** Ir a **"Table Editor"** en el menú lateral
- [ ] **6.4** Expandir la tabla `items` (si ya existe)
- [ ] **6.5** Ver que está conectada correctamente

---

## FASE 7: Preparar Frontend Nuxt (5 minutos)

- [ ] **7.1** Abrir otra terminal PowerShell
- [ ] **7.2** Navegar a carpeta raíz:
  ```powershell
  cd C:\Users\esaul\OneDrive\Documents\tienda-raremarket
  ```

- [ ] **7.3** Instalar dependencias:
  ```powershell
  npm install
  ```

- [ ] **7.4** Iniciar frontend:
  ```powershell
  npm run dev
  ```

- [ ] **7.5** Debería mostrar:
  ```
  NUXT server listening on http://localhost:3000
  ```

---

## FASE 8: Probar comunicación Frontend ↔ Backend (5 minutos)

- [ ] **8.1** Abrir navegador: `http://localhost:3000`
- [ ] **8.2** Abrir consola del navegador (F12)
- [ ] **8.3** Ir a pestaña "Network"
- [ ] **8.4** Hacer una acción en el frontend que llamar al API
- [ ] **8.5** Verificar que el request llegue a `localhost:8080`
- [ ] **8.6** Ver status `200` o `201` (éxito)

---

## FASE 9: Crear datos de prueba (5 minutos)

### Opción 1: Postman

```
POST http://localhost:8080/api/items
Content-Type: application/json

{
  "nombre": "Laptop Gaming",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1299.99,
  "urlImagen": "https://ejemplo.com/laptop.jpg",
  "nombreVendedor": "Juan Pérez",
  "categoria": "electrónica"
}
```

- [ ] **9.1** Hacer POST con este JSON
- [ ] **9.2** Debería responder con Status `201`
- [ ] **9.3** Ver el ID del nuevo item

### Opción 2: PowerShell

```powershell
$body = @{
    nombre = "Laptop Gaming"
    descripcion = "Laptop de alto rendimiento"
    precio = 1299.99
    urlImagen = "https://ejemplo.com/laptop.jpg"
    nombreVendedor = "Juan Pérez"
    categoria = "electrónica"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/items" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

- [ ] **9.4** Ejecutar comando
- [ ] **9.5** Ver respuesta con ID generado

---

## FASE 10: Verificar datos en Supabase (2 minutos)

- [ ] **10.1** Ir a https://supabase.com
- [ ] **10.2** Abrir tu proyecto → **Table Editor**
- [ ] **10.3** Abrir tabla `items`
- [ ] **10.4** ✅ **Ver tus datos insertados desde Spring Boot**

---

## 🎉 ¡LISTO!

Completaste todo el checklist. Tu arquitectura está:

```
✅ Frontend Nuxt (localhost:3000)
   ↓ HTTP
✅ Backend Spring Boot (localhost:8080)
   ↓ JDBC
✅ Supabase PostgreSQL (Cloud)
```

---

## 🆘 Solución de problemas

| Problema | Solución |
|----------|----------|
| `Could not translate host name` | Verifica SUPABASE_HOST en .env, sin protocolo |
| `Password authentication failed` | Revisa SUPABASE_PASSWORD, sin caracteres especiales sin escapar |
| `Connection refused` | Asegúrate que Supabase está desplegado, no es timeout |
| `CORS error` | Verifica CorsConfig.java está cargando, y origen correcto |
| `404 en /api/items` | El Controller no está mapeado, crea ItemController.java |
| `500 Internal Server Error` | Ver logs del backend en terminal mvn |

---

## 📞 Recursos

- 📖 [Documentación Supabase](https://supabase.com/docs)
- 📖 [Documentación Spring Boot](https://spring.io/projects/spring-boot)
- 📖 [Maven](https://maven.apache.org/)
- 📖 [Nuxt.js](https://nuxt.com/)

---

**Tiempo total estimado**: 45 minutos (la mayoría es esperar a que compile/despliegue)

¡Mucho éxito! 🚀


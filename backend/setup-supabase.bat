@echo off
REM Script para facilitar la conexión con Supabase
REM Uso: ejecuta este script y sigue las instrucciones

echo.
echo ======================================
echo  Asistente de Configuración Supabase
echo ======================================
echo.

REM Verificar si existe .env
if not exist ".env" (
    echo [!] No se encontró archivo .env
    echo [i] Creando .env desde .env.example...
    copy ".env.example" ".env" >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] No se pudo crear .env
        exit /b 1
    )
    echo [OK] .env creado exitosamente
) else (
    echo [OK] Archivo .env encontrado
)

echo.
echo [i] Archivos necesarios:
echo     - backend/.env (credenciales de Supabase)
echo     - backend/.env.example (referencia)
echo.

echo [i] Próximos pasos:
echo     1. Edita backend/.env con tus credenciales de Supabase
echo     2. Ejecuta: mvn clean install
echo     3. Ejecuta: mvn spring-boot:run
echo.

echo [i] Para obtener tus credenciales:
echo     1. Ve a https://supabase.com
echo     2. Crea/abre tu proyecto
echo     3. Settings -> Database
echo     4. Copia Host, Password, etc.
echo.

pause


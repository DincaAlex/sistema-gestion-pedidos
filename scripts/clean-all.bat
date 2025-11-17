@echo off
echo ====================================
echo Limpieza completa de Docker
echo ====================================
echo.
echo ADVERTENCIA: Este script realizara las siguientes acciones:
echo   1. Detener todos los contenedores
echo   2. Eliminar todos los contenedores
echo   3. Eliminar todos los volumenes (bases de datos, configuraciones, etc.)
echo   4. Eliminar redes no utilizadas
echo   5. Eliminar las imagenes del proyecto
echo.
echo Esto eliminara TODOS los datos almacenados en las bases de datos.
echo.

set /p CONFIRM="Estas seguro de continuar? (S/N): "
if /i not "%CONFIRM%"=="S" (
    echo.
    echo [INFO] Operacion cancelada por el usuario.
    pause
    exit /b 0
)

echo.
echo ====================================
echo Iniciando limpieza...
echo ====================================
echo.

REM Verificar si Docker esta corriendo
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta corriendo. Por favor inicia Docker Desktop.
    pause
    exit /b 1
)

echo [PASO 1/5] Deteniendo contenedores...
docker-compose down
echo.

echo [PASO 2/5] Eliminando contenedores...
docker-compose rm -f
echo.

echo [PASO 3/5] Eliminando volumenes...
docker-compose down -v
echo.

echo [PASO 4/5] Eliminando imagenes del proyecto...
docker rmi -f config-server:latest 2>nul
docker rmi -f registry-service:latest 2>nul
docker rmi -f ms-productos:latest 2>nul
docker rmi -f ms-productos-v2:latest 2>nul
docker rmi -f ms-productos-writer:latest 2>nul
docker rmi -f ms-pedidos:latest 2>nul
docker rmi -f gateway-service:latest 2>nul
docker rmi -f oauth-server:latest 2>nul
docker rmi -f resource-server:latest 2>nul
docker rmi -f oauth-client:latest 2>nul
echo.

echo [PASO 5/5] Limpiando recursos no utilizados...
docker system prune -f
echo.

echo ====================================
echo Limpieza completa finalizada
echo ====================================
echo.
echo Para reconstruir el proyecto ejecuta:
echo   1. Primero construye las imagenes con los scripts build-*.bat
echo   2. Luego inicia los contenedores con start-containers.bat
echo.

pause

@echo off
echo ====================================
echo Deteniendo contenedores Docker
echo ====================================
echo.

REM Verificar si Docker esta corriendo
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta corriendo. Por favor inicia Docker Desktop.
    pause
    exit /b 1
)

echo [INFO] Deteniendo todos los contenedores...
docker-compose stop

if errorlevel 1 (
    echo [ERROR] Fallo al detener los contenedores.
    pause
    exit /b 1
)

echo.
echo ====================================
echo Contenedores detenidos exitosamente
echo ====================================
echo.
echo Para ver el estado ejecuta: docker-compose ps
echo Para reiniciarlos ejecuta: start-containers.bat
echo.

pause

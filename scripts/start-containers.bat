@echo off
echo ====================================
echo Iniciando contenedores Docker
echo ====================================
echo.

REM Verificar si Docker esta corriendo
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta corriendo. Por favor inicia Docker Desktop.
    pause
    exit /b 1
)

echo [INFO] Iniciando todos los contenedores con docker-compose...
docker-compose up -d

if errorlevel 1 (
    echo [ERROR] Fallo al iniciar los contenedores.
    pause
    exit /b 1
)

echo.
echo ====================================
echo Contenedores iniciados exitosamente
echo ====================================
echo.
echo Para ver el estado de los contenedores ejecuta: docker-compose ps
echo Para ver los logs ejecuta: docker-compose logs -f [servicio]
echo.
echo Servicios disponibles:
echo   - Gateway: http://localhost:8080
echo   - Eureka: http://localhost:8099
echo   - Grafana: http://localhost:3000 (admin/admin)
echo   - Prometheus: http://localhost:9090
echo   - Kafka UI: http://localhost:8090
echo   - OAuth Server: http://localhost:9000
echo   - Resource Server: http://localhost:8084
echo   - OAuth Client: http://localhost:8085
echo   - MS-Productos-V2: http://localhost:8086
echo.

pause

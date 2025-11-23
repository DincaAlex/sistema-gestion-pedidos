@echo off
if "%~1"=="" (
    echo Usage: restart-service.bat [service-name]
    echo.
    echo Available services:
    echo   - config-server
    echo   - registry-service
    echo   - gateway-service
    echo   - oauth-server
    echo   - ms-productos
    echo   - ms-productos-v2
    echo   - ms-productos-v3
    echo   - ms-productos-writer
    echo   - ms-pedidos
    exit /b 1
)

echo ================================================
echo Restarting Service: %~1
echo ================================================

cd /d "%~dp0..\..\..\"
docker-compose restart %~1

if %errorlevel% neq 0 (
    echo [ERROR] Failed to restart %~1
    exit /b 1
)

echo.
echo ================================================
echo Service %~1 Restarted Successfully
echo ================================================

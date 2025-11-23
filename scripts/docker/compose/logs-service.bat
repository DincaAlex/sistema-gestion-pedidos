@echo off
if "%~1"=="" (
    echo Usage: logs-service.bat [service-name] [optional: number of lines]
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
    echo.
    echo Example: logs-service.bat ms-productos-v3 100
    exit /b 1
)

cd /d "%~dp0..\..\..\"

if "%~2"=="" (
    docker-compose logs --tail=50 -f %~1
) else (
    docker-compose logs --tail=%~2 -f %~1
)

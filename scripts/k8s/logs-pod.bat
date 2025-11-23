@echo off
if "%~1"=="" (
    echo Usage: logs-pod.bat [service-name] [optional: number of lines]
    echo.
    echo Available services:
    echo   - ms-productos-v3
    echo   - ms-productos-v2
    echo   - ms-pedidos
    echo.
    echo Example: logs-pod.bat ms-productos-v3 100
    exit /b 1
)

set SERVICE_NAME=%~1
set NAMESPACE=practica
set LINES=%~2

if "%LINES%"=="" set LINES=50

echo ================================================
echo Logs for %SERVICE_NAME%
echo ================================================
echo.

kubectl logs -l app=%SERVICE_NAME% -n %NAMESPACE% --tail=%LINES% -f

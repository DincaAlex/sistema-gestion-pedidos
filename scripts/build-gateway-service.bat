@echo off
echo ========================================
echo Building Gateway Service Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t gateway-service:latest ./gateway-service

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Gateway Service image built successfully!
    echo Image name: gateway-service:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build Gateway Service image
    echo ========================================
    exit /b 1
)

pause

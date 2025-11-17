@echo off
echo ========================================
echo Building Registry Service Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t registry-service:latest ./registry-service

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Registry Service image built successfully!
    echo Image name: registry-service:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build Registry Service image
    echo ========================================
    exit /b 1
)

pause

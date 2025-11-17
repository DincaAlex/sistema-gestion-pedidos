@echo off
echo ========================================
echo Building Resource-Server Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t resource-server:latest ./resource-server

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Resource-Server image built successfully!
    echo Image name: resource-server:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build Resource-Server image
    echo ========================================
    exit /b 1
)

pause

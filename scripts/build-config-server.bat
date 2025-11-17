@echo off
echo ========================================
echo Building Config Server Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t config-server:latest ./ms-config-server

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Config Server image built successfully!
    echo Image name: config-server:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build Config Server image
    echo ========================================
    exit /b 1
)

pause

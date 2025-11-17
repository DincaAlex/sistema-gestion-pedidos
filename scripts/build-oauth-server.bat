@echo off
echo ========================================
echo Building OAuth-Server Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t oauth-server:latest ./oauth-server

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo OAuth-Server image built successfully!
    echo Image name: oauth-server:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build OAuth-Server image
    echo ========================================
    exit /b 1
)

pause

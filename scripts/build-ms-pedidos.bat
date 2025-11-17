@echo off
echo ========================================
echo Building MS-Pedidos Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t ms-pedidos:latest ./ms-pedidos

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo MS-Pedidos image built successfully!
    echo Image name: ms-pedidos:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build MS-Pedidos image
    echo ========================================
    exit /b 1
)

pause

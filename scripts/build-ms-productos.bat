@echo off
echo ========================================
echo Building MS-Productos Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t ms-productos:latest ./ms-productos

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo MS-Productos image built successfully!
    echo Image name: ms-productos:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build MS-Productos image
    echo ========================================
    exit /b 1
)

pause

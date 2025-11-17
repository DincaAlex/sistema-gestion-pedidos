@echo off
echo ========================================
echo Building MS-Productos-V2 Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t ms-productos-v2:latest ./ms-productos-v2

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo MS-Productos-V2 image built successfully!
    echo Image name: ms-productos-v2:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build MS-Productos-V2 image
    echo ========================================
    exit /b 1
)

pause

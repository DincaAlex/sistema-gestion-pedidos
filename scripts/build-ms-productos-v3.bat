@echo off
echo ========================================
echo Building MS-Productos-V3 Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t ms-productos-v3:latest ./ms-productos-v3

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo MS-Productos-V3 image built successfully!
    echo Image name: ms-productos-v3:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build MS-Productos-V3 image
    echo ========================================
    exit /b 1
)

pause

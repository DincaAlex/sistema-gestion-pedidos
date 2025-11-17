@echo off
echo ========================================
echo Building MS-Productos-Writer Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t ms-productos-writer:latest ./ms-productos-writer

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo MS-Productos-Writer image built successfully!
    echo Image name: ms-productos-writer:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build MS-Productos-Writer image
    echo ========================================
    exit /b 1
)

pause

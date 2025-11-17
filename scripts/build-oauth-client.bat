@echo off
echo ========================================
echo Building OAuth-Client Docker Image
echo ========================================

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

docker build -t oauth-client:latest ./oauth-client

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo OAuth-Client image built successfully!
    echo Image name: oauth-client:latest
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Failed to build OAuth-Client image
    echo ========================================
    exit /b 1
)

pause

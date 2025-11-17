@echo off
echo ========================================
echo Building All OAuth Services Docker Images
echo ========================================
echo.

REM Cambiar al directorio raiz del proyecto
cd /d "%~dp0.."

echo [1/3] Building OAuth-Server...
docker build -t oauth-server:latest ./oauth-server
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build OAuth-Server
    exit /b 1
)
echo OAuth-Server built successfully!
echo.

echo [2/3] Building Resource-Server...
docker build -t resource-server:latest ./resource-server
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build Resource-Server
    exit /b 1
)
echo Resource-Server built successfully!
echo.

echo [3/3] Building OAuth-Client...
docker build -t oauth-client:latest ./oauth-client
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Failed to build OAuth-Client
    exit /b 1
)
echo OAuth-Client built successfully!
echo.

echo ========================================
echo All OAuth services built successfully!
echo Images created:
echo   - oauth-server:latest
echo   - resource-server:latest
echo   - oauth-client:latest
echo ========================================

pause

@echo off
if "%~1"=="" (
    echo Usage: build-and-deploy.bat [service-name]
    echo.
    echo This script will:
    echo 1. Build the Docker image
    echo 2. Push to Harbor registry
    echo 3. Restart the pod in Kubernetes
    echo.
    echo Available services:
    echo   - ms-productos-v3
    echo   - ms-productos-v2
    echo   - ms-pedidos
    echo.
    echo Example: build-and-deploy.bat ms-productos-v3
    exit /b 1
)

set SERVICE_NAME=%~1

echo ================================================
echo Build and Deploy Pipeline for %SERVICE_NAME%
echo ================================================
echo.

echo [Step 1/3] Building Docker image...
call "%~dp0..\docker\build\individual\build-%SERVICE_NAME%.bat"
if %errorlevel% neq 0 (
    echo [ERROR] Build failed
    exit /b 1
)

echo.
echo [Step 2/3] Pushing to Harbor...
call "%~dp0push-to-harbor.bat" %SERVICE_NAME%
if %errorlevel% neq 0 (
    echo [ERROR] Push to Harbor failed
    exit /b 1
)

echo.
echo [Step 3/3] Restarting pod in Kubernetes...
call "%~dp0restart-pod.bat" %SERVICE_NAME%
if %errorlevel% neq 0 (
    echo [ERROR] Pod restart failed
    exit /b 1
)

echo.
echo ================================================
echo SUCCESS: Complete deployment pipeline finished!
echo ================================================

@echo off
if "%~1"=="" (
    echo Usage: push-to-harbor.bat [image-name]
    echo.
    echo Available images:
    echo   - ms-productos-v3
    echo   - ms-productos-v2
    echo   - ms-pedidos
    echo   - gateway-service
    echo.
    echo Example: push-to-harbor.bat ms-productos-v3
    exit /b 1
)

set IMAGE_NAME=%~1
set HARBOR_HOST=localhost:30002
set HARBOR_PROJECT=library

echo ================================================
echo Pushing %IMAGE_NAME% to Harbor Registry
echo ================================================
echo.

echo [1/3] Tagging image...
docker tag %IMAGE_NAME%:latest %HARBOR_HOST%/%HARBOR_PROJECT%/%IMAGE_NAME%:latest

if %errorlevel% neq 0 (
    echo [ERROR] Failed to tag image
    exit /b 1
)

echo [2/3] Pushing to Harbor...
docker push %HARBOR_HOST%/%HARBOR_PROJECT%/%IMAGE_NAME%:latest

if %errorlevel% neq 0 (
    echo [ERROR] Failed to push image to Harbor
    exit /b 1
)

echo [3/3] Verifying push...
echo.
echo ================================================
echo SUCCESS: Image pushed to Harbor
echo ================================================
echo Image: %HARBOR_HOST%/%HARBOR_PROJECT%/%IMAGE_NAME%:latest
echo ================================================

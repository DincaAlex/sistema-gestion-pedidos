@echo off
if "%~1"=="" (
    echo Usage: delete-pod.bat [service-name]
    echo.
    echo Available services:
    echo   - ms-productos-v3
    echo   - ms-productos-v2
    echo   - ms-pedidos
    echo.
    echo Example: delete-pod.bat ms-productos-v3
    exit /b 1
)

set SERVICE_NAME=%~1
set NAMESPACE=practica

echo ================================================
echo WARNING: This will delete %SERVICE_NAME%
echo ================================================
echo.
set /p CONFIRM="Are you sure? (y/n): "

if /i not "%CONFIRM%"=="y" (
    echo Operation cancelled
    exit /b 0
)

echo.
echo Deleting deployment...
kubectl delete deployment %SERVICE_NAME% -n %NAMESPACE%

if %errorlevel% neq 0 (
    echo [ERROR] Failed to delete deployment
    exit /b 1
)

echo.
echo ================================================
echo SUCCESS: %SERVICE_NAME% deleted successfully
echo ================================================

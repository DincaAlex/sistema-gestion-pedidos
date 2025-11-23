@echo off
if "%~1"=="" (
    echo Usage: restart-pod.bat [service-name]
    echo.
    echo Available services:
    echo   - ms-productos-v3
    echo   - ms-productos-v2
    echo   - ms-pedidos
    echo.
    echo Example: restart-pod.bat ms-productos-v3
    exit /b 1
)

set SERVICE_NAME=%~1
set NAMESPACE=practica

echo ================================================
echo Restarting %SERVICE_NAME% in Kubernetes
echo ================================================
echo.

kubectl rollout restart deployment/%SERVICE_NAME% -n %NAMESPACE%

if %errorlevel% neq 0 (
    echo [ERROR] Failed to restart deployment
    exit /b 1
)

echo Waiting for rollout to complete...
kubectl rollout status deployment/%SERVICE_NAME% -n %NAMESPACE% --timeout=120s

if %errorlevel% neq 0 (
    echo [ERROR] Rollout failed
    exit /b 1
)

echo.
echo ================================================
echo SUCCESS: %SERVICE_NAME% restarted successfully
echo ================================================
kubectl get pods -n %NAMESPACE% -l app=%SERVICE_NAME%
echo ================================================

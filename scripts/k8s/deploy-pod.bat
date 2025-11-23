@echo off
if "%~1"=="" (
    echo Usage: deploy-pod.bat [service-name]
    echo.
    echo Available services:
    echo   - ms-productos-v3
    echo   - ms-productos-v2
    echo   - ms-pedidos
    echo.
    echo Example: deploy-pod.bat ms-productos-v3
    exit /b 1
)

set SERVICE_NAME=%~1
set NAMESPACE=practica
set K8S_DIR=%~dp0..\..\k8s

echo ================================================
echo Deploying %SERVICE_NAME% to Kubernetes
echo ================================================
echo.

if not exist "%K8S_DIR%\%SERVICE_NAME%-deployment.yaml" (
    echo [ERROR] Deployment file not found: %K8S_DIR%\%SERVICE_NAME%-deployment.yaml
    exit /b 1
)

echo [1/2] Applying deployment...
kubectl apply -f "%K8S_DIR%\%SERVICE_NAME%-deployment.yaml" -n %NAMESPACE%

if %errorlevel% neq 0 (
    echo [ERROR] Failed to apply deployment
    exit /b 1
)

echo [2/2] Waiting for pod to be ready...
kubectl rollout status deployment/%SERVICE_NAME% -n %NAMESPACE% --timeout=120s

if %errorlevel% neq 0 (
    echo [ERROR] Deployment rollout failed
    exit /b 1
)

echo.
echo ================================================
echo SUCCESS: %SERVICE_NAME% deployed successfully
echo ================================================
kubectl get pods -n %NAMESPACE% -l app=%SERVICE_NAME%
echo ================================================

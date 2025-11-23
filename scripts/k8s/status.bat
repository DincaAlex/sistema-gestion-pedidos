@echo off
set NAMESPACE=practica

echo ================================================
echo Kubernetes Cluster Status
echo ================================================
echo.

echo [Namespace: %NAMESPACE%]
echo.

echo --- Deployments ---
kubectl get deployments -n %NAMESPACE%
echo.

echo --- Pods ---
kubectl get pods -n %NAMESPACE%
echo.

echo --- Services ---
kubectl get services -n %NAMESPACE%
echo.

echo ================================================

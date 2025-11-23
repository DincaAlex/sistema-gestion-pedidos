@echo off
echo ================================================
echo Starting All Services with Docker Compose
echo ================================================

cd /d "%~dp0..\..\..\"
docker-compose up -d

if %errorlevel% neq 0 (
    echo [ERROR] Failed to start services
    exit /b 1
)

echo.
echo ================================================
echo All Services Started Successfully
echo ================================================
echo.
echo Access points:
echo - Gateway:           http://localhost:8080
echo - Config Server:     http://localhost:8888
echo - Eureka Registry:   http://localhost:8099
echo - OAuth Server:      http://localhost:9000
echo - Grafana:           http://localhost:3000
echo - Prometheus:        http://localhost:9090
echo ================================================

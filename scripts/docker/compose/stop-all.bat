@echo off
echo ================================================
echo Stopping All Services
echo ================================================

cd /d "%~dp0..\..\..\"
docker-compose stop

if %errorlevel% neq 0 (
    echo [ERROR] Failed to stop services
    exit /b 1
)

echo.
echo ================================================
echo All Services Stopped Successfully
echo ================================================
